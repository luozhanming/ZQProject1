package cn.com.ava.zqproject.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentSplashBinding
import cn.com.ava.zqproject.ui.MainViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val mSplashViewModel by viewModels<SplashViewModel>()
    // activity内共享
    private val mMainViewModel by activityViewModels<MainViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.button.setOnClickListener {
            mMainViewModel.getShowLoading().postValue(true)
        }



    }

}