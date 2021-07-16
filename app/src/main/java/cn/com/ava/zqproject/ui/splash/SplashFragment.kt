package cn.com.ava.zqproject.ui.splash

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.util.WebViewUtil
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentSplashBinding
import cn.com.ava.zqproject.ui.MainViewModel
import cn.com.ava.zqproject.ui.setting.LuBoSettingFragment
import cn.com.ava.zqproject.ui.setting.LuBoSettingFragmentDirections

class SplashFragment : BaseFragment<FragmentSplashBinding>() {


    private var isPermissionsGrant:Boolean = false

    private val mSplashViewModel by viewModels<SplashViewModel>()


    private val mMainViewModel by activityViewModels<MainViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

    override fun onBindViewModel2Layout(binding: FragmentSplashBinding) {
        binding.splashViewModel = mSplashViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            isPermissionsGrant = it.all { it.value }
        }.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        mSplashViewModel.login()
        mSplashViewModel.startTimeout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun observeVM(){
        mSplashViewModel.goWhere.observe(this){it->
            when(it){
                SplashViewModel.WHERE_LUBO_SETTING->{
                    findNavController().navigate(R.id.action_splashFragment_to_luBoSettingFragment)
                }
                SplashViewModel.WHERE_PLATFORM_SETTING->{
                    findNavController().navigate(R.id.action_splashFragment_to_luBoSettingFragment,Bundle().apply {
                        putInt(LuBoSettingFragment.EXTRA_KEY_TAB_INDEX,1)
                    })
                }
                SplashViewModel.WHERE_PLATFORM_LOGIN->{
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }

        }
    }



}