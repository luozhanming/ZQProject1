package cn.com.ava.zqproject.common

import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.VideoResourceManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 录制上传管理器
 * */
object RecordUploadManager {

    /**
     * 待上传的文件列表
     * */
    private val prepareUploadRecordFile: MutableList<RecordFilesInfo.RecordFile> by lazy {
        arrayListOf()
    }

    private val disposables: MutableList<Disposable> by lazy {
        arrayListOf()
    }

    private var uploadDisposable: Disposable? = null


    /**
     * 添加最近的录制文件
     * */
    fun addLatestUpload() {
        val disposable = Observable.timer(20, TimeUnit.SECONDS)
            .flatMap {
                VideoResourceManager.getRecordFileList()
                    .map {
                        Collections.sort(it) { t1, t2 ->
                            return@sort (t2.recordRawBeginTime.replace("\"", "")
                                .toLong() - t1.recordRawBeginTime.replace("\"", "")
                                .toLong()).toInt()
                        }
                        it
                    }.map { list ->
                        if (list.isNotEmpty()) {
                            return@map list[0]
                        } else {
                            return@map null
                        }
                    }
                    .subscribeOn(Schedulers.io())
            }.subscribe({
                it?.apply {
                    prepareUploadRecordFile.add(this)
                }
            }, {
                logPrint2File(it)
            })
        disposables.add(disposable)
    }


    fun reset() {
        disposables.forEach {
            it.dispose()
        }
        prepareUploadRecordFile.clear()
    }


    fun startUpload(callback: (List<RecordFilesInfo.RecordFile>) -> Unit) {
        uploadDisposable =
            Observable.create<Boolean> {
                while (disposables.isNotEmpty() && disposables.all { it.isDisposed }) {
                    Thread.sleep(100)
                }
                it.onNext(true)
                it.onComplete()
            }.timeout(20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.invoke(prepareUploadRecordFile)
                }, {
                    reset()
                    logPrint2File(it)
                },{
                    reset()
                })
    }
}