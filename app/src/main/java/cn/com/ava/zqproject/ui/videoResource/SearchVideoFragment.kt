package cn.com.ava.zqproject.ui.videoResource

import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentSearchVideoBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import cn.com.ava.zqproject.vo.VideoResource

class SearchVideoFragment : BaseFragment<FragmentSearchVideoBinding>() {

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private val mVideoResources: List<VideoResource> by lazy {
        val datasource = arrayListOf<VideoResource>()
        datasource.add(VideoResource("1", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长汉东省党委常委....mp4", "2021-06-20 14:00:00", "35MB"))
        datasource.add(VideoResource("2", "工商联合会拜年会议暨民营企业家汉东省党委常委民主生活会会长.mp4", "2021-06-20 14:00:00", "35MB"))
        datasource
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_video
    }

    override fun initView() {
        super.initView()

        mBinding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.btnClear.setOnClickListener {
            mBinding.etSearch.setText("")
        }

        mVideoResourceListItemAdapter = VideoResourceListItemAdapter(object : VideoResourceListItemAdapter.VideoResourceListCallback {
            override fun onDidClickedItem(data: RecordFilesInfo.RecordFile?) {
                logd("查看")
            }

            override fun onDownload(data: RecordFilesInfo.RecordFile?) {
                logd("下载")
            }

            override fun onUpload(data: RecordFilesInfo.RecordFile?) {
                logd("上传")
            }

            override fun onDelete(data: RecordFilesInfo.RecordFile?) {
                logd("删除")
            }
        })
//        mVideoResourceListItemAdapter?.setDatas(mVideoResources)

        mBinding.rvResourceList.adapter = mVideoResourceListItemAdapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }

}