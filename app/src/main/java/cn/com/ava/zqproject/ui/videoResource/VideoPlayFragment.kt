package cn.com.ava.zqproject.ui.videoResource

import android.view.Gravity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.listener.SimpleAnimationListener
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.player.IjkVideoPlayer
import cn.com.ava.player.PlayerCallback
import cn.com.ava.player.WYVideoPlayer
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoPlayBinding
import cn.com.ava.zqproject.ui.record.VolumePopupWindow
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import cn.com.ava.zqproject.vo.VideoResource
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.Disposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.nio.channels.AcceptPendingException
import java.util.concurrent.TimeUnit
import java.util.*

class VideoPlayFragment : BaseFragment<FragmentVideoPlayBinding>() {

    private var surface: Surface? = null

    private var video: RecordFilesInfo.RecordFile? = null

    private var player: WYVideoPlayer = WYVideoPlayer()

    private val mVideoPlayViewModel by viewModels<VideoPlayViewModel> ()

    private var mVolumeWindow by autoCleared<VolumePopupWindow>()

    private var disposable: Disposable? = null

    private var isFinishPlay = false

    private var seekToPosition: Long = 0

    // 控制动画相关
    private var topVisibleAnim by autoCleared<Animation>()

    private var topGoneAnim by autoCleared<Animation>()

    private var bottomVisibleAnim by autoCleared<Animation>()

