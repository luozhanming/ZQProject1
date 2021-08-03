package cn.com.ava.zqproject.ui.common

import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R

class LoadingDialog():BaseDialog() {


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(400),SizeUtils.dp2px(200),Gravity.CENTER,false)
    }

    override fun initView(root: View) {

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_loading
    }


    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }
}