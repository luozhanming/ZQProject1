package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.common.CommandKeyHelper
import cn.com.ava.zqproject.common.LayoutButtonHelper
import cn.com.ava.zqproject.vo.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class KeyControlViewModel : BaseViewModel() {


    val commandButtons: MutableLiveData<List<StatefulView<CommandButton>>> by lazy {
        MutableLiveData()
    }


    fun getCommandButtons() {
        Observable.create<List<CommandButton>> {
            val keys = CommandKeyHelper.getSelectedCommandKeys()
            it.onNext(keys)
        }.map {
            val statefuls = arrayListOf<StatefulView<CommandButton>>()
            it.forEach {
                statefuls.add(StatefulView(it))
            }
            statefuls
        }.subscribeOn(Schedulers.io())
            .subscribe({
                commandButtons.postValue(it)
            }, {
                logPrint2File(it,"KeyControlViewModel#getCommandButtons")
            })
    }

    fun sendKeyCommand(button: CommandButton) {
        var observable: Observable<Boolean>? = null
        logd("sendKeyCommand:${button.toString()}")
        if (button is VideoWindowButton) {
            observable = WindowLayoutManager.setPreviewLayout("V${button.windowIndex}")
        } else if (button is LayoutButton) {
            observable = WindowLayoutManager.setPreviewLayout(button.layoutCmd)
        } else if (button is VideoPresetButton) {
            val window = PreviewVideoWindow()
            window.ptzIdx = button.videoWindowIndex
            observable = WindowLayoutManager.setVideoPresetPos(window, button.presetIndex)
        }
        observable?.apply {
            subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"KeyControlViewModel#sendKeyCommand")
                })
        }
    }

}