    private var bottomGoneAnim by autoCleared<Animation>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_play
    }
    // rtsp://192.168.21.204:554/playback/63613135613863302d73747265616d302dbeabc6b7bfceb3ccc2bcd6c65fc1d6c0cfcaa62d3139323078313038305f33306670735f313230676f705f343039366b6270735f7662725f6176632d3332306b5f3136626974735f73746572656f5f34386b687a5f6161632d32303230313231313134323530345f32303230313231313134323530385f332e6d7034
    override fun initView() {
        super.initView()
        initAnim()

//        val rtspUrl: String = arguments?.get("rtspUrl").toString()
//        val downloadFileName = arguments?.get("downloadFileName").toString()
        video = Gson().fromJson(arguments?.get("video").toString(), object : TypeToken<RecordFilesInfo.RecordFile>(){}.type)
        logd("视频信息: ${video.toString()}")

        mBinding.tvFileName.setText(video?.downloadFileName)
        mBinding.tvDuration.setText(video?.duration)
        mBinding.sbProgress.max = video?.rawDuration!!.toInt()

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // 播放/暂停
        mBinding.btnPlayStop.setOnClickListener {
            if (mBinding.sbProgress.progress >= video?.rawDuration!!.toInt()) {
                mBinding.sbProgress.progress = 0
                isFinishPlay = false
                resetPlay()
                return@setOnClickListener
            }
            if (player.isPlaying == true) {
                pausePlay()
            } else {
                resumePlay()
            }
        }
        mBinding.root.setOnClickListener {
            if (mVolumeWindow?.isShowing == true) {
                mVolumeWindow?.dismiss()
                return@setOnClickListener
            }
            mVideoPlayViewModel.isControlVisible.value = mVideoPlayViewModel.isControlVisible.value?.not()
        }
        // 音量
        mBinding.btnVolume.setOnClickListener {
            if (mVolumeWindow?.isShowing == true) {
                mVolumeWindow?.dismiss()
                return@setOnClickListener
            }
            mVideoPlayViewModel.getVolumeInfo()
        }
        // 播放进度
        mBinding.sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mBinding.tvBeginTime.setText(mVideoPlayViewModel.getCurrentTime(p1))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                cancelTimer()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                logd("进度：" + p0?.progress)
                val duration = p0?.progress!!.toLong() * 1000
                logd("跳转到：$duration")
                if (isFinishPlay) { // 上次播放已经结束了，需要重新startPlay才行
                    seekToPosition = duration
                    resetPlay()
                } else {
                    player.seekTo(duration)
                    startTimer()
                }
            }
        })

        mBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder?) {
                surface = p0?.surface
                player.setSurface(surface)

                logd("开始播放")
                resetPlay()
            }

            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                logd("进度: ${player.curPosition}")
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                player.stopPlay()
                player.release()
            }
        })
    }

    private fun initAnim() {
        view?.post{
            topVisibleAnim = TranslateAnimation(0f, 0f, -mBinding.topView.measuredHeight.toFloat(), 0f).apply {
                duration = 200
                fillAfter = true
                setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationStart(animation: Animation?) {
                        mBinding.topView.visibility = View.VISIBLE
                    }
                })
            }
            topGoneAnim = TranslateAnimation(0f, 0f, 0f, -mBinding.topView.measuredHeight.toFloat()).apply {
                duration = 200
                fillAfter = true
                setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        mBinding.topView.visibility = View.GONE
                    }
                })
            }
            bottomVisibleAnim = TranslateAnimation(0f, 0f, mBinding.bottomView.measuredHeight.toFloat(), 0f).apply {
                duration = 200
                fillAfter = true
                setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationStart(animation: Animation?) {
                        mBinding.bottomView.visibility = View.VISIBLE
                    }
                })
            }
            bottomGoneAnim = TranslateAnimation(0f, 0f, 0f, mBinding.bottomView.measuredHeight.toFloat()).apply {
                duration = 200
                fillAfter = true
                setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        mBinding.bottomView.visibility = View.GONE
                    }
                })
            }
        }
    }

    // 开启定时器
    fun startTimer() {
        cancelTimer()
        disposable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                var time = mVideoPlayViewModel.currentDuration.value
                time = (player.curPosition / 1000).toInt()
                logd("当前时间（毫秒）: ${player.curPosition}, 当前时间（秒）: $time")
                if (time!! <= video?.rawDuration!!.toInt()) {
                    mVideoPlayViewModel.currentDuration.value = time
                } else {
                    finishPlay()
                }
            }, {

            })
    }

    // 取消定时器
    fun cancelTimer() {
        if (disposable != null && disposable?.isDisposed() == false) {
            logd("取消定时器")
            disposable?.dispose()
        }
    }

    // 开始播放
    fun startPlay() {
        startTimer()
        if (!isFinishPlay) {
            mBinding.sbProgress.progress = 0
        }
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_pause_3)
    }

    // 重置播放器
    fun resetPlay() {
        player.startPlay(video?.rtspUrl,
            object : PlayerCallback {
                override fun onStart() {
                    logd("onStart")
                    startPlay()
                    if (isFinishPlay) { // 已经播放完成在点击进度条跳转的
                        player.seekTo(seekToPosition)
                    }
                    isFinishPlay = false
                }

                override fun onCompleted() {
                    logd("onCompleted")
                    finishPlay()
                }

                override fun onError(error: Int) {
                    logd("onError")
                }

                override fun notifyRemoteStop() {
                    logd("notifyRemoteStop")
//                            cancelTimer()
                }
            })
    }

    // 恢复播放
    fun resumePlay() {
        logd("恢复播放")
        player.resume()
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_pause_3)
    }

    // 暂停播放
    fun pausePlay() {
        logd("暂停播放")
        player.pause()
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_play)
    }

    // 播放完成
    fun finishPlay() {
        logd("播放完成")
        isFinishPlay = true
        cancelTimer()
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_play)
    }

    override fun observeVM() {
        mVideoPlayViewModel.isControlVisible.observe(viewLifecycleOwner) { visible ->
            topVisibleAnim?.apply {
                if (visible) {
                    mBinding.topView.startAnimation(topVisibleAnim)
                    mBinding.bottomView.startAnimation(bottomVisibleAnim)
                } else {
                    mBinding.topView.startAnimation(topGoneAnim)
                    mBinding.bottomView.startAnimation(bottomGoneAnim)
                }
            }
        }

        mVideoPlayViewModel.masterVolume.observe(viewLifecycleOwner) { volume ->
            mVolumeWindow = mVolumeWindow?: VolumePopupWindow(requireContext()){
                mVideoPlayViewModel.changeVolume(it)
            }
            mVolumeWindow?.setVolume(volume.volumnLevel)
            val xoff = (mVolumeWindow!!.width - mBinding.btnVolume.width) / 2
            mVolumeWindow?.showAsDropDown(
                mBinding.btnVolume,
                -xoff,
                -SizeUtils.dp2px(26),
                Gravity.TOP
            )
        }

        mVideoPlayViewModel.currentDuration.observe(viewLifecycleOwner) {

            mBinding.sbProgress.progress = it
            mBinding.tvBeginTime.setText(mVideoPlayViewModel.getCurrentTime(it))
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}