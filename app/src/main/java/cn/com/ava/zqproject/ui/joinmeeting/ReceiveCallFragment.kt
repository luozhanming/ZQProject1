package cn.com.ava.zqproject.ui.joinmeeting

import androidx.fragment.app.viewModels
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentReceiveCallingBinding
import cn.com.ava.zqproject.ui.BaseLoadingFragment

class ReceiveCallFragment : BaseLoadingFragment<FragmentReceiveCallingBinding>() {


    private val mReceiveCallViewModel by viewModels<ReceiveCallViewModel>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_receive_calling
    }

    override fun onBindViewModel2Layout(binding: FragmentReceiveCallingBinding) {
        binding.receiveCallViewModel = mReceiveCallViewModel
    }
}