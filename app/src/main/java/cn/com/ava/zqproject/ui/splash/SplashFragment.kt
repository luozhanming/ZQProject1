package cn.com.ava.zqproject.ui.splash

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentSplashBinding
import cn.com.ava.zqproject.ui.MainViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding>() {


    private var isPermissionsGrant:Boolean = false

    private val mSplashViewModel by viewModels<SplashViewModel>()
    // activity内共享
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.button1.setOnClickListener {

        }
    }

}