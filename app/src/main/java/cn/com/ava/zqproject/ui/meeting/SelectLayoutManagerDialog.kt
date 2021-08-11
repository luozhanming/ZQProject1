package cn.com.ava.zqproject.ui.meeting

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogSelectLayoutBinding

class SelectLayoutManagerDialog : BaseDialogV2<DialogSelectLayoutBinding>() {

    private val mSelectLayoutViewModel by viewModels<SelectLayoutManagerViewModel>()


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(720),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
    }

    override fun initView(root: View) {

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnCancel.setOnClickListener { dismiss() }
        val layoutClicked = View.OnClickListener {
            when(it.id){
                R.id.iv_layout_auto->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_AUTO
                R.id.iv_layout_1->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_1
                R.id.iv_layout_2->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_2
                R.id.iv_layout_3->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_3
                R.id.iv_layout_4->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_4
                R.id.iv_layout_6->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_6
                R.id.iv_layout_8->mSelectLayoutViewModel.layoutSelect.value = SelectLayoutManagerViewModel.LAYOUT_8
            }
        }
        mBinding.ivLayoutAuto.setOnClickListener(layoutClicked)
        mBinding.ivLayout1.setOnClickListener(layoutClicked)
        mBinding.ivLayout2.setOnClickListener(layoutClicked)
        mBinding.ivLayout3.setOnClickListener(layoutClicked)
        mBinding.ivLayout4.setOnClickListener(layoutClicked)
        mBinding.ivLayout6.setOnClickListener(layoutClicked)
        mBinding.ivLayout8.setOnClickListener(layoutClicked)
        mBinding.btnNextStep.setOnClickListener {
            val selectLayout = mSelectLayoutViewModel.layoutSelect.value!!
            if(selectLayout==SelectLayoutManagerViewModel.LAYOUT_AUTO){
                mSelectLayoutViewModel.step.value = SelectLayoutManagerViewModel.STEP_2_2
            }else{
                mSelectLayoutViewModel.step.value = SelectLayoutManagerViewModel.STEP_2_1
            }
        }
        mBinding.btnLastStep.setOnClickListener {
            mSelectLayoutViewModel.step.value = SelectLayoutManagerViewModel.STEP_1
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_select_layout
    }

    override fun onBindViewModel2Layout(binding: DialogSelectLayoutBinding) {
        binding.selectLayoutViewModel = mSelectLayoutViewModel
    }

    override fun observeVM() {

    }
}