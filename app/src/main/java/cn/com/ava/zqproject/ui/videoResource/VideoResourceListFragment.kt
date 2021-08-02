package cn.com.ava.zqproject.ui.videoResource

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.common.util.loge
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoResourceListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import cn.com.ava.zqproject.ui.videoResource.dialog.UploadVideoDialog
import cn.com.ava.zqproject.vo.VideoResource
import com.blankj.utilcode.util.Utils

class VideoResourceListFragment : BaseFragment<FragmentVideoResourceListBinding>() {

    private val mVideoManageViewModel by viewModels<VideoManageViewModel>({ requireParentFragment() })

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private val mVideoResourceListViewModel by viewModels<VideoResourceListViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_resource_list
    }

    private val mVideoResources: List<VideoResource> by lazy {
        val datasource = arrayListOf<VideoResource>()
        datasource.add(VideoResource("1", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长汉东省党委常委....mp4", "2021-06-20 14:00:00", "35MB"))
        datasource.add(VideoResource("2", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长.mp4", "2021-06-20 14:00:00", "35MB"))
        datasource
    }

    override fun initView() {
        super.initView()

        mBinding.refreshLayout.setOnRefreshListener {
            mVideoManageViewModel.getVideoResourceList()
        }
        mBinding.refreshLayout.autoRefresh()
        if (mVideoResourceListItemAdapter == null) {
            mVideoResourceListItemAdapter = VideoResourceListItemAdapter(object : VideoResourceListItemAdapter.VideoResourceListCallback {
                override fun onDidClickedItem(data: RecordFilesInfo.RecordFile?) {
                    logd("查看")

                    findNavController().navigate(R.id.action_videoResourceFragment_to_videoPlayFragment,
                    Bundle().apply {
                        putString("rtspUrl", data?.rtspUrl)
                        putString("downloadFileName", data?.downloadFileName)
                    })
                }

                override fun onDownload(data: RecordFilesInfo.RecordFile?) {
                    if (data != null) {
                        logd("下载")
                        mVideoManageViewModel.downloadVideo(data)
                    }
                }

                override fun onUpload(data: RecordFilesInfo.RecordFile?) {
                    logd("上传")
                    val dialog = UploadVideoDialog({
                        logd("视频信息： $it")
                    })
                    dialog.show(childFragmentManager, "")
                }

                override fun onDelete(data: RecordFilesInfo.RecordFile?) {
                    logd("删除")
                }
            })
        }

//        mVideoResourceListItemAdapter?.setDatas(mVideoResources)

        mBinding.rvResourceList.adapter = mVideoResourceListItemAdapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun observeVM() {
        mVideoManageViewModel.videoResources.observe(viewLifecycleOwner) {
            logd("获取视频资源成功")
            mVideoResourceListItemAdapter?.setDatas(it)
        }
        mVideoManageViewModel.refreshState.observe(viewLifecycleOwner) {
            if (it.isRefresh) {
                mBinding.refreshLayout.finishRefresh(!it.hasError)
            }
        }
    }

    override fun onDestroy() {
        mBinding.rvResourceList.adapter = null
        super.onDestroy()
    }
}