package cn.com.ava.zqproject.ui.videoResource

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.storage.StorageVolume
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentManageResourcesBinding
import cn.com.ava.zqproject.ui.BaseLoadingFragment
import cn.com.ava.zqproject.ui.videoResource.adapter.ManageResourceItemAdapter
import cn.com.ava.zqproject.ui.videoResource.dialog.DeleteVideoDialog
import cn.com.ava.zqproject.ui.videoResource.dialog.SelectDiskDialog
import cn.com.ava.zqproject.ui.videoResource.service.DownloadService
import cn.com.ava.zqproject.usb.UsbHelper
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils

class ManageResourcesFragment : BaseLoadingFragment<FragmentManageResourcesBinding>() {
    // 一次最多可选删除50条
    val MAX_DELETE_COUNT = 50

    private val mVideoManageViewModel by activityViewModels<VideoManageViewModel>()

    private var mManageResourceItemAdapter by autoCleared<ManageResourceItemAdapter>()

    // 下载服务
    private var mDownloadService: DownloadService? = null

    private val mDownloadServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                mDownloadService = p1?.let { (it as DownloadService.DownloadBinder).service }
            }
            override fun onServiceDisconnected(p0: ComponentName?) {
                mDownloadService = null
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_manage_resources
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mVideoManageViewModel.mSelectedVideos.clear()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView() {
        super.initView()
        startService()
        bindService()

        logd("视频size: ${mVideoManageViewModel.videoResources.value?.size}")

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // 全选
        mBinding.btnSelectAll.setOnClickListener {
            mVideoManageViewModel.selectedAllOrCancel(false)
        }

        // 取消全选
        mBinding.btnCancelSelectAll.setOnClickListener {
            mVideoManageViewModel.selectedAllOrCancel(true)
        }

        // 下载
        mBinding.btnDownload.setOnClickListener {
            if (mVideoManageViewModel.mSelectedVideos.size == 0) {
                ToastUtils.showShort("请勾选需要下载的视频")
                return@setOnClickListener
            }
            logd("下载视频: ${mVideoManageViewModel.mSelectedVideos.size}")
            // 检测是否有下载缓存

            var hasCache = false
            mVideoManageViewModel.mSelectedVideos.forEach {
                it.transmissionType = 1
                if (mVideoManageViewModel.checkCacheResult(it)) {
                    hasCache = true
                }
            }
            if (hasCache) {
                ToastUtils.showShort("部分视频已在下载队列")
                return@setOnClickListener
            }
            val uDiskPath = UsbHelper.getUDiskPath()
            if (uDiskPath.isEmpty()) {
                ToastUtils.showShort(getString(R.string.tip_find_no_udisk))
                return@setOnClickListener
            } else if (uDiskPath.size > 1) {
                ToastUtils.showShort(getString(R.string.tip_remain_only_one_udisk))
                return@setOnClickListener
            }

            if (UsbHelper.getHelper().usbUri == null) {
                val uDiskStorageVolume: StorageVolume?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uDiskStorageVolume = UsbHelper.getUDiskStorageVolume()
                    if (uDiskStorageVolume != null) {
                        val accessIntent = uDiskStorageVolume.createAccessIntent(null)
                        startActivityForResult(accessIntent, 3)
                    }
                }
                return@setOnClickListener
            } else {
                showSelectDiskDialog()
            }

        }

        // 删除
        mBinding.btnDelete.setOnClickListener {
            if (mVideoManageViewModel.mSelectedVideos.size == 0) {
                ToastUtils.showShort(R.string.tip_select_delete_videos)
                return@setOnClickListener
            }
            if (mVideoManageViewModel.mSelectedVideos.size > MAX_DELETE_COUNT) {
                ToastUtils.showShort(R.string.tip_max_delete_videos_count)
                return@setOnClickListener
            }
            val dialog = DeleteVideoDialog(getString(R.string.tip_delete_videos), {
                deleteVideos()
            })
            dialog.show(childFragmentManager, "")
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
                    mVideoManageViewModel.addOrDelSelectedVideo(data.obj)
                }
            })
        }

        mBinding.rvResourceList.adapter = mManageResourceItemAdapter;
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    fun deleteVideos() {
        logd("删除视频: ${mVideoManageViewModel.mSelectedVideos.size}")

        var count = mVideoManageViewModel.mSelectedVideos.size / 5
        for (i in 0..count) {
            var lastIndex = (i + 1) * 5
            if (i == count) { // 最后一页截到最后
                lastIndex = mVideoManageViewModel.mSelectedVideos.size
            }
            // subList不包括lastIndex选项
            val list = mVideoManageViewModel.mSelectedVideos.subList(i * 5, lastIndex).toList()
            logd("list.size = ${list.size}")
            if (list.size > 0) {
                mVideoManageViewModel.deleteVideo(list)
            }
        }
//        mVideoManageViewModel.deleteVideo(mVideoManageViewModel.mSelectedVideos.toList())
//        mVideoManageViewModel.mSelectedVideos.forEach {
//            mVideoManageViewModel.deleteVideo(it)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showSelectDiskDialog() {

        val paths = UsbHelper.getUDiskPath()
        logd("U盘路径: ${paths.toString()}")

        val dialog = SelectDiskDialog(paths , { path ->
            logd("下载路径： $path")
            downloadVideo()
        })
        dialog.show(childFragmentManager, "")
    }

    // 下载视频
    fun downloadVideo() {
        logd("下载视频")
        mVideoManageViewModel.mSelectedVideos.forEach {
            it.transmissionType = 1
            mDownloadService?.downloadVideo(it)
        }
        mVideoManageViewModel.saveCacheVideo(mVideoManageViewModel.mSelectedVideos)
    }

    override fun observeVM() {

        mVideoManageViewModel.videoResources.observe(viewLifecycleOwner) {
            mManageResourceItemAdapter?.setDatas(it)
        }
        mVideoManageViewModel.isSelectedAll.observe(viewLifecycleOwner) { isSelectedAll ->
            logd("是否全选, ${isSelectedAll.toString()}")
            mBinding.btnSelectAll.isVisible = !isSelectedAll
            mBinding.btnCancelSelectAll.isVisible = isSelectedAll
        }
        mVideoManageViewModel.isLoading.observeOne(viewLifecycleOwner){
            if(it)showLoading()
            else hideLoading()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            logd(data.toString())
            val data1 = data!!.data
            UsbHelper.getHelper().setUsbStorageUri(data1)
            showSelectDiskDialog()
        }
    }

    // 开启下载服务
    private fun startService() {
        try {
            requireActivity().startService(
                Intent(requireContext(), DownloadService::class.java)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 绑定下载服务
    private fun bindService() {
        try {
            requireActivity().bindService(
                Intent(requireContext(), DownloadService::class.java),
                mDownloadServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 解绑下载服务
    private fun unbindService() {
        try {
            requireActivity().unbindService(mDownloadServiceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        mBinding.rvResourceList.adapter = null
        mVideoManageViewModel.mSelectedVideos.clear()
        mVideoManageViewModel.videoResources.value?.forEach {
            it.isSelected = false
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        unbindService()
        super.onDestroy()
    }

}