package cn.com.ava.base.ui

import android.os.Bundle
import android.os.Handler
import android.view.View
import cn.com.ava.common.extension.bindExtras
import cn.com.ava.common.util.ScreenCompatUtil
import com.luozm.mvplibrary.BaseMVPActivity
import com.luozm.mvplibrary.BasePresenter
import com.luozm.mvplibrary.IPresenter
import com.luozm.mvplibrary.IView

abstract class BaseActivityV2<P:IPresenter>:BaseMVPActivity<P>() {
    protected val mHandler: Handler by lazy {
        Handler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenCompatUtil.initScreenCompat(this, application, true, 360)
        bindExtras()
    }

    override fun onStart() {
        super.onStart()
        //设置沉浸式
        setWindowImmersive(true)
    }

    private fun setWindowImmersive(immersive: Boolean) {
        val setUI: () -> Unit = {
            val systemUIVisibility: Int = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            window.decorView.systemUiVisibility = systemUIVisibility
        }
        setUI()
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                setUI()
            }
        }
    }
}