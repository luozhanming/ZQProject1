package cn.com.ava.zqproject.ui.videoResource

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoTransmissionListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoTransmissionListItemAdapter
import cn.com.ava.zqproject.ui.videoResource.dialog.DeleteVideoDialog
import cn.com.ava.zqproject.vo.VideoResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VideoTransmissionListFragment : BaseFragment<FragmentVideoTransmissionListBinding>() {

    private val mVideoManageViewModel by viewModels<VideoManageViewModel>({ requireParentFragment() })

    private var mVideoTransmissionListItemAdapter by autoCleared<VideoTransmissionListItemAdapter>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_transmission_list
    }

    override fun initView() {
        super.initView()

        if (mVideoManageViewModel.transmissionVideos.value?.size == 0) { // 初始化数据
            mVideoManageViewModel.transmissionVideos.value = mVideoManageViewModel.getCacheVideos()
        }

        mVideoTransmissionListItemAdapter = VideoTransmissionListItemAdapter(object : VideoTransmissionListItemAdapter.VideoTransmissionCallback {
            override fun onDidClickedItem(data: RecordFilesInfo.RecordFile?) {
                logd("查看视频")

                findNavController().navigate(R.id.action_videoResourceFragment_to_videoPlayFragment,
                    Bundle().apply {
                        putString("rtspUrl", data?.rtspUrl)
                        putString("downloadFileName", data?.downloadFileName)
                        putString("video", Gson().toJson(data))
                    }
                )
            }

            override fun onDelete(data: RecordFilesInfo.RecordFile?) {
                val dialog = DeleteVideoDialog("您确定要删除该视频吗？", {
                    mVideoManageViewModel.deleteCacheVideo(data!!)
                })
                dialog.show(childFragmentManager, "")
            }
        })

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