package cn.com.ava.base.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.com.ava.common.util.ScreenCompatUtil

abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    protected val mBinding: B by lazy {
        val binding = DataBindingUtil.setContentView<B>(this, getLayoutId())
        binding
    }

    protected val mHandler: Handler by lazy {
        Handler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenCompatUtil.initScreenCompat(this, application, true, 360)
        mBinding.lifecycleOwner = this
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


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    protected abstract fun getLayoutId(): Int


}