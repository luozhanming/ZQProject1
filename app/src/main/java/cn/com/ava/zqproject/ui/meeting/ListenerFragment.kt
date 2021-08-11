package cn.com.ava.zqproject.ui.meeting

import android.view.Gravity
import android.view.SurfaceHolder
import android.view.View
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
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.player.IjkVideoPlayer
import cn.com.ava.player.PlayerCallback
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.databinding.FragmentListenerBinding
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.common.ConfirmDialog

class ListenerFragment : BaseFragment<FragmentListenerBinding>(), SurfaceHolder.Callback {


    private val mListenerViewModel by viewModels<ListenerViewModel>()
    private val mMainViewModel by activityViewModels<MainViewModel>()
    private var mVideoPlayer by autoCleared<IjkVideoPlayer>()

    private var mExitMeetingDialog by autoCleared<ConfirmDialog>()
    private var mComputerSourceWindow by autoCleared<ComputerSourcePopupWindow>()
    private var mMeetingInfoWindow by autoCleared<MeetingInfoPopupWindow>()


    //控制台动画相关
    private var topVisibleAnim by autoCleared<Animation>()

    private var topGoneAnim by autoCleared<Animation>()

    private var bottomVisibleAnim by autoCleared<Animation>()

    private var bottomGoneAnim by autoCleared<Animation>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_listener
    }


    override fun initView() {
        initAnim()
        mBinding.videoView.holder.addCallback(this)

        mBinding.root.setOnClickListener {
            mListenerViewModel.isControlVisible.value =
                mListenerViewModel.isControlVisible.value?.not() ?: true
        }
        mBinding.topBar.setOnClickListener { }
        mBinding.bottomBar.setOnClickListener { }
        mBinding.llApplySpeak.setOnClickListener { }
        mBinding.llComputer.setOnClickListener {
            mListenerViewModel.toggleComputer()
        }
        mBinding.llVolumeScene.setOnClickListener { }
        mBinding.llExitMeeting.setOnClickListener {
            mExitMeetingDialog = ConfirmDialog(getString(R.string.tip_confirm_exit_meeting), true, {
                mListenerViewModel.exitMeeting()
                it?.dismiss()
            })
            if (mExitMeetingDialog?.isAdded == false) {
                mExitMeetingDialog?.show(childFragmentManager, "exit_meeting")
            }
        }
        mBinding.ivComputerMenu.setOnClickListener {
            mListenerViewModel.getComputerSourceInfo()
        }
        mBinding.ivVolumeSceneMenu.setOnClickListener {
            val dialog = VolumeSceneDialog()
            if (!dialog.isAdded) {
                dialog.show(childFragmentManager, "volume_scene")
            }
        }
        mBinding.ivInfo.setOnClickListener {
            mMeetingInfoWindow = mMeetingInfoWindow ?: MeetingInfoPopupWindow(requireContext())
            mMeetingInfoWindow?.setListenerInfo(mListenerViewModel.listenerInfo.value)
            mMeetingInfoWindow?.showAtLocation(
                mBinding.ivInfo,
                Gravity.TOP or Gravity.CENTER,
                0,
                mBinding.topBar.measuredHeight
            )
        }

        mListenerViewModel.loopCurVideoSceneSources()
        mListenerViewModel.loopLoadListenerInfo()
    }

    override fun observeVM() {
        mListenerViewModel.isShowLoading.observe(viewLifecycleOwner) {
            mMainViewModel.isShowLoading.postValue(it)
        }
        mListenerViewModel.isControlVisible.observe(viewLifecycleOwner) { visible ->
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
        mListenerViewModel.computerSourceList.observe(viewLifecycleOwner) {
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
        mListenerViewModel.exitMeeting.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
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
        }
    }

    override fun onBindViewModel2Layout(binding: FragmentListenerBinding) {
        binding.listenerViewModel = mListenerViewModel
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