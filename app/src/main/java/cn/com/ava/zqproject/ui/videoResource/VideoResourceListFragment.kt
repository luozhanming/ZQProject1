package cn.com.ava.zqproject.ui.videoResource

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoResourceListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import cn.com.ava.zqproject.vo.VideoResource
import com.blankj.utilcode.util.Utils

class VideoResourceListFragment : BaseFragment<FragmentVideoResourceListBinding>() {

    private val TAG = "VideoResourceListFragme"

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

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

        mVideoResourceListItemAdapter = VideoResourceListItemAdapter(object : VideoResourceListItemAdapter.VideoResourceListCallback {
            override fun onDidClickedItem(data: VideoResource?) {
                Log.d(TAG, "onDidClickedItem: 查看")
            }

            override fun onDownload(data: VideoResource?) {
                Log.d(TAG, "onDownload: 下载")
            }

            override fun onUpload(data: VideoResource?) {
                Log.d(TAG, "onDownload: 上传")
            }
        })
        mVideoResourceListItemAdapter?.setDatas(mVideoResources)

        mBinding.rvResourceList.adapter = mVideoResourceListItemAdapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }
}