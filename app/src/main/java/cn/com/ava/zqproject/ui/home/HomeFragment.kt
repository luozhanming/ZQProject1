package cn.com.ava.zqproject.ui.home

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.RecordUploadManager
import cn.com.ava.zqproject.databinding.FragmentHomeBinding
import cn.com.ava.zqproject.ui.LuBoShareViewModel
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.common.ConfirmDialog
import cn.com.ava.zqproject.ui.common.power.PowerDialog
import cn.com.ava.zqproject.ui.common.power.PowerViewModel
import cn.com.ava.zqproject.ui.meeting.MemberManagerDialog
import cn.com.ava.zqproject.ui.videoResource.service.DownloadService
import cn.com.ava.zqproject.ui.videoResource.service.VideoSingleton

class HomeFragment : BaseFragment<FragmentHomeBinding>() {


    private val mHomeViewModel by viewModels<HomeViewModel>()

    private val mPowerViewModel by viewModels<PowerViewModel>()

    private var mServiceConnection: ServiceConnection? = null

    private var mDownloadBinder by autoCleared<DownloadService.DownloadBinder>()


    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        initData()
        mBinding.btnCreateMeeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createMeetingFragment)
        }
        mBinding.btnRecord.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_recordFragment)
        }
        mBinding.btnResource.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_videoResourceFragment)
//            findNavController().navigate(R.id.action_homeFragment_to_joinMeetingFragment)
        }
        mBinding.btnJoinMeeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_joinMeetingFragment)

        }

        mBinding.ivSetting.setOnClickListener {
            val popupMenu = SettingPopupMenu(requireContext()) {
                when (it) {
                    SettingPopupMenu.ITEM_INFO -> {
                        val dialog = InfoUpgradeDialog()
                        dialog.show(childFragmentManager, "info")
                    }
                    SettingPopupMenu.ITEM_POWER -> {
                        val dialog = PowerDialog()
                        dialog.show(childFragmentManager, "power")
                    }
                    SettingPopupMenu.ITEM_LOGOUT -> {
                        val dialog = ConfirmDialog(getString(R.string.confirm_logout), true, {
                            it?.dismiss()
                            mHomeViewModel.logout()
                            findNavController().navigate(R.id.action_back_to_login,null, navOptions {
                                popUpTo( R.id.homeFragment){
                                    inclusive = true
                                }
                            })
                        })
                        dialog.show(childFragmentManager,"confirm")
                    }
                }
            }
            popupMenu.showAtLocation(mBinding.ivSetting,Gravity.RIGHT or Gravity.TOP,-SizeUtils.dp2px(32),SizeUtils.dp2px(60))
        }
        //预加载相关
        mHomeViewModel.preloadWindowAndLayout()
        //不停刷新token
        mHomeViewModel.loopRefreshToken()

    }

    fun initData() {
        logd("实例化单例")
        VideoSingleton.getInstance()
    }

    override fun onStart() {
        super.onStart()
        mHomeViewModel.startloadLuboInfo()
        mHomeViewModel.startLoopMeetingInfoZQ()
        mHomeViewModel.startLoopMeetingInvitation()
        RecordUploadManager.startUpload {
            it.forEach {
                mDownloadBinder?.service?.uploadVideo(it,"")
            }
        }
    }


    override fun onStop() {
        super.onStop()
        mHomeViewModel.stopLoadLuboInfo()
        mHomeViewModel.stopLoopMeetingInfoZQ()
        mHomeViewModel.stopLoopMeetingInvitation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewModel.startHeartBeat()
        mServiceConnection = mServiceConnection?:object :ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mDownloadBinder = service as? DownloadService.DownloadBinder
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }

        }
        requireContext().bindService(
            Intent(requireContext(),DownloadService::class.java),mServiceConnection,
            Service.BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        super.onDestroy()
        mHomeViewModel.stopHeartBeat()
        //解绑
        requireContext().unbindService(mServiceConnection)
        mServiceConnection = null
    }

    override fun onBindViewModel2Layout(binding: FragmentHomeBinding) {
        binding.homeViewModel = mHomeViewModel
    }

    override fun observeVM() {
        //监听PowerViewModel
        val goSetting = { it: Boolean ->
            //退出登录
            LoginManager.logout()
            findNavController().navigate(R.id.action_back_to_lubo_setting,null, navOptions {
                popUpTo(R.id.homeFragment){
                    inclusive = true
                }
            })
        }
        mPowerViewModel.reloadMachine.observe(viewLifecycleOwner, goSetting)
        mPowerViewModel.sleepMachine.observe(viewLifecycleOwner, goSetting)
        mPowerViewModel.turnoffMachine.observe(viewLifecycleOwner, goSetting)

        mHomeViewModel.meetingInfoZq.observeOne(viewLifecycleOwner){
            if("created"==it.confStatus){
                if("cloudCtrlMode"==it.confMode){
                    findNavController().navigate(R.id.action_homeFragment_to_masterFragment)
                }else if("classMode"==it.confMode){
                    findNavController().navigate(R.id.action_homeFragment_to_listenerFragment)
                }
            }
        }
        mHomeViewModel.invitationInfo.observeOne(viewLifecycleOwner){
            val args = Bundle()
            args.putSerializable("invitationInfo",it)
            findNavController().navigate(R.id.action_homeFragment_to_receiveCallFragment,args)
        }
        mHomeViewModel.backToLogin.observeOne(viewLifecycleOwner){
            findNavController().navigate(R.id.action_back_to_login)
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }

}