package cn.com.ava.base.ui

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import cn.com.ava.common.extension.bindExtras
import cn.com.ava.common.util.EditTextUtils
import cn.com.ava.common.util.ScreenCompatUtil
import cn.com.ava.common.util.logd

abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity(), MVVMView<B> {

    protected val mBinding: B by lazy {
        val binding = DataBindingUtil.setContentView<B>(this, getLayoutId())
        binding
    }

    val mHandler: Handler by lazy {
        Handler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        logd("onCreate()")
        super.onCreate(savedInstanceState)
        mBinding.lifecycleOwner = this
        ScreenCompatUtil.initScreenCompat(this, application, true, 1280)
        bindExtras()
        onBindViewModel2Layout(mBinding)
        observeVM()
    }

    override fun onDestroy() {
        super.onDestroy()
        logd("onDestroy()")
    }


    override fun onBindViewModel2Layout(binding: B) {

    }

    override fun observeVM() {

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


    protected abstract fun getLayoutId(): Int


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (EditTextUtils.isShouldHideSoftKeyBoard(view, ev)) {
                EditTextUtils.hideSoftKeyBoard(view.windowToken, this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    open fun checkCurNavFragmentBackPressed(@IdRes navResId: Int): Boolean {
        try {
            val navHostFragment: NavHostFragment? =
                supportFragmentManager.findFragmentById(navResId) as NavHostFragment?
            if (navHostFragment != null) {
                val fragmentList: List<Fragment> =
                    navHostFragment.childFragmentManager.fragments
                if (fragmentList.isNotEmpty()) {
                    val curFragment = fragmentList[0]
                    if (curFragment is BaseFragment<*>) {
                        return curFragment.onBackPressed()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


}