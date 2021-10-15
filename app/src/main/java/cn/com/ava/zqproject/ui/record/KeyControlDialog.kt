package cn.com.ava.zqproject.ui.record

import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogKeyControlBinding
import cn.com.ava.zqproject.ui.record.adapter.CustomKeyAdapter
import cn.com.ava.zqproject.vo.CommandButton
import cn.com.ava.zqproject.vo.StatefulView
/**
 * 已选择按键对话框
 * */
class KeyControlDialog : BaseDialogV2<DialogKeyControlBinding>() {

    private val mKeyControlViewModel by viewModels<KeyControlViewModel>({requireParentFragment()})

    private var mKeyControlAdapter by autoCleared<CustomKeyAdapter>()

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(648), SizeUtils.dp2px(390), Gravity.CENTER, true)
    }

    override fun initView(root: View) {
        mBinding.rvButton.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        mKeyControlAdapter = CustomKeyAdapter(object : CustomKeyAdapter.CustomKeyCallback{
            override fun buttonClicked(button: StatefulView<CommandButton>) {
                handleKeyClicked(button)
            }

        })
        mBinding.rvButton.adapter = mKeyControlAdapter
        mBinding.btnReset.setOnClickListener {
            dismiss()
            val dialog = KeySettingDialog()
            dialog.show(requireParentFragment().childFragmentManager,"key_setting")
        }
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mKeyControlViewModel.getCommandButtons()
    }

    private fun handleKeyClicked(button: StatefulView<CommandButton>) {
        val btn = button.obj
        mKeyControlViewModel.sendKeyCommand(btn)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_key_control
    }

    override fun onBindViewModel2Layout(binding: DialogKeyControlBinding) {

    }

    override fun observeVM() {
        mKeyControlViewModel.commandButtons.observe(this) {
            mKeyControlAdapter?.setDatasWithDiff(it)
        }
    }
}