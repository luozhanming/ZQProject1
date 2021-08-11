package cn.com.ava.zqproject.ui.record

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.entity.RecordInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogRecordinfoBinding

class RecordInfoDialog : BaseDialogV2<DialogRecordinfoBinding>() {


    private val mRecordViewModel by viewModels<RecordViewModel>({requireParentFragment()})

    private val mRecordInfoViewModel by viewModels<RecordInfoViewModel>()

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(648),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )

    }

    override fun initView(root: View) {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.ivEdit.setOnClickListener {
            mRecordInfoViewModel.isEditingTheme.value = mRecordInfoViewModel.isEditingTheme.value?.not()?:false
            mRecordInfoViewModel.themeEditText.value = mRecordViewModel.recordInfo.value?.classTheme?:""
        }
        mBinding.tvCancel.setOnClickListener {
            mRecordInfoViewModel.isEditingTheme.value = false
        }
        mBinding.tvSure.setOnClickListener {
            mRecordInfoViewModel.changeTheme()
        }
        mRecordInfoViewModel.getMahineInfo()
    }



    override fun getLayoutId(): Int {
        return R.layout.dialog_recordinfo
    }



    override fun onBindViewModel2Layout(binding: DialogRecordinfoBinding) {
        binding.recordInfoViewModel = mRecordInfoViewModel
        binding.recordViewModel = mRecordViewModel
    }

    override fun observeVM() {

    }


}