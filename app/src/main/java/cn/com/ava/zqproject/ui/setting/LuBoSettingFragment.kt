package cn.com.ava.zqproject.ui.setting

import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseActivity
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentLuboSettingBinding
import cn.com.ava.zqproject.ui.MainViewModel

class LuBoSettingFragment : BaseFragment<FragmentLuboSettingBinding>() {


    private val mLuboSettingViewModel by viewModels<LuBoSettingViewModel>()

    private val mMainViewModel by activityViewModels<MainViewModel>()


    override fun getLayoutId(): Int = R.layout.fragment_lubo_setting
}