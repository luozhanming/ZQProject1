package cn.com.ava.lubosdk.manager

import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.control.whiteboard.*
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.whiteboard.BlankBoardState
import cn.com.ava.lubosdk.entity.whiteboard.BoardClassDataState
import cn.com.ava.lubosdk.entity.whiteboard.ClassroomControlEntity
import cn.com.ava.lubosdk.entity.whiteboard.PageInfo
import cn.com.ava.lubosdk.query.DownloadBgFileQuery
import cn.com.ava.lubosdk.query.DownloadTrailFileQuery
import cn.com.ava.lubosdk.spquery.whiteboard.BlankBoardStateQuery
import cn.com.ava.lubosdk.spquery.whiteboard.BoardClassDataQuery
import cn.com.ava.lubosdk.spquery.whiteboard.SwitchPageQueryControl
import cn.com.ava.lubosdk.spquery.whiteboard.WhiteBoardCurOutputQuery
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import java.lang.Exception
import java.util.*
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object WhiteBoardManager {


    /**
     * 获取录播白板状态
     * */
    fun getWhiteBoardState(): Observable<BlankBoardState> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<BlankBoardState> {
                    AVAHttpEngine.requestSPQuery(BlankBoardStateQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as BlankBoardState))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }

        }
    }


    /**
     * 开关电子白板
     * */
    fun switchBlankBoard(isOn: Boolean): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(WhiteBoardSwitchControl(
                            isOn,
                            onResult = { result ->
                                it.resumeWith(Result.success(result))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }

        }
    }


    /**
     * 输出课室
     */
    fun pushBlankBoard(data: List<ClassroomControlEntity>): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(PushWhiteBoardControl(
                            data,
                            onResult = { result ->
                                it.resumeWith(Result.success(result))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }

    /**
     * 切换白板书写状态
     */
    fun switchWrite(isOn: Boolean, isUseBg: Boolean): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(StartWriteControl(
                            isOn, isUseBg,
                            onResult = { result ->
                                it.resumeWith(Result.success(result))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }

    /**
     * 获取连接课室信息
     */
    fun getClassData(): Observable<BoardClassDataState> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<BoardClassDataState> {
                    AVAHttpEngine.requestSPQuery(BoardClassDataQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as BoardClassDataState))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 设置课室书写权限
     */
    fun setWritePermission(entity: ClassroomControlEntity, isOn: Boolean): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(SetWritePermissionControl(
                            entity,
                            isOn,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 换页
     */
    fun switchPage(page: Int): Observable<PageInfo> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<PageInfo> {
                    AVAHttpEngine.requestSPQuery(SwitchPageQueryControl(
                            page,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as PageInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 切换书写模式
     */
    fun switchBlankBoardMode(mode: String): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(SwitchWhiteBoardModeControl(
                            mode,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 获取录播当前课室输出
     */
    fun getBlankBoardPreOutput(): Observable<List<String>> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<ListWrapper<String>> {
                    AVAHttpEngine.requestSPQuery(WhiteBoardCurOutputQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ListWrapper<String>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result.datas)
            }
        }
    }


    /**
     * 下载笔迹
     *
     * @param savePath   存储路径
     * @param classIndex 课室id
     */
    fun downloadTrailFile(savePath: String, classIndex: String): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestDownloadFile(DownloadTrailFileQuery(
                            classIndex,
                            savePath,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 下载背景文件
     *
     * @param savePath 存储路径
     */
    fun downloadBgFile(savePath: String, index: String): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestDownloadFile(DownloadBgFileQuery(
                            index,
                            savePath,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 清除笔迹
     *
     * @param isLocal 是否清本地
     */
    fun clearBlankBoardData(isLocal: Boolean): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(CleanWhiteBoardDataControl(
                            isLocal,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }


    /**
     * 撤回批注
     *
     * @param classId 课室id
     * @param penType 笔类型
     */
    fun withDrawPotil(classId: String, penType: Int, count: Int): Observable<Boolean> {
        return Observable.create { emmiter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(WithdrawPotilControl(
                            classId,penType,count,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emmiter.onNext(result)
            }
        }
    }

}