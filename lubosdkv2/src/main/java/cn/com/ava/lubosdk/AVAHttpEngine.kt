package cn.com.ava.lubosdk

import android.text.TextUtils
import android.util.Log
import cn.com.ava.lubosdk.entity.Login
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.eventbus.KeyInvalidEvent
import cn.com.ava.lubosdk.http.AVAServerService
import cn.com.ava.lubosdk.http.Config
import cn.com.ava.lubosdk.manager.LoginManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.*
import okhttp3.EventListener
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * AVA录播Http请求引擎
 * */
object AVAHttpEngine {

    const val TAG = "AVAHttpEngine"

    private val mDurationFlow: Flow<Long> = flow {
        var i = 0L
        while (true) {
            delay(1000)
            emit(i)
            i++
            if (i == 3600L) i = 0
        }
    }

    private var mEngineJob: Job? = null

    private val mQueries: ArrayBlockingQueue<IQuery<*>> by lazy {
        ArrayBlockingQueue<IQuery<*>>(20)
    }

    private var isInit: Boolean = false

    private var mNetErrorListener: LuBoSDK.NetErrorListener? = null

    private lateinit var mService: AVAServerService

    private val mOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            readTimeout(Config.TIMEOUT_READ, TimeUnit.MILLISECONDS)
            writeTimeout(Config.TIMEOUT_WRITE, TimeUnit.MILLISECONDS)
            connectTimeout(Config.TIMEOUT_CONNECT, TimeUnit.MILLISECONDS)
            eventListener(object : EventListener() {
                override fun callFailed(call: Call, ioe: IOException) {
                    if (ioe is SocketTimeoutException) {
                        mNetErrorListener?.onTimeoutError()
                    } else if (ioe is ConnectException) {
                        mNetErrorListener?.onConnectFailedError()
                    }else{
                        mNetErrorListener?.onUnknownError()
                    }

                }
            })
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
            cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {

                }

