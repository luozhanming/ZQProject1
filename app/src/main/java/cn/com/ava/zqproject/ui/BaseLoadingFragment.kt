package cn.com.ava.zqproject.ui

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
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
        //曾经崩溃，添加此行代码
        if (mLoadingDialog?.parentFragment == null) return
        mLoadingDialog?.dismiss()
    }

    override fun onDestroyView() {
        hideLoading()
        super.onDestroyView()
    }
}