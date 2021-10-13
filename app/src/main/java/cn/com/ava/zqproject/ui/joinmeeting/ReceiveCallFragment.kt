package cn.com.ava.zqproject.ui.joinmeeting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.common.util.Extra
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentReceiveCallingBinding
import cn.com.ava.zqproject.ui.BaseLoadingFragment
import cn.com.ava.zqproject.vo.InvitationInfo
import com.blankj.utilcode.util.ToastUtils

class ReceiveCallFragment : BaseLoadingFragment<FragmentReceiveCallingBinding>() {


    private val mReceiveCallViewModel by viewModels<ReceiveCallViewModel>()

    @Extra("invitationInfo")
    var mInvitationInfo:InvitationInfo?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_receive_calling
    }

    override fun onBindViewModel2Layout(binding: FragmentReceiveCallingBinding) {
        binding.receiveCallViewModel = mReceiveCallViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mReceiveCallViewModel.invitationInfo.value = mInvitationInfo
        mReceiveCallViewModel.startFinishCountDown()
    }

    override fun bindListener() {
        mBinding.btnCancel.setOnClickListener {
            mReceiveCallViewModel.step.value = 1
        }
        mBinding.btnReject.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.btnReceiveCall.setOnClickListener {
            mReceiveCallViewModel.step.value = 2
        }
        mBinding.btnJoin.setOnClickListener {
            mReceiveCallViewModel.joinMeeting()
        }
    }

    override fun observeVM() {
        mReceiveCallViewModel.isLoading.observeOne(viewLifecycleOwner){
            if(it)showLoading() else hideLoading()
        }
        mReceiveCallViewModel.joinSuccess.observeOne(viewLifecycleOwner){
            findNavController().popBackStack()
        }
        mReceiveCallViewModel.finishCall.observeOne(viewLifecycleOwner){
            findNavController().popBackStack()
        }
    }
}