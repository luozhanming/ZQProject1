package cn.com.ava.zqproject.ui.videoResource

import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoTransmissionListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoTransmissionListItemAdapter
import cn.com.ava.zqproject.vo.VideoResource

class VideoTransmissionListFragment : BaseFragment<FragmentVideoTransmissionListBinding>() {

    private var mVideoTransmissionListItemAdapter by autoCleared<VideoTransmissionListItemAdapter>()

    private val mVideoTransmissions: List<VideoResource> by lazy {
        val datasource = arrayListOf<VideoResource>()
        datasource.add(VideoResource("1", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长汉东省党委常委....mp4", "2021-06-20 14:00:00", "35MB"))
        datasource.add(VideoResource("2", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长.mp4", "2021-06-20 14:00:00", "35MB"))
        datasource
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_transmission_list
    }

    override fun initView() {
        super.initView()

        mVideoTransmissionListItemAdapter = VideoTransmissionListItemAdapter(object : VideoTransmissionListItemAdapter.VideoTransmissionCallback {
            override fun onDidClickedItem(data: VideoResource?) {
                logd("查看视频")
            }
        })

        mVideoTransmissionListItemAdapter?.setDatas(mVideoTransmissions)
        mBinding.rvTransmissionList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvTransmissionList.adapter = mVideoTransmissionListItemAdapter
    }
}