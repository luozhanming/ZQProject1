package cn.com.ava.zqproject.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.Extra
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentLuboSettingBinding
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.ui.BaseLoadingFragment
import cn.com.ava.zqproject.ui.common.ConfirmDialog
import cn.com.ava.zqproject.ui.meeting.VolumeSceneDialog
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils

/**
 * 录播平台设置界面
 * <p>
 * 界面流程：
 * 1.点击录播连接，表单检测，登录录播(登录成功提示，登录失败提示，休眠弹出唤醒对话框，唤醒则弹出等待框60秒后再登录）
 * 2.点击平台连接，获取平台接口（测试是否可用，不可用证明地址不正确，提示错误）
 * 3.只有录播连接成功和平台连接成功才能返回到平台登录界面
 * </p>
 */
class LuBoSettingFragment : BaseLoadingFragment<FragmentLuboSettingBinding>() {


    companion object {
        //tab的选择
        const val EXTRA_KEY_TAB_INDEX = "TabIndex"
    }

    @Extra(EXTRA_KEY_TAB_INDEX)
    var mTabIndex: Int = 0

    private val mLuboSettingViewModel by viewModels<LuBoSettingViewModel>()


    override fun getLayoutId(): Int = R.layout.fragment_lubo_setting

    private var mWakeupMachineDialog by autoCleared<ConfirmDialog>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVM()
        logd("tabIndex:${mTabIndex}")
        mLuboSettingViewModel.loadInitData()
        if(mTabIndex==0){
            mBinding.tvTabLubo.performClick()
        }else{
            mBinding.tvTabPlat.performClick()
        }
    }

    override fun onBindViewModel2Layout(binding: FragmentLuboSettingBinding) {
        mBinding.luboViewModel = mLuboSettingViewModel
    }


    override fun observeVM() {
        mLuboSettingViewModel.toastMsg.observeOne(viewLifecycleOwner) {
            ToastUtils.showShort(it)
        }
        mLuboSettingViewModel.showLoading.observeOne(viewLifecycleOwner) {
            if (it)
                showLoading()
            else
                hideLoading()
        }
        mLuboSettingViewModel.isShowWakeUp.observeOne(viewLifecycleOwner) {
            if (it != 0) {
                mWakeupMachineDialog = mWakeupMachineDialog ?: ConfirmDialog(Utils.getApp()
                    .getString(R.string.tips_lubo_need_wake_up),
                    false,
                    { dialog ->
                        dialog?.dismiss()
                        mLuboSettingViewModel.wakeupMachine()
                    },
                    { dialog ->
                        dialog?.dismiss()
                    })
                if (mWakeupMachineDialog?.isAdded == false) {
                    mWakeupMachineDialog?.show(childFragmentManager, "wakeup_confirm")
                }
            }
        }


    }

    override fun initView() {
        mBinding.tvTabLubo.setOnClickListener {
            mLuboSettingViewModel.tabIndex.value = 0
        }
        mBinding.tvTabPlat.setOnClickListener {
            mLuboSettingViewModel.tabIndex.value = 1
        }
        mBinding.btnLinkLubo.setOnClickListener {
            mLuboSettingViewModel.login()
        }
        mBinding.btnLinkPlatform.setOnClickListener {
            mLuboSettingViewModel.loadPlatformInterface()
        }
        if (LoginManager.isLogin() && PlatformApiManager.getApiPath(PlatformApiManager.PATH_WEBVIEW_LOGIN) != null) {
            mLuboSettingViewModel.canBackShow.postValue(true)
        } else {
            mLuboSettingViewModel.canBackShow.postValue(false)
        }
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        mLuboSettingViewModel.tabIndex.value = 1
    }

    override fun onBackPressed(): Boolean {
        logd("onBackPressed")
        if (LoginManager.isLogin() && PlatformApiManager.getApiPath(PlatformApiManager.PATH_WEBVIEW_LOGIN) != null) {
            findNavController().navigate(R.id.action_back_to_login, null, navOptions {
                popUpTo(R.id.luBoSettingFragment) {
                    inclusive = true
                }
            })
        }
        return true
    }

}