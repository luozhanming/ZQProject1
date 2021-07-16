package cn.com.ava.lubosdk.manager

import android.util.Log
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.control.*
import cn.com.ava.lubosdk.entity.*
import cn.com.ava.lubosdk.query.*
import cn.com.ava.lubosdk.spquery.IPv4NetConfigurationQuery
import io.reactivex.Observable
import io.reactivex.functions.Function
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object GeneralManager {

    /**
     * 获得机器信息
     */
    fun getMachineInfo(): Observable<MachineInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<MachineInfo> {
                    Log.d(RecordManager.TAG, "getRecordInfo: ${Thread.currentThread().name}")
                    AVAHttpEngine.addQueryCommand(MachineInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as MachineInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 获取录播基本信息（注册，机身信息等）
     */
    fun getLuboInfo(): Observable<LuBoInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCancellableCoroutine<LuBoInfo> {
                    AVAHttpEngine.addQueryCommand(LuboInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as LuBoInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 获得抠像信息
     */
    fun getImageMatInfo(): Observable<ImageMatInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ImageMatInfo> {
                    AVAHttpEngine.addQueryCommand(ImageMatQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ImageMatInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 保存抠像参数
     */
    fun saveImageMat(
            info: ImageMatInfo,
            index: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SaveImageMatSettingControl(
                                    info,
                                    index,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 获取字幕初始信息
     */
    fun getCaptionInfo(): Observable<CaptionInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<CaptionInfo> {
                    AVAHttpEngine.addQueryCommand(CaptionInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as CaptionInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 保存抠像参数
     */
    fun showCaption(
            isShow: Boolean
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            ShowCaptionControl(
                                    isShow,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 设置是否滚动字幕
     */
    fun rollCaption(isRoll: Boolean): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            RollCaptionControl(
                                    isRoll,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) }),
                            isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 设置字幕字
     */
    fun setCaptionText(text: String): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SetCaptionTextControl(
                                    text,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                            , isEncode = true)
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 获取音量初始化信息
     */
    fun getVolumeChannelInfoV2(): Observable<List<VolumeChannel>> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListWrapper<VolumeChannel>> {
                    AVAHttpEngine.addQueryCommand(VolumeChannelsQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ListWrapper<VolumeChannel>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.datas)
            }
        }
    }


    /**
     * 设置主音量控制
     */
    fun setMasterChannelVolume(level: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            MasterVolumnControl(
                                    level,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 设置会议模式时音量控制
     * */
    fun setMeetingMasterChannelVolume(level: Int, extVolumeInfo: String): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            MasterVolumnControl(
                                    level,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 设置其他音量
     * @param channels 修改的音量通道(channelName=>newChannelLevel)新值
     */
    fun setOtherChannelVolume(
            channels: List<VolumeChannel>
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            OtherChannelVolumeControl(
                                    channels,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 获取logo信息
     */
    fun getLogoInfo(): Observable<LogoInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<LogoInfo> {
                    AVAHttpEngine.addQueryCommand(LogoInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as LogoInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 设置logo是否显示
     */
    fun setLogoVisible(
            info: LogoInfo,
            isVisible: Boolean
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            LogoVisibleControl(
                                    info,
                                    isVisible,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 设置logo位置
     */
    fun setLogoPosition(x: Int, y: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            LogoPositionControl(
                                    x,
                                    y,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 能否进入互动界面
     */
    fun canEnterInteraction(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Boolean>> {
                    AVAHttpEngine.addQueryCommand(CanEnterInteractionQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Boolean>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 能否进入录课
     */
    fun canEnterRecordClass(): Observable<Boolean?>? {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Boolean>> {
                    AVAHttpEngine.addQueryCommand(CanEnterRecordQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Boolean>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 自由扩展接口调用
     */
    fun <T> commonInterface(
            params: Map<String, String>,
            convert: Function<ResponseBody, T>
    ): Observable<T> {
        val param: MutableMap<String, String> =
                LinkedHashMap()
        param["action"] = "2"
        param["cmd"] = params.size.toString() + ""
        param["key"] = LoginManager.getLogin()?.key ?: ""
        param.putAll(params)
        return Observable.create<T> { emitter ->
            runBlocking {
                val call = AVAHttpEngine.getHttpService().httpCommand(params)
                val response = call.execute()
                val convert = convert.apply(response.body()!!)
                emitter.onNext(convert)
            }
        }
    }


    /**
     * 上传logo文件
     *
     * @param file 文件
     */
    fun uploadLogoFile(file: File): Observable<Boolean> {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart(
                "file", file.name,
                RequestBody.create(MediaType.parse("multipart/octet-stream"), file)
        )
        return Observable.create<Boolean> { emitter ->
            runBlocking {
                val call = AVAHttpEngine.getHttpService().uploadFile(builder.build())
                val response = call.execute()
                emitter.onNext(response.isSuccessful)
            }
        }
    }


    /**
     * 获取系统时间
     */
    fun getLuboSystemTime(): Observable<Long> {
        return Observable.create { emitter ->
            val url = URL(LuBoSDK.getHost())
            val uc = url.openConnection() as HttpURLConnection
            uc.connect()
            val date = uc.date
            emitter.onNext(date)
            uc.disconnect()
        }
    }

    /**
     * 获取IPv4网络设置信息
     * */
    fun getIpv4Configuration(): Observable<IPv4NetConfiguration> {
        return Observable.create { emitter ->
            runBlocking {
                val configuration = suspendCoroutine<IPv4NetConfiguration> {
                    AVAHttpEngine.requestSPQuery(IPv4NetConfigurationQuery(
                            onResult = { queryResult -> it.resumeWith(Result.success(queryResult as IPv4NetConfiguration)) },
                            onError = { throwable -> it.resumeWithException(throwable) }
                    ))
                }
                emitter.onNext(configuration)
            }
        }
    }

    /**
     * 设置IPv4网络
     * */
    fun setIPv4Configuration(ip: String, mask: String, gateway: String, macAddress: String,
                             dns: String, backupDNS: String, mtu: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(SetIpv4ConfigurationControl(
                            ip, mask, gateway, macAddress, dns, backupDNS, mtu,
                            onResult = { result -> it.resumeWith(Result.success(result)) },
                            onError = { throwable -> it.resumeWithException(throwable) }
                    ),isEncode = true)
                }
                emitter.onNext(result)
            }
        }
    }


}