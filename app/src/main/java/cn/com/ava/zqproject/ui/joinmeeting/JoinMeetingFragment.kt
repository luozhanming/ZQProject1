package cn.com.ava.zqproject.ui.joinmeeting

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentJoinMeetingBinding

class JoinMeetingFragment:BaseFragment<FragmentJoinMeetingBinding>() {

    private val mJoinMeetingViewModel by viewModels<JoinMeetingViewModel>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_join_meeting
    }


    override fun initView() {
        mBinding.btnJoin.setOnClickListener {

        }
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onBindViewModel2Layout(binding: FragmentJoinMeetingBinding) {
        binding.joinMeetingViewModel = mJoinMeetingViewModel
    }







}