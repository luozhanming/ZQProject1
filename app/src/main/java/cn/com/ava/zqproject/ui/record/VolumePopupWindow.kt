package cn.com.ava.zqproject.ui.record

import android.content.Context
import android.view.View
import android.widget.SeekBar
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R

/**
 * 音量窗口
 * */
class VolumePopupWindow(context: Context, val onVolumeChanged: ((Int) -> Unit)? = null) :
    BasePopupWindow(context) {

    private var sbVolume: SeekBar? = null


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(20), SizeUtils.dp2px(160), true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_volume
    }

    override fun initView(root: View) {
        sbVolume = root.findViewById(R.id.sb_volume)
        sbVolume?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress =seekBar?.progress?:0
                onVolumeChanged?.invoke(progress)
            }

        })
    }

    fun setVolume(value: Int) {
        if (value > 5) {
            sbVolume?.progress = 0
        }else{
            sbVolume?.progress = value
        }
    }
}