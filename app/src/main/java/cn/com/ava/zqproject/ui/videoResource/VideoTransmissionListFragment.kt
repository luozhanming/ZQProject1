package cn.com.ava.zqproject.ui.videoResource

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoTransmissionListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoTransmissionListItemAdapter
import cn.com.ava.zqproject.vo.VideoResource

class VideoTransmissionListFragment : BaseFragment<FragmentVideoTransmissionListBinding>() {

    private val mVideoManageViewModel by viewModels<VideoManageViewModel>({ requireParentFragment() })

    private var mVideoTransmissionListItemAdapter by autoCleared<VideoTransmissionListItemAdapter>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_transmission_list
    }

    override fun initView() {
        super.initView()

        mVideoTransmissionListItemAdapter = VideoTransmissionListItemAdapter(object : VideoTransmissionListItemAdapter.VideoTransmissionCallback {
            override fun onDidClickedItem(data: RecordFilesInfo.RecordFile?) {
                logd("查看视频")
            }
        })

//        mVideoTransmissionListItemAdapter?.setDatas(mVideoTransmissions)
        mBinding.rvTransmissionList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvTransmissionList.adapter = mVideoTransmissionListItemAdapter
    }


    override fun observeVM() {
        mVideoManageViewModel.transmissionVideos.observe(viewLifecycleOwner) {
            logd("更新传输列表")
            mVideoTransmissionListItemAdapter?.setDatas(it)
        }
    }
}