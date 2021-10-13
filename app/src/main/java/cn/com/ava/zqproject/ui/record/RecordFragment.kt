package cn.com.ava.zqproject.ui.record

import android.view.Gravity
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.listener.SimpleAnimationListener
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.Constant
import cn.com.ava.player.IjkVideoPlayer
import cn.com.ava.player.PlayerCallback
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommandKeyHelper
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.databinding.FragmentRecordBinding
import cn.com.ava.zqproject.ui.BaseLoadingFragment
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.common.ConfirmDialog

/**
 * 录课界面
 * <p>
 *     1.轮询录播信息，当录播进入会议跳到会议界面
 *     2.需求中的录播操作
 * </p>
 */
class RecordFragment : BaseLoadingFragment<FragmentRecordBinding>(),SurfaceHolder.Callback {

    private val mRecordViewModel by viewModels<RecordViewModel>()


    override fun getLayoutId(): Int = R.layout.fragment_record

    private var mVideoPlayer by autoCleared<IjkVideoPlayer>()

    private var mRecordInfoDialog by autoCleared<RecordInfoDialog>()

    private var mComputerMenu by autoCleared<ComputerModePopupWindow>()

    private var mVolumeWindow by autoCleared<VolumePopupWindow>()


    //控制台动画相关
    private var topVisibleAnim by autoCleared<Animation>()

    private var topGoneAnim by autoCleared<Animation>()

    private var bottomVisibleAnim by autoCleared<Animation>()

    private var bottomGoneAnim by autoCleared<Animation>()

    private var breatheAnim by autoCleared<Animation>()

    private var mTrackModeWindow by autoCleared<TrackModeWindow>()


    override fun initView() {
        initAnim()

        mBinding.videoView.holder.addCallback(this)

        mBinding.llRecord.setOnClickListener {
            mRecordViewModel.toggleRecord()
        }
        mBinding.llPause.setOnClickListener {
            mRecordViewModel.togglePause()
        }
        mBinding.llLive.setOnClickListener {
            mRecordViewModel.toggleLive()
        }
        mBinding.llComputer.setOnClickListener {
            mRecordViewModel.toggleComputer()
        }
        mBinding.ivComputerMenu.setOnClickListener {
            if (mComputerMenu == null) {
                mComputerMenu = ComputerModePopupWindow(requireContext()) {
                    //如果当前正在接入电脑并切换模式，画面也会切换
                    mRecordViewModel.changeComputer()
                }
            }
            val xoff = (mComputerMenu!!.width - mBinding.ivComputerMenu.width) / 2
            mComputerMenu?.showAsDropDown(
                mBinding.ivComputerMenu,
                -xoff,
                -SizeUtils.dp2px(26),
                Gravity.TOP
            )
        }
        mBinding.llTrack.setOnClickListener {
            showTrackModeWindow()
        }
        mBinding.llKeySet.setOnClickListener {
            if (CommandKeyHelper.getSelectedCommandKeys().isEmpty()) {
                val dialog = KeySettingDialog()
                dialog?.show(childFragmentManager, "key_setting")
            } else {
                val dialog = KeyControlDialog()
                dialog?.show(childFragmentManager, "key_control")
            }

        }
        mBinding.llVolume.setOnClickListener {

            mVolumeWindow = mVolumeWindow?: VolumePopupWindow(requireContext()){
                mRecordViewModel.changeVolume(it)
            }

            val xoff = (mVolumeWindow!!.width - mBinding.llVolume.width) / 2
            mVolumeWindow?.showAsDropDown(
                mBinding.llVolume,
                -xoff,
                -SizeUtils.dp2px(26),
                Gravity.TOP
            )
            mRecordViewModel.getVolumeInfo()
        }
        mBinding.ivRecordInfo.setOnClickListener {
            mRecordInfoDialog = RecordInfoDialog()
            if (mRecordInfoDialog?.isAdded == false) {
                mRecordInfoDialog?.show(childFragmentManager, "record_info")
            }
        }
        mBinding.ivBack.setOnClickListener {
            val recordInfo = mRecordViewModel.recordInfo.value
            if(recordInfo?.isLiving==true||recordInfo?.recordState!=Constant.RECORD_STOP){
                val dialog = ConfirmDialog(getString(R.string.tip_cancel_record_or_live),true,{
                    if(recordInfo?.recordState!=Constant.RECORD_STOP){
                        mRecordViewModel.toggleRecord()
                    }
                    if(recordInfo?.isLiving==true){
                        mRecordViewModel.toggleLive()
                    }
                    findNavController().popBackStack()
                },isDangerous = true)
                dialog.show(childFragmentManager,"dialog_exit")
            }else{
                findNavController().popBackStack()
            }

        }
        mBinding.rootView.setOnClickListener {
            //如果有弹出菜单，不隐藏
            if (mTrackModeWindow?.isShowing == true) {
                return@setOnClickListener
            }
            mRecordViewModel.isControlVisible.value = mRecordViewModel.isControlVisible.value?.not()
        }
        mBinding.topBar.setOnClickListener {

        }
        mBinding.bottomBar.setOnClickListener {

        }


        mRecordViewModel.startLoadRecordInfo()
        mRecordViewModel.getVolumeInfo()

    }

