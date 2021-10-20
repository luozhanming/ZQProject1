package cn.com.ava.lubosdk.manager

import android.text.TextUtils
import android.util.Log
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.entity.Login
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.entity.RSAKey
import cn.com.ava.lubosdk.query.LuboInfoQuery
import cn.com.ava.lubosdk.spquery.LoginSPQuery
import cn.com.ava.lubosdk.spquery.RSAKeyQuery
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object LoginManager {

    const val TAG = "LoginManager"

    private var mLogin: Login? = Login()

    /**
     * 登录
     */
    fun login(
        username: String,
        password: String
    ): Observable<Login> {
        return Observable.create { emitter ->
            Log.d(
                TAG,
                "login(${Thread.currentThread().name}): username=$username;password=$password"
            )
            runBlocking {
                val login = suspendCoroutine<Login> {
                    Log.d(TAG, "begin requestSPQuery(${Thread.currentThread().name})")
                    AVAHttpEngine.requestSPQuery(
                        LoginSPQuery(
                            username,
                            password,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as Login))
                            },
                            onError = { throwable ->
                                Log.d(
                                    TAG,
                                    "onError: requestSPQuery(${Thread.currentThread().name})"
                                )
                                it.resumeWithException(throwable)
                            })
                    )

                }
                val luboInfo = suspendCoroutine<LuBoInfo> {
                    AVAHttpEngine.addQueryCommand(LuboInfoQuery(
                        onResult = {queryResult ->
                            it.resumeWith(Result.success(queryResult as LuBoInfo))
                        }, onError = {throwable ->
                            it.resumeWithException(throwable)
                        }
                    ))
                }
                mLogin = login
                mLogin?.rserverInfo = luboInfo.stun
                emitter.onNext(mLogin?:login)
            }
        }
    }

    fun newLogin(username: String, password: String): Observable<Login> {
        Log.d(TAG, "begin loginEx: ")
        return Observable.create { emitter ->
            runBlocking {
                val rsaKey = suspendCoroutine<RSAKey> {
                    AVAHttpEngine.requestSPQuery(RSAKeyQuery(
                        onResult = { queryResult ->
                            it.resumeWith(Result.success(queryResult as RSAKey))
                        },
                        onError = { throwable ->
                            it.resumeWithException(throwable)
                        }
                    ))
                }
                val isEx = !TextUtils.isEmpty(rsaKey.modulus)
                Log.d(TAG, "loginEx: getRSAKey ${rsaKey.toString()}")
                val login = suspendCoroutine<Login> {
                    Log.d(TAG, "begin requestSPQuery(${Thread.currentThread().name})")
                    AVAHttpEngine.requestSPQuery(
                        LoginSPQuery(
                            username,
                            password,
                            isEx,
                            rsaKey = rsaKey,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as Login))
                            },
                            onError = { throwable ->
                                Log.d(
                                    TAG,
                                    "onError: requestSPQuery(${Thread.currentThread().name})"
                                )
                                it.resumeWithException(throwable)
                            })
                    )
                }

                Log.d(TAG, "loginEx: getLogin ${login.toString()}")
                if(login.isLoginSuccess&&!login.isSleep){
                    val luboInfo = suspendCoroutine<LuBoInfo> {
                        AVAHttpEngine.addQueryCommand(LuboInfoQuery(
                            onResult = {queryResult ->
                                it.resumeWith(Result.success(queryResult as LuBoInfo))
                            }, onError = {throwable ->
                                it.resumeWithException(throwable)
                            }
                        ))
                    }
                    mLogin = login
                    mLogin?.rserverInfo = luboInfo.stun
                }else if(login.isLoginSuccess){
                    mLogin = login
                }

                emitter.onNext(mLogin?:login)

            }
        }
    }


    fun getLogin(): Login? {
        return mLogin
    }

    /**
     * 是否已登录
     */
    fun isLogin(): Boolean {
        return mLogin != null && !TextUtils.isEmpty(mLogin?.key)
    }

    /**
     * 登出
     */
    fun logout() {
        mLogin = null
    }


}