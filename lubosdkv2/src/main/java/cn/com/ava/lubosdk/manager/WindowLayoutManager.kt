package cn.com.ava.lubosdk.manager

import androidx.annotation.IntRange
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.control.*
import cn.com.ava.lubosdk.entity.*
import cn.com.ava.lubosdk.query.ComputerIndexQuery
import cn.com.ava.lubosdk.query.CurPreviewWindowQuery
import cn.com.ava.lubosdk.query.LayoutButtonInfoQuery
import cn.com.ava.lubosdk.query.PreviewWindowInfoQuery
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * 录播窗口视频相关
 * */
object WindowLayoutManager {
    /**
     * 开启关闭抠像
     */
    fun setVideoImageMat(
        windowIndex: Int,
        isOn: Boolean
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetImageMatControl(
                            windowIndex,
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
     * 设置视频源布局窗口
     */
    fun setPreviewLayout(layout: String): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetPreviewLayoutControl(
                            layout,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置预置位
     */
    fun setVideoPresetPos(
        window: PreviewVideoWindow,
        presetIndex: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetVideoPresetPosControl(
                            window,
                            presetIndex,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 保存预置位
     * @param idx 窗口的idx
     * @param presetIndex 位置
     */
    fun saveVideoPresetPos(
        idx: Int,
        presetIndex: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SaveVideoPresetPosControl(
                            idx,
                            presetIndex,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置云台焦距
     *
     * @param focusLevel 0/250/500/750
     */
    fun setPresetFocus(
        window: PreviewVideoWindow,
        @Constant.FocusLevel focusLevel: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetPresetFocusControl(
                            window.ptzIdx,
                            focusLevel,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 云台变焦开始
     */
    fun beginCamZoom(
        window: PreviewVideoWindow,
        @Constant.ZoomOrientation zoomOrientation: Int,
        speed: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        BeginCamZoomControl(
                            window.ptzIdx,
                            zoomOrientation,
                            speed,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * *云台结束变焦
     *
     * @param window
     */
    fun finishCloudPlatformZoom(window: PreviewVideoWindow): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        FinishCamZoomControl(
                            window.ptzIdx,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 开始控制云台方向
     *
     * @param window      窗口实体
     * @param orientation 方向 (0左/1上/2下/3右)
     * @param pspeed      灵敏度左边数值(1-24)
     * @param tspeed      灵敏度右边数值(1_20)
     */
    fun beginSetCloudPlatformOrientation(
        window: PreviewVideoWindow,
        orientation: Int,
        @IntRange(from = 1, to = 24) pspeed: Int,
        @IntRange(from = 1, to = 20) tspeed: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        BeginSetCamOrientationControl(
                            window.ptzIdx,
                            orientation,
                            pspeed,
                            tspeed,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 结束控制云台方向
     *
     * @param window      窗口实体
     * @param orientation 方向 (0左/1上/2下/3右)
     * @param pspeed      灵敏度左边数值(1-24)
     * @param tspeed      灵敏度右边数值(1_20)
     */
    fun finishSetCloudPlatformOrientation(
        window: PreviewVideoWindow,
        orientation: Int,
        pspeed: Int,
        tspeed: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        FinishSetCamOrientationControl(
                            window.ptzIdx,
                            orientation,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 对有多个视频源的窗口进行选择
     * @param window
     * @param sourceIndex 视频源序号
     */
    fun setWindowSource(
        window: PreviewVideoWindow,
        sourceCmd: String
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetWindowSourceControl(
                            window.index,
                                sourceCmd,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                        , isEncode = true
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 获取电脑所在窗口索引
     */
    fun getComputerIndex(): Observable<String> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ComputerIndex> {
                    AVAHttpEngine.addQueryCommand(ComputerIndexQuery(
                        onResult = { queryResult ->
                            it.resumeWith(Result.success(queryResult as ComputerIndex))
                        },
                        onError = { throwable ->
                            it.resumeWithException(throwable)
                        }
                    ))
                }
                emitter.onNext(info.index)
            }
        }
    }


    /**
     * 获取布局按钮列表
     */
    fun getLayoutButtonInfo(): Observable<List<LayoutButtonInfo>> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListWrapper<LayoutButtonInfo>> {
                    AVAHttpEngine.addQueryCommand(LayoutButtonInfoQuery(
                        onResult = { queryResult ->
                            it.resumeWith(Result.success(queryResult as ListWrapper<LayoutButtonInfo>))
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
     * 获取当前预览窗口
     */
    fun getCurPreviewWindow(): Observable<String> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<CurPreviewWindow> {
                    AVAHttpEngine.addQueryCommand(CurPreviewWindowQuery(
                        onResult = { queryResult ->
                            it.resumeWith(Result.success(queryResult as CurPreviewWindow))
                        },
                        onError = { throwable ->
                            it.resumeWithException(throwable)
                        }
                    ))
                }
                emitter.onNext(info.curPreview)
            }
        }
    }

    /**
     * 获取所有可切换预览窗口
     * */
    fun getPreviewWindowInfo(): Observable<List<PreviewVideoWindow>> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListWrapper<PreviewVideoWindow>> {
                    AVAHttpEngine.addQueryCommand(PreviewWindowInfoQuery(
                        onResult = { queryResult ->
                            it.resumeWith(Result.success(queryResult as ListWrapper<PreviewVideoWindow>))
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
     * 抠像序号是否翻转
     */
    fun isMatReverse(): Boolean {
        return PreviewWindowInfoQuery.keylocate[0] > PreviewWindowInfoQuery.keylocate[1]
    }


}