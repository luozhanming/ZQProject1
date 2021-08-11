package cn.com.ava.zqproject.ui.login

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.util.WebViewUtil
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.databinding.FragmentLoginBinding
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.ui.common.power.PowerDialog
import cn.com.ava.zqproject.ui.common.power.PowerViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>() {


    private val mLoginViewModel by viewModels<LoginViewModel>()

    private var mLastHtmlUrl: String? = null

    private val mPowerViewModel by viewModels<PowerViewModel>()


    private val mWebView by lazy {
        val webView = WebViewUtil.getH5WebView()
        webView.addJavascriptInterface(mLoginViewModel, "androidObj")
        webView
    }

    override fun getLayoutId(): Int = R.layout.fragment_login


    override fun initView() {
        if (mWebView.parent == null) {
            mBinding.webViewContainer.addView(
                mWebView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
        val html = "${CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")}" +
                "${PlatformApiManager.getApiPath(PlatformApiManager.PATH_WEBVIEW_LOGIN)}"
        logd("Webview load ${html}")
        if (html != mLastHtmlUrl) {
            mWebView.loadUrl(
                html
            )
            mLastHtmlUrl = html
        }
        mBinding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_luBoSettingFragment)
        }

        mBinding.ivPower.setOnClickListener {
            val power = PowerDialog()
            power.show(childFragmentManager,"power")
        }

    }

    override fun observeVM() {
        mLoginViewModel.goHome.observe(viewLifecycleOwner) { it ->
            if (it != 0) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
        val goSetting = { it: Boolean ->
            //退出登录
            LoginManager.logout()
            findNavController().navigate(R.id.action_back_to_lubo_setting,null, navOptions {
                popUpTo(R.id.loginFragment){
                    inclusive = true
                }
            })
        }
        mPowerViewModel.reloadMachine.observe(viewLifecycleOwner, goSetting)
        mPowerViewModel.sleepMachine.observe(viewLifecycleOwner, goSetting)
        mPowerViewModel.turnoffMachine.observe(viewLifecycleOwner, goSetting)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mWebView.parent?.apply {
            if (this is ViewGroup) {
                this.removeAllViews()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        WebViewUtil.destroy(mWebView)
    }

}