package cn.com.ava.zqproject.ui.videoResource

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoResourceListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import com.blankj.utilcode.util.Utils

class VideoResourceListFragment : BaseFragment<FragmentVideoResourceListBinding>() {

    private val TAG = "VideoResourceListFragme"
    
    override fun getLayoutId(): Int {
        return R.layout.fragment_video_resource_list
    }

    private val mFragmentTitles: List<String> by lazy {
        val titles = arrayListOf<String>()
        titles.add(Utils.getApp().getString(R.string.resource_list))
        titles.add(Utils.getApp().getString(R.string.transmission_list))
        titles
    }

    override fun initView() {
        super.initView()

        val adapter = VideoResourceListItemAdapter(mFragmentTitles, object : VideoResourceListItemAdapter.VideoResourceListCallback {
            override fun onCancel(data: String?) {
                Log.d(TAG, "onCancel: 哈哈")
            }
        })
        adapter.setDatas(mFragmentTitles)

        mBinding.rvResourceList.adapter = adapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }
}