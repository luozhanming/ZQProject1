package cn.com.ava.zqproject.ui.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.td.terminal.avartspviewer.RtspView
import cn.com.ava.zqproject.ui.meeting.adapter.CamPreviewAdapter
import cn.com.ava.zqproject.vo.CamPreviewInfo
import cn.com.ava.zqproject.vo.StatefulView
import io.reactivex.schedulers.Schedulers

class PreviewSceneWidget(context: Context, attributeSet: AttributeSet?, defStyle: Int) :
    HorizontalScrollView(context, attributeSet, defStyle), TextureView.SurfaceTextureListener {


    companion object {
        const val PREVIEW_DIVIDER_WIDTH = 3
    }

    private var rvPreviewInfo: RecyclerView? = null
    private var mLinearLayout: LinearLayout? = null
    private var mFrameLayout: FrameLayout? = null
    private var mRtspView: RtspView? = null

    private var mCurCamPreviewInfo: CamPreviewInfo? = null

    private var isDestroyLive: Boolean = false

    private var mCamPreviewAdapter: CamPreviewAdapter? = null


    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        mLinearLayout = LinearLayout(context)
        mLinearLayout?.orientation = LinearLayout.HORIZONTAL
        addView(
            mLinearLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mFrameLayout = FrameLayout(context)
        mRtspView = RtspView(context)
        rvPreviewInfo = RecyclerView(context)
        mCamPreviewAdapter = CamPreviewAdapter(){
            //简单功能直接上业务代码  别学
            InteracManager.postMainStream(it.index)
                .subscribeOn(Schedulers.io())
                .subscribe({

                },{
                    logPrint2File(it)
                })
        }
        rvPreviewInfo?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvPreviewInfo?.adapter = mCamPreviewAdapter
    }


    fun setCamPreviewInfo(camPreviewInfo: CamPreviewInfo) {
        if (mCurCamPreviewInfo != null) {  //已经加载过了
            mCurCamPreviewInfo = camPreviewInfo
            updateCamPreviewInfo(camPreviewInfo)
            return
        }
        mCurCamPreviewInfo = camPreviewInfo
        //根据控件高度计算每个窗口的宽度16:9
        val eachWidth = height * 16 / 9
        logd("window each width:${eachWidth}")
        //计算FrameLayout的宽度
        val frameWidth =
            eachWidth * camPreviewInfo.camCount + (camPreviewInfo.camCount - 1) * PREVIEW_DIVIDER_WIDTH
        //计算rtspview的宽高
        val rtspWidth =
            eachWidth * camPreviewInfo.column + (camPreviewInfo.column - 1) * PREVIEW_DIVIDER_WIDTH
        var rtspHeight = 0
        if (camPreviewInfo.row > 1) {
            rtspHeight = 2 * height + 3
        } else {
            rtspHeight = height
        }
        mLinearLayout?.addView(
            mFrameLayout,
            LinearLayout.LayoutParams(frameWidth, LinearLayout.LayoutParams.MATCH_PARENT)
        )
        mRtspView?.surfaceTextureListener = this
        mFrameLayout?.addView(mRtspView, LayoutParams(rtspWidth, rtspHeight))
        mFrameLayout?.addView(rvPreviewInfo, FrameLayout.LayoutParams(frameWidth, height))
        camPreviewInfo.previewWindow.apply {
            val statefuls = arrayListOf<StatefulView<PreviewVideoWindow>>()
            forEach {
                val stateful = StatefulView(it)
                stateful.isSelected = it.isCurrentOutput
                statefuls.add(stateful)
            }
            mCamPreviewAdapter?.setDatasWithDiff(statefuls)
        }
    }

    private fun updateCamPreviewInfo(camPreviewInfo: CamPreviewInfo) {
        camPreviewInfo.previewWindow.apply {
            val statefuls = arrayListOf<StatefulView<PreviewVideoWindow>>()
            forEach {
                val stateful = StatefulView(it)
                stateful.isSelected = it.isCurrentOutput
                statefuls.add(stateful)
            }
            mCamPreviewAdapter?.setDatas(statefuls)
        }
    }





override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
    logd("onSurfaceTextureAvailable")
    isDestroyLive = false
    mRtspView?.initLiveTool(Surface(surface))
    val num_streams = 1
    val in_x = intArrayOf(0)
    val in_y = intArrayOf(0)
    val in_width = intArrayOf(0)
    val in_height = intArrayOf(0)
    mRtspView?.startLiving(
        "rtsp://192.168.21.204:554/stream/1?config.login=web",
        num_streams,
        in_x,
        in_y,
        in_width,
        in_height
    )
}

override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

}

override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
    mRtspView?.stopLiving()
    mRtspView?.destroyLiveTool()
    isDestroyLive = true
    logd("onSurfaceTextureDestroyed")
    return true
}

override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

}

}