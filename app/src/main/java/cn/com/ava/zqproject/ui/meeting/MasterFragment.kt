package cn.com.ava.zqproject.ui.meeting

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
import cn.com.ava.player.IjkVideoPlayer
import cn.com.ava.player.PlayerCallback
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.databinding.FragmentMasterBinding
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.common.ConfirmDialog

/**
 * 主讲界面
 * */
class MasterFragment : BaseFragment<FragmentMasterBinding>(), SurfaceHolder.Callback {

    private val mMasterViewModel by viewModels<MasterViewModel>()

    private val mMainViewModel by activityViewModels<MainViewModel>()


    private var mExitMeetingDialog by autoCleared<ConfirmDialog>()
    private var mVideoPlayer by autoCleared<IjkVideoPlayer>()
    private var mMeetingInfoWindow by autoCleared<MeetingInfoPopupWindow>()
    private var mComputerSourceWindow by autoCleared<ComputerSourcePopupWindow>()






    //控制台动画相关
    private var topVisibleAnim by autoCleared<Animation>()

    private var topGoneAnim by autoCleared<Animation>()

    private var bottomVisibleAnim by autoCleared<Animation>()

    private var bottomGoneAnim by autoCleared<Animation>()

    private var breatheAnim by autoCleared<Animation>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_master
    }

    override fun initView() {
        initAnim()
        bindListener()
        mMasterViewModel.startLoadMeetingInfo()
        mMasterViewModel.loopCurVideoSceneSources()
    }



    override fun observeVM() {
        mMasterViewModel.isShowLoading.observe(viewLifecycleOwner) {
            mMainViewModel.isShowLoading.postValue(it)
        }
        mMasterViewModel.isControlVisible.observe(viewLifecycleOwner) { visible ->
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
        mMasterViewModel.computerSourceList.observe(viewLifecycleOwner) {
            mComputerSourceWindow =
                mComputerSourceWindow ?: ComputerSourcePopupWindow(requireContext())
            val xoff = (mComputerSourceWindow!!.width - mBinding.ivComputerMenu.width) / 2
            mComputerSourceWindow?.showAsDropDown(
                mBinding.ivComputerMenu,
                -xoff,
                -SizeUtils.dp2px(300),
                Gravity.TOP
            )
            mComputerSourceWindow?.setSources(it)
        }
        mMasterViewModel.exitMeeting.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        mMasterViewModel.isRecording.observe(viewLifecycleOwner) {
            breatheAnim?.apply {
                if (it) {
                    mBinding.ivDot.startAnimation(breatheAnim)
                } else {
                    breatheAnim?.cancel()
                    mBinding.ivDot.clearAnimation()
                }
            }
        }

    }

    override fun onBindViewModel2Layout(binding: FragmentMasterBinding) {
        binding.masterViewModel = mMasterViewModel
    }

    override fun bindListener() {
        mBinding.videoView.holder.addCallback(this)

        mBinding.root.setOnClickListener {
            mMasterViewModel.isControlVisible.value =
                mMasterViewModel.isControlVisible.value?.not() ?: true
        }
        mBinding.topBar.setOnClickListener { }
        mBinding.bottomBar.setOnClickListener { }
        mBinding.llComputer.setOnClickListener {
            mMasterViewModel.toggleComputer()
        }
        mBinding.llVolumeScene.setOnClickListener { }
        mBinding.llExitMeeting.setOnClickListener {
            mExitMeetingDialog = ConfirmDialog(getString(R.string.tip_confirm_exit_meeting), true, {
                mMasterViewModel.overMeeting()
                it?.dismiss()
            })
            if (mExitMeetingDialog?.isAdded == false) {
                mExitMeetingDialog?.show(childFragmentManager, "exit_meeting")
            }
        }
        mBinding.ivComputerMenu.setOnClickListener {
            mMasterViewModel.getComputerSourceInfo()
        }
        mBinding.ivVolumeSceneMenu.setOnClickListener {
            val dialog = VolumeSceneDialog()
            if (!dialog.isAdded) {
                dialog.show(childFragmentManager, "volume_scene")
            }
        }
        mBinding.ivInfo.setOnClickListener {
            mMeetingInfoWindow = mMeetingInfoWindow ?: MeetingInfoPopupWindow(requireContext())
            mMeetingInfoWindow?.setMeetingMasterInfo(mMasterViewModel.meetingInfo.value)
            mMeetingInfoWindow?.showAtLocation(
                mBinding.ivInfo,
                Gravity.TOP or Gravity.CENTER,
                0,
                mBinding.topBar.measuredHeight
            )
        }
        mBinding.llRecord.setOnClickListener {
            mMasterViewModel.toggleRecord()
        }
        mBinding.llLive.setOnClickListener {
            mMasterViewModel.toggleLive()
        }
        mBinding.llSceneLayout.setOnClickListener {
            val dialog = SelectLayoutManagerDialog()
            dialog.show(childFragmentManager,"select_layout")
        }
        mBinding.llMemberManager.setOnClickListener {

        }
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
}