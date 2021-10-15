package cn.com.ava.zqproject.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.authcode.AuthKeyUtil
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.zqproject.BuildConfig
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.databinding.FragmentSplashBinding
import cn.com.ava.zqproject.extension.getMainHandler
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.common.ConfirmDialog
import cn.com.ava.zqproject.ui.setting.LuBoSettingFragment
import com.blankj.utilcode.util.Utils

/**
 * 欢迎界面
 * <p>
 * 界面流程：
 * 1.尝试登录录播（登录成功->跳到步骤2;登录失败->跳到录播设置界面;录播休眠中->步骤4）
 * 2.查询是否已有平台token，如果有步骤3，没有跳到平台登录界面.
 * 3.调用平台刷新token的接口，如果刷新没过期，跳到主界面，如果过期跳到平台登录界面
 * 4.弹出对话框询问是否唤醒，如果不唤醒，跳到录播设置界面，唤醒弹出等待加载弹窗（不可取消），60秒后步骤1
 * 5.申请磁盘读写权限，如果允许跳到步骤1，不允许也跳到步骤1
 * </p>
 */
class SplashFragment : BaseFragment<FragmentSplashBinding>() {


    private var isPermissionsGrant: Boolean = false

    private val mSplashViewModel by viewModels<SplashViewModel>()


    private val mMainViewModel by activityViewModels<MainViewModel>()

    private var mWakeUpConfirmDialog by autoCleared<ConfirmDialog>()

    private var hasAutoCode by CommonPreference(CommonPreference.KEY_HAS_AUTH_CODE, false)

    private var mAuthCodeDialog by autoCleared<AuthCodeDialog>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

    override fun onBindViewModel2Layout(binding: FragmentSplashBinding) {
        binding.splashViewModel = mSplashViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermissions()) {  //检测必要权限
            //步骤1
//            if (!BuildConfig.DEBUG&&!hasAutoCode) {
//                AuthKeyUtil.generateDeviceFile()
//                //弹出对话框
//                mAuthCodeDialog = mAuthCodeDialog?: AuthCodeDialog(){
//                    mSplashViewModel.login()
//                }
//                mAuthCodeDialog?.show(childFragmentManager,"auth")
//            }else{
                mSplashViewModel.login()
       //     }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
            || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                isPermissionsGrant = it.all { it.value }
//                if (!BuildConfig.DEBUG&&!hasAutoCode) {
//                    AuthKeyUtil.generateDeviceFile()
//                    //弹出对话框
//                    mAuthCodeDialog = mAuthCodeDialog?: AuthCodeDialog(){
//                        mSplashViewModel.login()
//                    }
//                    mAuthCodeDialog?.show(childFragmentManager,"auth")
//                }else{
                    mSplashViewModel.login()
              //  }
            }.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else {
            isPermissionsGrant = true
        }
        return isPermissionsGrant
    }

    override fun observeVM() {
        mSplashViewModel.goWhere.observeOne(this) { it ->
            val go: () -> Unit = {
                when (it) {
                    SplashViewModel.WHERE_LUBO_SETTING -> {
                        findNavController().navigate(R.id.action_splashFragment_to_luBoSettingFragment,    Bundle().apply {
                            putInt(LuBoSettingFragment.EXTRA_KEY_TAB_INDEX, 0)
                        })

                    }
                    SplashViewModel.WHERE_PLATFORM_SETTING -> {
                        findNavController().navigate(
                            R.id.action_splashFragment_to_luBoSettingFragment,
                            Bundle().apply {
                                putInt(LuBoSettingFragment.EXTRA_KEY_TAB_INDEX, 1)
                            })
                    }
                    SplashViewModel.WHERE_PLATFORM_LOGIN -> {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                    SplashViewModel.WHERE_GO_HOME -> {
                        findNavController().navigate(R.id.action_splashFragment_to_navigation_home)
                    }
                }
            }
            if (System.currentTimeMillis() in mSplashViewModel.stayRange) {
                val diff = mSplashViewModel.stayRange.last - System.currentTimeMillis()
                getMainHandler()?.postDelayed({
                    go()
                }, diff)
            } else {
                go()
            }
        }

        mSplashViewModel.isShowWakeUp.observe(viewLifecycleOwner) {
            if (it != 0) {
                if (mWakeUpConfirmDialog == null) {
                    mWakeUpConfirmDialog =
                        ConfirmDialog(Utils.getApp().getString(R.string.tips_lubo_need_wake_up),
                            false,
                            { dialog ->
                                dialog?.dismiss()
                                //1.弹出60秒对话框唤醒设备
                                //2.再次调用登录
                                mSplashViewModel.wakeupMachine()
                            }, { dialog ->
                                dialog?.dismiss()
                                findNavController().navigate(R.id.action_splashFragment_to_luBoSettingFragment)
                            })
                }
                mWakeUpConfirmDialog?.show(childFragmentManager, "wakeup")
            }
        }
        mSplashViewModel.isShowLoading.observe(viewLifecycleOwner) {
            mMainViewModel.isShowLoading.postValue(it)
        }
    }


    override fun onStart() {
        super.onStart()
        mSplashViewModel.isShowLoading.observe(viewLifecycleOwner) {
            mMainViewModel.isShowLoading.postValue(it)
        }
    }


}