package cn.com.ava.zqproject.ui

import android.os.Bundle
import androidx.activity.viewModels
import cn.com.ava.base.ui.BaseActivity
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ActivityMainBinding
import cn.com.ava.zqproject.ui.common.LoadingDialog

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
    }

    private val mMainViewModel by viewModels<MainViewModel>()

    private val mLuboViewModel by viewModels<LuBoShareViewModel>()

    /**
     *  加载对话框
     */
    private val mLoadingDialog by lazy {
        val dialog = LoadingDialog()
        dialog.setDismissCallback {
            mMainViewModel.isShowLoading.postValue(false)
        }
        dialog
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainViewModel.isShowLoading.observe(this) { show ->
            if (show) {
                if (!mLoadingDialog.isVisible) {
                    mLoadingDialog.show(supportFragmentManager, "loading1")
                }
            } else {
                mLoadingDialog.dismiss()
            }
        }

    }

    override fun onBackPressed() {
        if(!checkCurNavFragmentBackPressed(R.id.nav_host_fragment)){
            super.onBackPressed()
        }

    }


}