package cn.com.ava.zqproject.ui.record

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
import cn.com.ava.common.util.logd
import cn.com.ava.player.IjkVideoPlayer
import cn.com.ava.player.PlayerCallback
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.databinding.FragmentRecordBinding
import cn.com.ava.zqproject.extension.getMainHandler
import cn.com.ava.zqproject.ui.MainViewModel

/**
 * 录课界面
 * <p>
 *     1.轮询录播信息，当录播进入会议跳到会议界面
 *     2.需求中的录播操作
 * </p>
 */
class RecordFragment : BaseFragment<FragmentRecordBinding>() {

    private val mRecordViewModel by viewModels<RecordViewModel>()

    private val mMainViewModel by activityViewModels<MainViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_record

    private var mVideoPlayer by autoCleared<IjkVideoPlayer>()

    override fun onBindViewModel2Layout(binding: FragmentRecordBinding) {
        binding.recordViewModel = mRecordViewModel
    }

    //控制台动画相关
    private var topVisibleAnim by autoCleared<Animation>()

    private var topGoneAnim by autoCleared<Animation>()

    private var bottomVisibleAnim by autoCleared<Animation>()

    private var bottomGoneAnim by autoCleared<Animation>()


    override fun initView() {
        initAnim()
        mVideoPlayer = IjkVideoPlayer()
        mBinding.videoView.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                mVideoPlayer?.setSurface(holder.surface)
                mVideoPlayer?.startPlay(
                    "rtsp://${CommonPreference.getElement(CommonPreference.KEY_LUBO_IP,"")}/stream/4?config.login=web"
                ,object : PlayerCallback {
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
            }

        })

        mRecordViewModel.startLoadRecordInfo()
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

        }
        mBinding.ivComputerMenu.setOnClickListener {

        }
        mBinding.llTrack.setOnClickListener {

        }
        mBinding.llKeySet.setOnClickListener {

        }
        mBinding.llVolume.setOnClickListener {

        }
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.rootView.setOnClickListener {
            mRecordViewModel.isControlVisible.value = mRecordViewModel.isControlVisible.value?.not()
        }
        mBinding.topBar.setOnClickListener {  }
        mBinding.bottomBar.setOnClickListener {  }


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

    override fun observeVM() {
        mRecordViewModel.isShowLoading.observe(viewLifecycleOwner) {
            mMainViewModel.isShowLoading.postValue(it)
        }
        mRecordViewModel.isControlVisible.observe(viewLifecycleOwner) { visible ->
            topVisibleAnim?.apply {
                if (visible) {   //显示
                    mBinding.topBar.startAnimation(topVisibleAnim)
                    mBinding.bottomBar.startAnimation(bottomVisibleAnim)
                }else{
                    mBinding.topBar.startAnimation(topGoneAnim)
                    mBinding.bottomBar.startAnimation(bottomGoneAnim)
                }
            }

        }
    }

    override fun onDestroyView() {
        cancelAnim()
        super.onDestroyView()
    }

    private fun cancelAnim() {
        mBinding.topBar.clearAnimation()
        mBinding.bottomBar.clearAnimation()

        topGoneAnim = null
        topVisibleAnim = null
        bottomVisibleAnim = null
        bottomGoneAnim = null
    }


}