package cn.com.ava.zqproject.ui.videoResource

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentSearchVideoBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter

class SearchVideoFragment : BaseFragment<FragmentSearchVideoBinding>() {

    private val mSearchVideoViewModel by viewModels<SearchVideoViewModel>()

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private var mSearchVideoItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    val args : SearchVideoFragmentArgs by navArgs()

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_video
    }

    override fun onBindViewModel2Layout(binding: FragmentSearchVideoBinding) {

    }

    override fun initView() {
        super.initView()

//        val video = args.videos;
//        logd(video.toString())
        val videos: String = arguments?.get("videos").toString()
        logd(videos)
//        GsonUtil.fromJson(videos, List<RecordFilesInfo.RecordFile>)

        mBinding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.btnClear.setOnClickListener {
            mBinding.etSearch.setText("")
        }

        mSearchVideoItemAdapter = VideoResourceListItemAdapter(object : VideoResourceListItemAdapter.VideoResourceListCallback {
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

        mBinding.rvResourceList.adapter = mSearchVideoItemAdapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun observeVM() {
        mSearchVideoViewModel.searchKey.observe(viewLifecycleOwner) {

        }
    }

    override fun onDestroy() {
        mBinding.rvResourceList.adapter = null
        super.onDestroy()
    }

}