package cn.com.ava.zqproject.ui.home

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentHomeBinding
import cn.com.ava.zqproject.ui.LuBoShareViewModel
import cn.com.ava.zqproject.ui.MainViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {


    private val mHomeViewModel by viewModels<HomeViewModel>()

    private val mMainViewModel by activityViewModels<MainViewModel>()

    private val mLuboShareViewModel by activityViewModels<LuBoShareViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_home

}