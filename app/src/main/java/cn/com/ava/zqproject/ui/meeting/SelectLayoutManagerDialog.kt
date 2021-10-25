package cn.com.ava.zqproject.ui.meeting

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogSelectLayoutBinding
import cn.com.ava.zqproject.ui.meeting.adapter.AutoPatrolMemberAdapter
import cn.com.ava.zqproject.ui.meeting.adapter.SelectLayoutSignalAdapter
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils

class SelectLayoutManagerDialog(val onSure: (() -> Unit)? = null) :
    BaseDialogV2<DialogSelectLayoutBinding>() {

    private val mSelectLayoutViewModel by viewModels<SelectLayoutManagerViewModel>()

    private var mSelectLayoutSignalAdapter by autoCleared<SelectLayoutSignalAdapter>()

    private var mSelectSignalPopupWindow by autoCleared<SignalSelectPopupWindow>()

    private var mAutoPatrolMemberAdapter by autoCleared<AutoPatrolMemberAdapter>()

    private val mMasterViewModel by viewModels<MasterViewModel>({ requireParentFragment() })


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(720),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
    }

    override fun initView(root: View) {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnCancel.setOnClickListener { dismiss() }
        val layoutClicked = View.OnClickListener {
            when (it.id) {
                R.id.iv_layout_auto -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_AUTO
                R.id.iv_layout_1 -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_1
                R.id.iv_layout_2 -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_2
                R.id.iv_layout_3 -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_3
                R.id.iv_layout_4 -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_4
                R.id.iv_layout_6 -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_6
                R.id.iv_layout_8 -> mSelectLayoutViewModel.layoutSelect.value =
                    SelectLayoutManagerViewModel.LAYOUT_8
            }
            mSelectLayoutViewModel.getInteracMemberInfo()
        }
        mBinding.ivLayoutAuto.setOnClickListener(layoutClicked)
        mBinding.ivLayout1.setOnClickListener(layoutClicked)
        mBinding.ivLayout2.setOnClickListener(layoutClicked)
        mBinding.ivLayout3.setOnClickListener(layoutClicked)
        mBinding.ivLayout4.setOnClickListener(layoutClicked)
        mBinding.ivLayout6.setOnClickListener(layoutClicked)
        mBinding.ivLayout8.setOnClickListener(layoutClicked)
        mBinding.btnNextStep.setOnClickListener {
            val selectLayout = mSelectLayoutViewModel.layoutSelect.value!!
            if (selectLayout == SelectLayoutManagerViewModel.LAYOUT_AUTO) {
                if (mSelectLayoutViewModel.linkUsers.value?.size ?: 0 <= 1) {  //不足一个无法是使用轮播
                    ToastUtils.showShort(getString(R.string.toast_no_need_patrol))
                    return@setOnClickListener
                }
                mSelectLayoutViewModel.step.value = SelectLayoutManagerViewModel.STEP_2_2
            } else if(selectLayout!=-1){
                mSelectLayoutViewModel.step.value = SelectLayoutManagerViewModel.STEP_2_1
            }
        }
        mBinding.btnLastStep.setOnClickListener {
            mSelectLayoutViewModel.step.value = SelectLayoutManagerViewModel.STEP_1
        }
        mBinding.rvLayoutSignal.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mSelectLayoutSignalAdapter = SelectLayoutSignalAdapter { index, view ->
            // 在View下弹出选择框
            mSelectSignalPopupWindow?.showAsDropDown(index, view, 0, 0)
        }
        mBinding.rvLayoutSignal.adapter = mSelectLayoutSignalAdapter

        mSelectSignalPopupWindow =
            mSelectSignalPopupWindow ?: SignalSelectPopupWindow(requireContext()) { i, user ->
                mSelectLayoutViewModel.selectSignal(i, user)
                mSelectSignalPopupWindow?.dismiss()
            }
        mBinding.btnSure.setOnClickListener {
            val layoutMode = mSelectLayoutViewModel.layoutSelect.value ?: -1
            val period = mSelectLayoutViewModel.patrolPeriod.value ?: "10"
            if (layoutMode == SelectLayoutManagerViewModel.LAYOUT_AUTO) {
                //   mSelectLayoutViewModel.patrolSure(mAutoPatrolMemberAdapter?.getSelectedUser()?: emptyList())
                if (period.toInt() < 1 || period.toInt() > 3600) {
                    ToastUtils.showShort("请输入合理的轮播间隔")
                    return@setOnClickListener
                }
                if (mAutoPatrolMemberAdapter?.getSelectedUser()?.size ?: 0 < 2) {
                    ToastUtils.showShort("请选择大于2个需要轮播的成员")
                    return@setOnClickListener
                }
                mMasterViewModel.beginPatrol(
                    mAutoPatrolMemberAdapter?.getSelectedUser(),
                    period.toInt()
                )
                dismiss()
            } else {
                mSelectLayoutViewModel.layoutSure()
                mMasterViewModel.cancelPatrol()
            }
            onSure?.invoke()

        }
        mBinding.rvSelectWhatPatrol.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mAutoPatrolMemberAdapter = AutoPatrolMemberAdapter()
        mBinding.rvSelectWhatPatrol.adapter = mAutoPatrolMemberAdapter
        mSelectLayoutViewModel.getInteracMemberInfo()
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_select_layout
    }

    override fun onBindViewModel2Layout(binding: DialogSelectLayoutBinding) {
        binding.selectLayoutViewModel = mSelectLayoutViewModel
    }

    override fun observeVM() {
        mSelectLayoutViewModel.layoutSignals.observe(this) {
            mSelectLayoutSignalAdapter?.setDatas(it)
        }
        mSelectLayoutViewModel.unSelectSignals.observe(this) {
            mSelectSignalPopupWindow?.setDatas(it)
        }
        mSelectLayoutViewModel.layoutSure.observeOne(this) {
            dismiss()
        }
        mSelectLayoutViewModel.linkUsers.observe(this) {
            val statefuls = arrayListOf<StatefulView<LinkedUser>>()
            it.forEach {
                statefuls.add(StatefulView(it))
            }
            mAutoPatrolMemberAdapter?.setDatas(
                statefuls
            )
        }

    }
}