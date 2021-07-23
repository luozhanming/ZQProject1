package cn.com.ava.zqproject.ui.videoResource

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoManageBinding

class VideoManageFragment : BaseFragment<FragmentVideoManageBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_video_manage
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}