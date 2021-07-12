package cn.com.ava.zqproject.ui.common

import android.view.Gravity
import android.view.View
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.zqproject.R

class LoadingDialog:BaseDialog() {
    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(300,200,Gravity.CENTER)
    }

    override fun initView(root: View) {

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_test
    }
}