    private fun showTrackModeWindow() {
        if (mTrackModeWindow == null) {
            mTrackModeWindow = TrackModeWindow(requireContext()) { mode ->
                mRecordViewModel.setTrackMode(mode)
            }
        }
        val xoff = (mTrackModeWindow!!.width - mBinding.llTrack.width) / 2
        mTrackModeWindow?.showAsDropDown(mBinding.llTrack, -xoff, -SizeUtils.dp2px(18), Gravity.TOP)
    }

    private fun initAnim() {
        view?.post {
            topVisibleAnim =
                TranslateAnimation(0f, 0f, -mBinding.topBar.measuredHeight.toFloat(), 0f).apply {
                    duration = 200
                    fillAfter = true
                    setAnimationListener(object : SimpleAnimationListener() {
                        override fun onAnimationStart(animation: Animation?) {
                            mBinding.topBar.visibility = View.VISIBLE
                        }
                    })
                }
            topGoneAnim =
                TranslateAnimation(0f, 0f, 0f, -mBinding.topBar.measuredHeight.toFloat()).apply {
                    duration = 200
                    fillAfter = true
                    setAnimationListener(object : SimpleAnimationListener() {
                        override fun onAnimationEnd(animation: Animation?) {
                            mBinding.topBar.visibility = View.GONE
                        }
                    })
                }
            bottomVisibleAnim =
                TranslateAnimation(0f, 0f, mBinding.bottomBar.measuredHeight.toFloat(), 0f).apply {
                    duration = 200
                    fillAfter = true
                    setAnimationListener(object : SimpleAnimationListener() {
                        override fun onAnimationStart(animation: Animation?) {
                            mBinding.bottomBar.visibility = View.VISIBLE
                        }
                    })
                }

            bottomGoneAnim =
                TranslateAnimation(0f, 0f, 0f, mBinding.bottomBar.measuredHeight.toFloat()).apply {
                    duration = 200
                    fillAfter = true
                    setAnimationListener(object : SimpleAnimationListener() {
                        override fun onAnimationEnd(animation: Animation?) {
                            mBinding.bottomBar.visibility = View.GONE
                        }
                    })
                }

            breatheAnim = AlphaAnimation(1f, 0f).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }
        }


    }

    override fun onBindViewModel2Layout(binding: FragmentRecordBinding) {
        binding.recordViewModel = mRecordViewModel
    }


    override fun observeVM() {
        mRecordViewModel.isShowLoading.observeOne(viewLifecycleOwner) {
            if(it)showLoading() else hideLoading()
        }
        mRecordViewModel.isControlVisible.observe(viewLifecycleOwner) { visible ->
            topVisibleAnim?.apply {
                if (visible) {   //显示
                    mBinding.topBar.startAnimation(topVisibleAnim)
                    mBinding.bottomBar.startAnimation(bottomVisibleAnim)
                } else {
                    mBinding.topBar.startAnimation(topGoneAnim)
                    mBinding.bottomBar.startAnimation(bottomGoneAnim)
                }
            }
        }
        mRecordViewModel.recordInfo.observe(viewLifecycleOwner) { info ->
            mTrackModeWindow?.setTrackMode(info.trackMode)
        }
        mRecordViewModel.isRecording.observe(viewLifecycleOwner) {
            breatheAnim?.apply {
                if (it) {
                    mBinding.ivDot.startAnimation(breatheAnim)
                } else {
                    breatheAnim?.cancel()
                    mBinding.ivDot.clearAnimation()
                }
            }
        }
        mRecordViewModel.masterVolume.observe(viewLifecycleOwner){volume->
            mVolumeWindow?.setVolume(volume.volumnLevel)
        }

    }

    override fun onDestroyView() {
        cancelAnim()
        mBinding.videoView.holder.removeCallback(this)
        super.onDestroyView()
    }



        override fun surfaceCreated(holder: SurfaceHolder) {
            mVideoPlayer = IjkVideoPlayer()
            mVideoPlayer?.setSurface(holder.surface)
            mVideoPlayer?.startPlay(
                "rtsp://${
                    CommonPreference.getElement(
                        CommonPreference.KEY_LUBO_IP,
                        ""
                    )
                }/stream/4?config.login=web", object : PlayerCallback {
                    override fun onStart() {
                        logd("Video player onStart()")
                    }

                    override fun onCompleted() {
                        logd("Video player onComplete()")
                    }

                    override fun onError(error: Int) {
                        logd("Video player onError(${error})")
                    }

                    override fun notifyRemoteStop() {
                        logd("Video player notifyRemoteStop()")
                    }

                })
        }

        override fun surfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            mVideoPlayer?.stopPlay()
            mVideoPlayer?.release()
            mVideoPlayer = null
        }



    private fun cancelAnim() {
        mBinding.topBar.clearAnimation()
        mBinding.bottomBar.clearAnimation()
        mBinding.ivDot.clearAnimation()

    }


}