package cn.com.ava.zqproject.ui.videoResource

import android.view.Surface
import android.view.SurfaceHolder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.player.IjkVideoPlayer
import cn.com.ava.player.PlayerCallback
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoPlayBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import cn.com.ava.zqproject.vo.VideoResource

class VideoPlayFragment : BaseFragment<FragmentVideoPlayBinding>() {

    private var surface: Surface? = null

    private var player: IjkVideoPlayer = IjkVideoPlayer()

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_play
    }
    // rtsp://192.168.21.204:554/playback/63613135613863302d73747265616d302dbeabc6b7bfceb3ccc2bcd6c65fc1d6c0cfcaa62d3139323078313038305f33306670735f313230676f705f343039366b6270735f7662725f6176632d3332306b5f3136626974735f73746572656f5f34386b687a5f6161632d32303230313231313134323530345f32303230313231313134323530385f332e6d7034
    override fun initView() {
        super.initView()

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

//        mBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceCreated(p0: SurfaceHolder?) {
//                surface = p0?.surface
////                player.setSurface(surface)
////                player.startPlay("rtsp://192.168.21.204:554/playback/63613135613863302d73747265616d302dbeabc6b7bfceb3ccc2bcd6c65fc1d6c0cfcaa62d3139323078313038305f33306670735f313230676f705f343039366b6270735f7662725f6176632d3332306b5f3136626974735f73746572656f5f34386b687a5f6161632d32303230313231313134323530345f32303230313231313134323530385f332e6d7034",
////                    object : PlayerCallback {
////                        override fun onStart() {
////                            logd("onStart")
////                        }
////
////                        override fun onCompleted() {
////                            logd("onCompleted")
////                        }
////
////                        override fun onError(error: Int) {
////                            logd("onError")
////                        }
////
////                        override fun notifyRemoteStop() {
////                            logd("notifyRemoteStop")
////                        }
////                    })
//            }
//
//            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun surfaceDestroyed(p0: SurfaceHolder?) {
//
//            }
//
//        })
    }
}