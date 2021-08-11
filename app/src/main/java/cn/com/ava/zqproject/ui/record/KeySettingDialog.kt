package cn.com.ava.zqproject.ui.record

import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommandKeyHelper
import cn.com.ava.zqproject.databinding.DialogKeySettingBinding
import cn.com.ava.zqproject.ui.record.adapter.VideoLayoutAdapter
import cn.com.ava.zqproject.ui.record.adapter.VideoWindowAdapter
import cn.com.ava.zqproject.ui.record.adapter.WindowPresetAdapter
import cn.com.ava.zqproject.vo.LayoutButton
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoPresetButton
import cn.com.ava.zqproject.vo.VideoWindowButton
import com.blankj.utilcode.util.ToastUtils
/**
 * 按键设置对话框
 * */
class KeySettingDialog : BaseDialogV2<DialogKeySettingBinding>() {

    private val mKeySettingViewModel by viewModels<KeySettingViewModel>()

    private var mVideoWindowAdapter by autoCleared<VideoWindowAdapter>()

    private var mLayoutWindowAdapter by autoCleared<VideoLayoutAdapter>()

    private var mPresetWindowAdapter by autoCleared<WindowPresetAdapter>()


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(1136), SizeUtils.dp2px(688), Gravity.CENTER, true)
    }

    override fun initView(root: View) {
        mBinding.rvVideoWindow.isNestedScrollingEnabled = true
        mBinding.rvVideoWindow.layoutManager =
            GridLayoutManager(requireContext(), 6, GridLayoutManager.VERTICAL, false)
        if (mVideoWindowAdapter == null) {
            mVideoWindowAdapter =
                VideoWindowAdapter(object : VideoWindowAdapter.OnVideoWindowCallback {
                    override fun onClicked(view: StatefulView<VideoWindowButton>) {
                        if (mKeySettingViewModel.addSelectButton(view.obj)) {  //添加成功
                            view.isSelected = !view.isSelected
                            mVideoWindowAdapter?.changeData(view)
                        }else{
                            ToastUtils.showShort(getString(R.string.toast_max_button_add))
                        }
                    }
                })
        }
        mBinding.rvVideoWindow.adapter = mVideoWindowAdapter

        mBinding.rvVideoLayout.isNestedScrollingEnabled = true
        mBinding.rvVideoLayout.layoutManager =
            GridLayoutManager(requireContext(), 6, GridLayoutManager.VERTICAL, false)
        if (mLayoutWindowAdapter == null) {
            mLayoutWindowAdapter = VideoLayoutAdapter(object :VideoLayoutAdapter.VideoLayoutCallback{
                override fun onVideoLayoutSelected(button: StatefulView<LayoutButton>) {
                    if (mKeySettingViewModel.addSelectButton(button.obj)) {  //添加成功
                        button.isSelected = !button.isSelected
                        mLayoutWindowAdapter?.changeData(button)
                    }else{
                        ToastUtils.showShort(getString(R.string.toast_max_button_add))
                    }
                }

            })
        }
        mBinding.rvVideoLayout.adapter = mLayoutWindowAdapter


        mBinding.rvPreset.isNestedScrollingEnabled = true
        mBinding.rvPreset.layoutManager =
            LinearLayoutManager(requireContext(), GridLayoutManager.VERTICAL, false)
        if (mPresetWindowAdapter == null) {
            mPresetWindowAdapter = WindowPresetAdapter(object :WindowPresetAdapter.WindowPresetCallback{
                override fun onPresetClicked(button: StatefulView<VideoPresetButton>) {
                    if (mKeySettingViewModel.addSelectButton(button.obj)) {  //添加成功
                        button.isSelected = !button.isSelected
                        //TODO 以下代码虽然效果达成，但是执行性能不行
                        mPresetWindowAdapter?.notifyDataSetChanged()
                    }else{
                        ToastUtils.showShort(getString(R.string.toast_max_button_add))
                    }
                }

            })
        }
        mBinding.rvPreset.adapter = mPresetWindowAdapter


        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnCancel.setOnClickListener {
            dismiss()
            val buttons = CommandKeyHelper.getSelectedCommandKeys()
            if(!buttons.isEmpty()){
                val dialog = KeyControlDialog()
                dialog.show(requireParentFragment().childFragmentManager,"key_control")
            }
        }
        mBinding.btnSure.setOnClickListener {
            //保存选中按钮
            val buttons = mKeySettingViewModel.getSelectedButton()
            logd("保存按钮：${buttons.toString()}")
            CommandKeyHelper.setSelectedCommandKeys(buttons)
            dismiss()
            //显示按钮控制对话框
            val dialog = KeyControlDialog()
            dialog.show(requireParentFragment().childFragmentManager,"key_control")
        }
        mKeySettingViewModel.getVideoWindowList()
        mKeySettingViewModel.getVideoLayoutList()
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_key_setting
    }

    override fun onBindViewModel2Layout(binding: DialogKeySettingBinding) {

    }

    override fun observeVM() {
        mKeySettingViewModel.videoWindowList.observe(this) {
            mVideoWindowAdapter?.setDatasWithDiff(it)
        }
        mKeySettingViewModel.videoLayoutList.observe(this) {
            mLayoutWindowAdapter?.setDatasWithDiff(it)
        }
        mKeySettingViewModel.presetWindows.observe(this) {
            mPresetWindowAdapter?.setDatasWithDiff(it)
        }
    }
}