                override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                    val login: Login? = LoginManager.getLogin()
                    return if (login != null && login.isLoginSuccess() && !TextUtils.isEmpty(login.getToken())) {
                        val token: String = login.getToken()
                        val cookie = Cookie.Builder().domain(url.host()).name("AVA_A4S_JWT_TOKEN").value(token).build()
                        val cookies: MutableList<Cookie> = ArrayList()
                        cookies.add(cookie)
                        cookies
                    } else {
                        ArrayList()
                    }
                }
            })
        }.build()
    }


    /**
     * 初始化
     * @param serverHost 服务器host(格式：{ip}:{port})，不带http
     */
    fun init(serverHost: String) {
        val retrofit = Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://$serverHost/")
                .build()
        mService = retrofit.create(AVAServerService::class.java)
        isInit = true
    }

    fun getHttpService(): AVAServerService {
        checkInit()
        return mService
    }

    fun release() {
        isInit = false
    }

    /**
     * 添加请求队列
     * @param query
     * */
    fun addQueryCommand(query: IQuery<*>) {
        checkInit()
        synchronized(this) {
            Log.d(TAG, "addQueryCommand: ${Thread.currentThread().name}")
            mQueries.add(query)
        }
    }

    /**
     * 执行特殊信息请求
     *
     * */
    fun requestSPQuery(query: ISPQuery<*>, resultOnMain: Boolean = true) {
        checkInit()
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "begin requestSPQuery: ${Thread.currentThread().name}")
            val call = mService.httpCommand(query.getQueryParams())
            var response: Response<ResponseBody>? = null
            try {
                response = call.execute()
                if (response.isSuccessful) {
                    var build: QueryResult? = null
                    build = query.build(response.body()?.string()?.trim() ?: "")
                    resultOnMain(resultOnMain) {
                        query.onResult(build!!)
                    }
                } else {
                    resultOnMain(resultOnMain) {
                        query.onError?.invoke(HttpException(response))
                    }
                }
            } catch (e: Exception) {
                resultOnMain(resultOnMain) {
                    query.onError?.invoke(e)
                }
                return@launch
            }
        }
    }

    fun requestRecordFile(query: ISPQuery<*>, resultOnMain: Boolean = true){
        checkInit()
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "begin requestSPQuery: ${Thread.currentThread().name}")
            val call = mService.getFileInfoCommand(query.getQueryParams())
            var response: Response<ResponseBody>? = null
            try {
                response = call.execute()
                if (response.isSuccessful) {
                    var build: QueryResult? = null
                    build = query.build(response.body()?.string()?.trim() ?: "")
                    resultOnMain(resultOnMain) {
                        query.onResult(build!!)
                    }
                } else {
                    resultOnMain(resultOnMain) {
                        query.onError?.invoke(HttpException(response))
                    }
                }
            } catch (e: Exception) {
                resultOnMain(resultOnMain) {
                    query.onError?.invoke(e)
                }
                return@launch
            }

        }
    }

    /**
     * 下载文件
     * */
    fun requestDownloadFile(query: IDownloadQuery, resultOnMain: Boolean = false) {
        checkInit()
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "begin download query: ${Thread.currentThread().name}")
            val call = mService.httpCommand(query.getQueryParams())
            var response: Response<ResponseBody>? = null
            try {
                response = call.execute()
                if (response.isSuccessful) {
                    response.body()?.apply {
                        val result = query.build(this)
                        resultOnMain(resultOnMain) {
                            query.onResult(result)
                        }
                    } ?: resultOnMain(resultOnMain) {
                        query.onResult(false)
                    }
                } else {
                    resultOnMain(resultOnMain) {
                        query.onError?.invoke(HttpException(response))
                    }
                }
            } catch (e: Exception) {
                resultOnMain(resultOnMain) {
                    query.onError?.invoke(e)
                }
                return@launch
            }
        }
    }

    /**
     * 执行控制指令
     * @param control
     * @param resultOnMain 结果回调是否运行在主线程
     * @param isEncode http请求是否编码
     * */
    fun requestControl(control: IControl, resultOnMain: Boolean = true, isEncode: Boolean = false) {
        checkInit()
        checkLogin()
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "requestControl#${Thread.currentThread().name}:${control.name}")
            val call = if (!isEncode) mService.httpCommand(control.getControlParams())
            else mService.httpCommandWithEncode(control.getControlParams())
            var response: Response<ResponseBody>? = null
            try {
                response = call.execute()
                if (response.isSuccessful) {
                    var result: Boolean = false
                    val body = response.body()?.string()?.trim() ?: ""
                    //检查录播key权限
                    if (checkKeyInvalid(body)) {
                        EventBus.getDefault().post(KeyInvalidEvent())
                        resultOnMain(resultOnMain) {
                            control.onError?.invoke(KeyInvalidException())
                        }
                        return@launch
                    }
                    result = control.build(body)
                    resultOnMain(resultOnMain) {
                        control.onResult(result)
                    }
                } else {
                    resultOnMain(resultOnMain) {
                        control.onError?.invoke(IllegalStateException())
                    }
                }
            } catch (e: Exception) {
                resultOnMain(resultOnMain) {
                    control.onError?.invoke(e)
                }
                return@launch
            }
        }
    }

    /**
     * 开启可合并http请求引擎
     * */
    fun startQueryEngine() {
        checkInit()
        mEngineJob = GlobalScope.launch {
            Log.d(TAG, "Launch engine:${Thread.currentThread().name} ")
            mDurationFlow.collect { i ->
                if (mQueries.isNotEmpty()) {
                    Log.d(TAG, "Flow get:${Thread.currentThread().name} ")
                    val readyList = mutableListOf<IQuery<*>>()
                    synchronized(this) {
                        val iterator = mQueries.iterator()
                        while (iterator.hasNext()) {
                            val query = iterator.next()
                            iterator.remove()
                            readyList.add(query)
                        }
                    }
                    //录播最大请求字段数是191，所以多的话需要分开
                    val groupSend = mutableListOf<List<IQuery<*>>>()
                    var accumulateCount: Int = 0
                    val groupIndexs = mutableListOf(0)
                    readyList.forEachIndexed { i, query ->
                        val size = query.resultSize()
                        if (accumulateCount + size > 190) {   //超出个数
                            groupIndexs.add(i)
                            accumulateCount = 0
                        }
                        accumulateCount += size
                    }
                    val indexSize = groupIndexs.size
                    for (i in 0 until indexSize - 1) {
                        groupSend.add(readyList.subList(groupIndexs[i], groupIndexs[i + 1]))
                    }
                    groupSend.add(
                            readyList.subList(
                                    groupIndexs[groupIndexs.lastIndex],
                                    readyList.size
                            )
                    )
                    groupSend.forEach {
                        launch(Dispatchers.IO) {
                            try {
                                Log.d(TAG, "HttpSend :${Thread.currentThread().name}")
                                synchronized(this){   //TODO 新增的测试这样是否稳定
                                    httpQuerySend(it)
                                }
                            } catch (e: Exception) {
                                //IO错误
                                it.filter { !it.isResume }.forEach {
                                    it.isResume = true
                                    it.onError?.invoke(e)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun httpQuerySend(
            readyList: List<IQuery<*>>
    ) {
        val params = linkedMapOf<String, String>()
        params.put("action", "2")
        var count = 0
        val iterator = readyList.iterator()
        count = readyList.asSequence().sumBy {
            it.resultSize()
        }
        //容错代码
        if(count<=0)return
        params.put("cmd", count.toString())
        params.put("key", "")
        var pIndex = 0
        var offset = 0
        while (iterator.hasNext()) {
            val query = iterator.next()
            query.mQueryFields.forEach {
                params.put("p${pIndex++}", it.toString())
            }
            query.offsets = offset
            offset += query.resultSize()
        }
        params.put("p1000", 1000.toString())
        mService.httpCommand(params).apply {
            try {
                val response = execute()
                if (response.isSuccessful) {
                    val string = response.body()?.string()?.trim()
                    if (TextUtils.isEmpty(string) || string == null) {
                    } else {
                        val split = string.split("|").dropLastWhile { TextUtils.isEmpty(it) }
                        runBlocking {
                            readyList.forEach { query ->
                                //每个请求开一个协程
                                launch {
                                    val offsets = query.offsets
                                    val resultSize = query.resultSize()
                                    val subList: MutableList<String> =
                                            split.subList(offsets, offsets + resultSize).toMutableList()
                                    if (query.isNeedSystemTime()) {
                                        subList.add(split[split.lastIndex])
                                    }
                                    var build: QueryResult? = null
                                    val measureTimeMillis = measureTimeMillis {
                                        try {
                                            build = query.build(subList)
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Build ${query.name} failed")
                                            //解析失败的异常
                                            e.printStackTrace()
                                        }
                                    }
                                    query.isResume = true
                                    build?.apply {
                                        Log.d(
                                                TAG,
                                                "Build ${query.name} consume:$measureTimeMillis ms"
                                        )
                                        query.onResult(this)
                                    } ?: query.onError?.invoke(java.lang.IllegalStateException("Build ${query.name} Failed."))
                                }
                            }
                        }
                    }
                } else {
                    readyList.filter { !it.isResume }.forEach {
                        it.isResume = true
                        it.onError?.invoke(HttpException(response))
                    }
                    // it.resumeWith(Result.failure(IllegalAccessException()))
                }
            } catch (e: Exception) {
                readyList.filter { !it.isResume }.forEach {
                    it.isResume = true
                    it.onError?.invoke(e)
                }
            }
        }
    }


    private fun checkLogin() {
        if (!LoginManager.isLogin()) throw IllegalAccessException("Must be login")
    }

    private fun checkInit() {
        if (!isInit) throw IllegalStateException("QueryEngine has not init.")
    }


    /**
     * 判断请求key是否失效
     */
    private fun checkKeyInvalid(response: String): Boolean {
        return response.trim { it <= ' ' }.startsWith("-2") || response.contains("jwt_check:-2")
    }

    /**关闭引擎*/
    fun stopQueryEngine() {
        checkInit()
        runBlocking {
            mEngineJob?.cancelAndJoin()
        }
    }


    /**
     * 回调是否在主线程
     * */
    suspend fun resultOnMain(isResultOnMain: Boolean, block: () -> Unit) {
        if (isResultOnMain) {
            withContext(Dispatchers.Main) {
                block.invoke()
            }
        } else {
            block.invoke()
        }
    }

    fun setNetErrorListener(listener: LuBoSDK.NetErrorListener?) {
        mNetErrorListener = listener
    }


}