package cn.com.ava.lubosdk.manager

import android.util.Log
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.control.*
import cn.com.ava.lubosdk.entity.EffectInfo
import cn.com.ava.lubosdk.entity.RecordInfo
import cn.com.ava.lubosdk.query.EffectInfoQuery
import cn.com.ava.lubosdk.query.RecordInfoQuery
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object RecordManager {


    const val TAG = "RecordManager"


    fun getRecordInfo(): Observable<RecordInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<RecordInfo> {
                    Log.d(TAG, "getRecordInfo: ${Thread.currentThread().name}")

                    AVAHttpEngine.addQueryCommand(RecordInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as RecordInfo))
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


    fun getEffectInfo(): Observable<EffectInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<EffectInfo> {
                    Log.d(TAG, "getEffectInfo: ${Thread.currentThread().name}")
                    AVAHttpEngine.addQueryCommand(EffectInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as EffectInfo))
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
     * 设置云台跟踪模式
     */
    fun setCamTrackMode(@Constant.CamTrackMode trackMode: String): Observable<Boolean> {
        val trackAdapt = when(trackMode){
            Constant.TRACK_FULL_AUTO->NewAutoTrackControl.AUTO
            Constant.TRACK_MID_AUTO->NewAutoTrackControl.MID
            Constant.TRACK_HAND->NewAutoTrackControl.HAND
            else->-1
        }
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            NewAutoTrackControl(
                                    trackAdapt,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 控制录制
     */
    fun controlRecord(@Constant.RecordState state: Int): Observable<Boolean> {
        val stateAdapt = when (state) {
            Constant.RECORD_RECORDING -> NewRecordSwitchControl.START
            Constant.RECORD_PAUSE -> NewRecordSwitchControl.PAUSE
            Constant.RECORD_RESUME -> NewRecordSwitchControl.RESUME
            Constant.RECORD_STOP -> NewRecordSwitchControl.STOP
            else -> -1
        }
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            NewRecordSwitchControl(
                                    stateAdapt,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 控制直播开关
     */
    fun controlLiving(isOn: Boolean): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            NewLiveControl(
                                    isOn,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 全自动下VGA电脑画面锁定
     */
    fun vgaLock(
            isOn: Boolean,
            username: String,
            password: String
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            VGALockControl(
                                    isOn,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置课程信息
     *
     * @param theme   课程名
     * @param teacher 主讲人
     */
    fun setClassInfo(
            theme: String,
            teacher: String
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SetClassInfoControl(
                                    theme, teacher,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * VGA校对
     */
    fun proofreadVGA(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            ProofVGAControl(
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置特技时间
     *
     * @param time 单位毫秒
     */
    fun setEffectTime(time: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SetEffectTimeControl(
                                    time,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置当前特技
     *
     * @param stunt 特技名
     */
    fun setCurEffect(stunt: String): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SetCurEffectControl(
                                    stunt,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


}