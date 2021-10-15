package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommandKeyHelper
import cn.com.ava.zqproject.vo.*
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class KeyControlViewModel : BaseViewModel() {


    val commandButtons: MutableLiveData<List<StatefulView<CommandButton>>> by lazy {
        MutableLiveData()
    }

    /**
     * 跟踪模式标识
     * */
    var trackMode: String = Constant.TRACK_HAND
        set(value) {
            if (field != value) {
                field = value
            }
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
                logPrint2File(it, "KeyControlViewModel#getCommandButtons")
            })
    }

    fun sendKeyCommand(button: CommandButton) {
        var observable: Observable<Boolean>? = null
        if (button is VideoWindowButton) {
            if(trackMode==Constant.TRACK_FULL_AUTO){
                ToastUtils.showShort(getResources().getString(R.string.toast_full_track_cannot_use))
                return
            }
            observable = WindowLayoutManager.setPreviewLayout("V${button.windowIndex}")
        } else if (button is LayoutButton) {
            if(trackMode==Constant.TRACK_FULL_AUTO){
                ToastUtils.showShort(getResources().getString(R.string.toast_full_track_cannot_use))
                return
            }
            observable = WindowLayoutManager.setPreviewLayout(button.layoutCmd)
        } else if (button is VideoPresetButton) {
            if(trackMode!=Constant.TRACK_HAND){
                ToastUtils.showShort(getResources().getString(R.string.toast_mid_track_cannot_use))
                return
            }
            val window = PreviewVideoWindow()
            window.ptzIdx = button.videoWindowIndex
            observable = WindowLayoutManager.setVideoPresetPos(window, button.presetIndex)
        }
        observable?.apply {
            subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it, "KeyControlViewModel#sendKeyCommand")
                })
        }
    }

}