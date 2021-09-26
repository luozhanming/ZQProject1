package cn.com.ava.zqproject.ui.joinmeeting

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentJoinMeetingBinding
import cn.com.ava.zqproject.ui.BaseLoadingFragment

class JoinMeetingFragment:BaseLoadingFragment<FragmentJoinMeetingBinding>() {

    private val mJoinMeetingViewModel by viewModels<JoinMeetingViewModel>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_join_meeting
    }


    override fun initView() {
        mBinding.btnJoin.setOnClickListener {
            mJoinMeetingViewModel.joinMeeting()
        }
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onBindViewModel2Layout(binding: FragmentJoinMeetingBinding) {
        binding.joinMeetingViewModel = mJoinMeetingViewModel
    }

    override fun observeVM() {
        mJoinMeetingViewModel.goListener.observeOne(viewLifecycleOwner){
            findNavController().navigate(R.id.action_joinMeetingFragment_to_listenerFragment)
        }
        mJoinMeetingViewModel.isLoading.observeOne(viewLifecycleOwner){
            if(it)showLoading()
            else hideLoading()
        }

    }







}