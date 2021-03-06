package cn.com.ava.zqproject.ui.videoResource

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
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

    // ??????????????????
    private var topVisibleAnim by autoCleared<Animation>()

    private var topGoneAnim by autoCleared<Animation>()

    private var bottomVisibleAnim by autoCleared<Animation>()

    private var bottomGoneAnim by autoCleared<Animation>()
    // ??????????????????
    private var mVolumeReceiver: MyVolumeReceiver? = null
    // ???????????????
    private var mAudioManager: AudioManager? = null
    // ?????????????????????
    private var mMaxVolume = 15
    private var mCurrentVolume = 0

    private var isShowVolume = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_play
    }

    override fun onResume() {
        super.onResume()
        myRegisterReceiver()
    }

    fun myRegisterReceiver() {
        logd("??????????????????")
        mVolumeReceiver = MyVolumeReceiver(object : MyVolumeReceiver.MyVolumeReceiverCallback {
            override fun onVolumeChanged(volume: Int) {
                mCurrentVolume = volume
                mVolumeWindow?.setMaxVolume(mMaxVolume)
                mVolumeWindow?.setVolume(mCurrentVolume)
            }
        })
        var filter = IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        requireActivity().registerReceiver(mVolumeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        logd("????????????????????????")
        requireActivity().unregisterReceiver(mVolumeReceiver)
    }

    // rtsp://192.168.21.204:554/playback/63613135613863302d73747265616d302dbeabc6b7bfceb3ccc2bcd6c65fc1d6c0cfcaa62d3139323078313038305f33306670735f313230676f705f343039366b6270735f7662725f6176632d3332306b5f3136626974735f73746572656f5f34386b687a5f6161632d32303230313231313134323530345f32303230313231313134323530385f332e6d7034
    override fun initView() {
        super.initView()
        initAnim()

//        val rtspUrl: String = arguments?.get("rtspUrl").toString()
//        val downloadFileName = arguments?.get("downloadFileName").toString()
        video = Gson().fromJson(arguments?.get("video").toString(), object : TypeToken<RecordFilesInfo.RecordFile>(){}.type)
        logd("????????????: ${video.toString()}")

        mAudioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mMaxVolume = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)!!
        mCurrentVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
        logd("??????????????????: $mCurrentVolume")

        mBinding.tvFileName.setText(video?.downloadFileName)
        mBinding.tvDuration.setText(video?.duration)
        mBinding.sbProgress.max = video?.rawDuration!!.toInt()

        mBinding.ivBack.setOnClickListener {
            isShowVolume = false
            findNavController().navigateUp()
        }
        // ??????/??????
        mBinding.btnPlayStop.setOnClickListener {
            isShowVolume = false
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
            if (isShowVolume) {
                isShowVolume = false
                mVolumeWindow?.dismiss()
                return@setOnClickListener
            }
            mVideoPlayViewModel.isControlVisible.value = mVideoPlayViewModel.isControlVisible.value?.not()
        }
        // ??????
        mBinding.btnVolume.setOnClickListener {
            if (isShowVolume) {
                isShowVolume = false
                mVolumeWindow?.dismiss()
                return@setOnClickListener
            }
            isShowVolume = true
            mVideoPlayViewModel.masterVolume.value = mCurrentVolume
        }
        // ????????????
        mBinding.sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mBinding.tvBeginTime.setText(mVideoPlayViewModel.getCurrentTime(p1))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                cancelTimer()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isShowVolume = false
                logd("?????????" + p0?.progress)
                val duration = p0?.progress!!.toLong() * 1000
                logd("????????????$duration")
                if (isFinishPlay) { // ??????????????????????????????????????????startPlay??????
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

                logd("????????????")
                resetPlay()
            }

            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                logd("??????: ${player.curPosition}")
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

    // ???????????????
    fun startTimer() {
        cancelTimer()
        disposable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                var time = mVideoPlayViewModel.currentDuration.value
                time = (player.curPosition / 1000).toInt()
                logd("????????????????????????: ${player.curPosition}, ?????????????????????: $time")
                if (time!! <= video?.rawDuration!!.toInt()) {
                    mVideoPlayViewModel.currentDuration.value = time
                } else {
                    finishPlay()
                }
            }, {

            })
    }

    // ???????????????
    fun cancelTimer() {
        if (disposable != null && disposable?.isDisposed() == false) {
            logd("???????????????")
            disposable?.dispose()
        }
    }

    // ????????????
    fun startPlay() {
        startTimer()
        if (!isFinishPlay) {
            mBinding.sbProgress.progress = 0
        }
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_pause_3)
    }

    // ???????????????
    fun resetPlay() {
        player.startPlay(video?.rtspUrl,
            object : PlayerCallback {
                override fun onStart() {
                    logd("onStart")
                    startPlay()
                    if (isFinishPlay) { // ?????????????????????????????????????????????
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

    // ????????????
    fun resumePlay() {
        logd("????????????")
        player.resume()
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_pause_3)
    }

    // ????????????
    fun pausePlay() {
        logd("????????????")
        player.pause()
        mBinding.btnPlayStop.setImageResource(R.mipmap.ic_play)
    }

    // ????????????
    fun finishPlay() {
        logd("????????????")
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
//                mVideoPlayViewModel.changeVolume(it)
                logd("???????????????: $it")
                mCurrentVolume = it
                mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, it, AudioManager.FLAG_SHOW_UI)
            }
            mVolumeWindow?.setMaxVolume(mMaxVolume)
            mVolumeWindow?.setVolume(volume)
            val xoff = (mVolumeWindow!!.width - mBinding.btnVolume.width) / 2 + 5
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

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private class MyVolumeReceiver(private val mCallback: MyVolumeReceiverCallback? = null) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

                val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                mCallback.let {
                    it?.onVolumeChanged(currVolume)
                }
//                ToastUtils.showLong("????????????: $currVolume")
//                logd("????????????: $currVolume")
            }
        }

        interface MyVolumeReceiverCallback {
            fun onVolumeChanged(volume: Int)
        }
    }
}