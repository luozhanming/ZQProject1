package cn.com.ava.zqproject.ui

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.ui.common.LoadingDialog

abstract class BaseLoadingFragment<B : ViewDataBinding> : BaseFragment<B>() {

    private var mLoadingDialog by autoCleared<LoadingDialog>()


    fun showLoading() {
        mLoadingDialog = mLoadingDialog ?: LoadingDialog()
        if (mLoadingDialog?.isAdded == false) {
            mLoadingDialog?.show(childFragmentManager, "loading")
        }
    }

    fun hideLoading() {
        mLoadingDialog = mLoadingDialog ?: LoadingDialog()
        mLoadingDialog?.dismiss()
    }
}