package cn.com.ava.zqproject.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.util.Extra
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentLuboSettingBinding
import cn.com.ava.zqproject.ui.MainViewModel
import com.blankj.utilcode.util.ToastUtils

class LuBoSettingFragment : BaseFragment<FragmentLuboSettingBinding>() {


    companion object {
        //tab的选择
        const val EXTRA_KEY_TAB_INDEX = "TabIndex"
    }

    @Extra(EXTRA_KEY_TAB_INDEX)
     var mTabIndex: Int = 0


    private val mLuboSettingViewModel by viewModels<LuBoSettingViewModel>()

    private val mMainViewModel by activityViewModels<MainViewModel>()


    override fun getLayoutId(): Int = R.layout.fragment_lubo_setting


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVM()
        logd("tabIndex:${mTabIndex}")
    }

    override fun observeVM() {
        mLuboSettingViewModel.toastMsg.observe(requireActivity()) {
            ToastUtils.showShort(it)
        }
    }
}