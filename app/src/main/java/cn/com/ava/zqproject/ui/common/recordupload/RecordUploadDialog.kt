package cn.com.ava.zqproject.ui.common.recordupload

import android.view.View
import android.view.ViewGroup
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogRecordUploadBinding
/**
 * 录制归档询问对话框
 * */
class RecordUploadDialog:BaseDialogV2<DialogRecordUploadBinding>() {
    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun initView(root: View) {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_record_upload
    }

    override fun onBindViewModel2Layout(binding: DialogRecordUploadBinding) {

    }

    override fun observeVM() {

    }
}