package cn.com.ava.lubosdk.manager

import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.control.ReloadMachineControl
import cn.com.ava.lubosdk.control.SleepMachineControl
import cn.com.ava.lubosdk.control.TurnOffMachineControl
import cn.com.ava.lubosdk.control.WakeUpMachineControl
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object PowerManager {


    /**
     * 重启机器
     */
    fun reloadMachine(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        ReloadMachineControl(
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 休眠机器
     */
    fun sleepMachine(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SleepMachineControl(
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 关机
     */
    fun turnOffMachine(): Observable<Boolean?>? {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        TurnOffMachineControl(
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 唤醒机器
     */
    fun wakeupMachine(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        WakeUpMachineControl(
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }
}