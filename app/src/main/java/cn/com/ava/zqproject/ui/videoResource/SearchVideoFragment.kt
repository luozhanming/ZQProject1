package cn.com.ava.zqproject.ui.videoResource

import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.concurrent.schedule

class SearchVideoFragment : BaseFragment<FragmentSearchVideoBinding>() {

    private val mVideoManageViewModel by viewModels<VideoManageViewModel>()

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private var mSearchVideoItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    val args : SearchVideoFragmentArgs by navArgs()

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_video
    }

    override fun onBindViewModel2Layout(binding: FragmentSearchVideoBinding) {
        binding.searchVideoViewModel = mVideoManageViewModel
    }

    override fun initView() {
        super.initView()

        Timer().schedule(3000) {
            logd("开始加载")
            mVideoManageViewModel.getVideoResourceList()
        }

//        val videos: String = arguments?.get("videos").toString()
//        val list = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(videos, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
//        mVideoManageViewModel.videoResources.value = list

//        logd("搜索视频size: ${mSearchVideoViewModel.videoResources.value?.size}")

        mBinding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.btnClear.setOnClickListener {
            mBinding.etSearch.setText("")
        }

        if (mSearchVideoItemAdapter == null) {
            mSearchVideoItemAdapter = VideoResourceListItemAdapter(object : VideoResourceListItemAdapter.VideoResourceListCallback {
                override fun onDidClickedItem(data: RecordFilesInfo.RecordFile?) {
                    logd("查看")
                    findNavController().navigate(R.id.action_searchVideoFragment_to_videoPlayFragment,
                        Bundle().apply {
                            putString("rtspUrl", data?.rtspUrl)
                            putString("downloadFileName", data?.downloadFileName)
                        }
                    )
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
        }

        mBinding.rvResourceList.adapter = mSearchVideoItemAdapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun observeVM() {
        mVideoManageViewModel.searchKey.observe(viewLifecycleOwner) { key ->
            logd("searchKey: $key")
            mBinding.btnClear.isVisible = !TextUtils.isEmpty(key)
        }

        mVideoManageViewModel.filterVideos.observe(viewLifecycleOwner) {
            if (TextUtils.isEmpty(mVideoManageViewModel.searchKey.value)) {
                logd("清空搜索框")
                mSearchVideoItemAdapter?.setDatas(arrayListOf<RecordFilesInfo.RecordFile>())
            } else {
                logd("更新filter: $it")
                mSearchVideoItemAdapter?.setDatas(it)
            }
        }
    }

    override fun onDestroyView() {
        mBinding.rvResourceList.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}