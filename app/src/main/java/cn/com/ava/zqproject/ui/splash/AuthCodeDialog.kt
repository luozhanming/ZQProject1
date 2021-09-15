package cn.com.ava.zqproject.ui.splash

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogAuthCodeBinding
import com.blankj.utilcode.util.ToastUtils

class AuthCodeDialog:BaseDialogV2<DialogAuthCodeBinding>() {


    private val mAuthCodeViewModel by viewModels<AuthCodeViewModel>()


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(648),ViewGroup.LayoutParams.WRAP_CONTENT,canTouchOutsideCancel = false)
    }

    override fun initView(root: View) {
        isCancelable = false
        mBinding.btnSure.setOnClickListener {
            mAuthCodeViewModel.validateAuthCode()
        }
        mBinding.btnGetCode.setOnClickListener {
            mAuthCodeViewModel.getCodeFromDisk()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_auth_code
    }

    override fun onBindViewModel2Layout(binding: DialogAuthCodeBinding) {
        binding.authCodeVM = mAuthCodeViewModel
    }

    override fun observeVM() {
        mAuthCodeViewModel.validateResult.observeOne(this){
            if(it){
                ToastUtils.showShort(getString(R.string.validate_success))
                dismiss()
            }else{
                ToastUtils.showShort(getString(R.string.validate_failed))
            }
        }
    }
}