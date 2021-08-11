package cn.com.ava.zqproject.ui.home

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentHomeBinding
import cn.com.ava.zqproject.ui.LuBoShareViewModel
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.common.ConfirmDialog
import cn.com.ava.zqproject.ui.common.power.PowerDialog
import cn.com.ava.zqproject.ui.common.power.PowerViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {


    private val mHomeViewModel by viewModels<HomeViewModel>()

    private val mMainViewModel by activityViewModels<MainViewModel>()

    private val mLuboShareViewModel by activityViewModels<LuBoShareViewModel>()

    private val mPowerViewModel by viewModels<PowerViewModel>()


    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        mBinding.btnCreateMeeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createMeetingFragment)
        }
        mBinding.btnRecord.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_recordFragment)
        }
        mBinding.btnResource.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_videoResourceFragment)
        }
        mBinding.btnJoinMeeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_masterFragment)
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
            popupMenu.showAsDropDown(mBinding.ivSetting)
        }

        //预加载相关
        mHomeViewModel.preloadWindowAndLayout()
        //不停刷新token
        mHomeViewModel.loopRefreshToken()
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
    }

}