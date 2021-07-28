package cn.com.ava.zqproject.ui.videoResource

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentManageResourcesBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.ManageResourceItemAdapter
import cn.com.ava.zqproject.vo.VideoResource

class ManageResourcesFragment : BaseFragment<FragmentManageResourcesBinding>() {

    private var mManageResourceItemAdapter by autoCleared<ManageResourceItemAdapter>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_manage_resources
    }

    private val mVideoResources: List<VideoResource> by lazy {
        val datasource = arrayListOf<VideoResource>()
        datasource.add(VideoResource("1", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长汉东省党委常委....mp4", "2021-06-20 14:00:00", "35MB"))
        datasource.add(VideoResource("2", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长.mp4", "2021-06-20 14:00:00", "35MB"))
        datasource
    }

    override fun initView() {
        super.initView()

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        mManageResourceItemAdapter = ManageResourceItemAdapter(object : ManageResourceItemAdapter.ManageResourceCallback {
            override fun onDidClickedItem(data: VideoResource?) {
                logd("点击")
            }
        })
        mBinding.rvResourceList.adapter = mManageResourceItemAdapter;
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mManageResourceItemAdapter?.setDatas(mVideoResources)
    }


}