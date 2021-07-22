package cn.com.ava.zqproject.ui.common

import android.view.Gravity
import android.view.View
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R

class LoadingDialog:BaseDialog() {
    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(300),200,Gravity.CENTER, true)
    }

    override fun initView(root: View) {

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_test
    }
}