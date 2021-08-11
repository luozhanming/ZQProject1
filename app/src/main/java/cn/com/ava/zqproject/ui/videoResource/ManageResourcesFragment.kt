package cn.com.ava.zqproject.ui.videoResource

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentManageResourcesBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.ManageResourceItemAdapter
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoResource
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ManageResourcesFragment : BaseFragment<FragmentManageResourcesBinding>() {

    private val mManageResourcesViewModel by viewModels<ManageResourcesViewModel>()

    private var mManageResourceItemAdapter by autoCleared<ManageResourceItemAdapter>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_manage_resources
    }

    override fun initView() {
        super.initView()

        val videos: String = arguments?.get("videos").toString()
        val list = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(videos, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)

        val statefuls = arrayListOf<StatefulView<RecordFilesInfo.RecordFile>>()
        for (video in list) {
            val stateful = StatefulView(video)
            statefuls.add(stateful)
        }
        mManageResourcesViewModel.videoResources.value = statefuls

        logd("视频size: ${mManageResourcesViewModel.videoResources.value?.size}")

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // 全选
        mBinding.btnSelectAll.setOnClickListener {
            mManageResourcesViewModel.selectedAllOrCancel(false)
        }

        // 取消全选
        mBinding.btnCancelSelectAll.setOnClickListener {
            mManageResourcesViewModel.selectedAllOrCancel(true)
        }

        // 下载
        mBinding.btnDownload.setOnClickListener {
            if (mManageResourcesViewModel.mSelectedVideos.size == 0) {
                ToastUtils.showShort("请勾选需要下载的视频")
                return@setOnClickListener
            }
            logd("下载视频: ${mManageResourcesViewModel.mSelectedVideos.size}")
        }

        // 删除
        mBinding.btnDelete.setOnClickListener {
            if (mManageResourcesViewModel.mSelectedVideos.size == 0) {
                ToastUtils.showShort("请勾选需要删除的视频")
                return@setOnClickListener
            }
            logd("删除视频: ${mManageResourcesViewModel.mSelectedVideos.size}")
        }

        if (mManageResourceItemAdapter == null) {
            mManageResourceItemAdapter = ManageResourceItemAdapter(object : ManageResourceItemAdapter.ManageResourceCallback {
                override fun onSelectVideo(
                    data: StatefulView<RecordFilesInfo.RecordFile>,
                    isSelected: Boolean
                ) {
                    logd("是否选择: ${isSelected.toString()}")
                    data.apply {
                        data.isSelected = isSelected
                        mManageResourceItemAdapter?.changeData(data)
                    }
                    mManageResourcesViewModel.addOrDelSelectedVideo(data.obj)
                }
            })
        }

        mBinding.rvResourceList.adapter = mManageResourceItemAdapter;
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun observeVM() {
        mManageResourcesViewModel.videoResources.observe(viewLifecycleOwner) {
            mManageResourceItemAdapter?.setDatas(it)
        }
        mManageResourcesViewModel.isSelectedAll.observe(viewLifecycleOwner) { isSelectedAll ->
            logd("是否全选, ${isSelectedAll.toString()}")
            mBinding.btnSelectAll.isVisible = !isSelectedAll
            mBinding.btnCancelSelectAll.isVisible = isSelectedAll
        }
    }

    override fun onDestroy() {
        mBinding.rvResourceList.adapter = null
        super.onDestroy()
    }

}