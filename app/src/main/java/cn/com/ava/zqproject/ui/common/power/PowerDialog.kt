package cn.com.ava.zqproject.ui.common.power

import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogPowerBinding

class PowerDialog:BaseDialogV2<DialogPowerBinding>() {

    private val mPowerViewModel by viewModels<PowerViewModel>({requireParentFragment()})

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(620),SizeUtils.dp2px(268),Gravity.CENTER,true)
    }

    override fun initView(root: View) {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnTurnoff.setOnClickListener {
            mPowerViewModel.turnoffMachine()
            dismiss()
        }
        mBinding.btnSleep.setOnClickListener {
            mPowerViewModel.sleepMachine()
            dismiss()
        }
        mBinding.btnReload.setOnClickListener {
            mPowerViewModel.reloadMachine()
            dismiss()
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_power
    }

    override fun onBindViewModel2Layout(binding: DialogPowerBinding) {

    }

    override fun observeVM() {
    }
}