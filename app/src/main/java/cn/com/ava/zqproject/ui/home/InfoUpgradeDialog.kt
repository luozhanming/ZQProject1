package cn.com.ava.zqproject.ui.home

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogMachineInfoBinding
import cn.com.ava.zqproject.ui.common.ConfirmDialog
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.google.android.material.tabs.TabLayout

class InfoUpgradeDialog : BaseDialogV2<DialogMachineInfoBinding>(),
    TabLayout.OnTabSelectedListener {


    private val mInfoUpgradeViewModel by viewModels<InfoUpgradeViewModel>()

    private var mUpgradeConfirmDialog by autoCleared<ConfirmDialog>()

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(620), SizeUtils.dp2px(400), Gravity.CENTER, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInfoUpgradeViewModel.loadMachineInfo()
    }

    override fun initView(root: View) {
        initTabLayout()
        mBinding.tabLayout.addOnTabSelectedListener(this)

    }

    private fun initTabLayout() {
        mBinding.tabLayout.addOnTabSelectedListener(this)
        val tabNames =
            arrayOf(getString(R.string.machine_info), getString(R.string.software_upgrade))
        tabNames.forEach {
            val tab = mBinding.tabLayout.newTab()
            val textView = TextView(context)

            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(
                Utils.getApp().resources.getColor(R.color.color_318EF8),
                Utils.getApp().resources.getColor(R.color.color_222222)
            )
            val colorStateList = ColorStateList(states, colors)
            textView.text = it
            textView.textSize = 19f

            textView.setTextColor(colorStateList)
            textView.typeface = Typeface.DEFAULT_BOLD
            tab.customView = textView
            mBinding.tabLayout.addTab(tab)
        }
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnUpgrade.setOnClickListener {
            mInfoUpgradeViewModel.requestNewVersion()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_machine_info
    }

    override fun onBindViewModel2Layout(binding: DialogMachineInfoBinding) {
        binding.infoUpgradeViewModel = mInfoUpgradeViewModel
    }

    override fun observeVM() {
        mInfoUpgradeViewModel.hasNewVersion.observe(this){
            if(it){
                mUpgradeConfirmDialog = mUpgradeConfirmDialog?:ConfirmDialog(getString(R.string.software_upgrade),true,
                    {

                    },{
                        it?.dismiss()
                    })
                if(mUpgradeConfirmDialog?.isAdded==false){
                    mUpgradeConfirmDialog?.show(childFragmentManager,"upgrade")
                }
            }else{
                ToastUtils.showShort(getString(R.string.no_upgrade_message))
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        if (tab.position == 0) {
            mBinding.group1.visibility = View.VISIBLE
            mBinding.group2.visibility = View.GONE
        } else {
            mBinding.group1.visibility = View.GONE
            mBinding.group2.visibility = View.VISIBLE
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    override fun onDestroy() {
        mBinding.tabLayout.removeOnTabSelectedListener(this)
        super.onDestroy()
    }


}