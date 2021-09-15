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
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
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
import cn.com.ava.zqproject.ui.videoResource.dialog.SelectDiskDialog
import cn.com.ava.zqproject.ui.videoResource.service.DownloadService
import cn.com.ava.zqproject.usb.UsbHelper
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.concurrent.schedule

class SearchVideoFragment : BaseFragment<FragmentSearchVideoBinding>() {

    private val mVideoManageViewModel by activityViewModels<VideoManageViewModel>()

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private var mSearchVideoItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    val args : SearchVideoFragmentArgs by navArgs()

    // 应对申请完权限自动下载的需求的临时成员
    private var mTempRecordFile: RecordFilesInfo.RecordFile? = null

    // 下载服务
    private var mDownloadService: DownloadService? = null

    private val mDownloadServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                mDownloadService = p1?.let { (it as DownloadService.DownloadBinder).service }
                logd("下载服务")
//                mDownloadService?.registerDownloadCallback(object : DownloadService.DownloadCallback {
//                    override fun onDownloadStateChanged(info: ConcurrentMap<String, RecordFilesInfo.RecordFile>) {
//                        mVideoManageViewModel.refreshDownloadProgress(info)
//                    }
//                })
            }
            override fun onServiceDisconnected(p0: ComponentName?) {
                mDownloadService = null
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_video
    }

    override fun onBindViewModel2Layout(binding: FragmentSearchVideoBinding) {
        binding.searchVideoViewModel = mVideoManageViewModel
    }

    override fun initView() {
        super.initView()
        startService()
        bindService()

        Timer().schedule(1000) {
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
                override fun onDidClickedItem(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    logd("查看")
                    findNavController().navigate(R.id.action_searchVideoFragment_to_videoPlayFragment,
                        Bundle().apply {
                            putString("rtspUrl", data?.obj?.rtspUrl)
                            putString("downloadFileName", data?.obj?.downloadFileName)
                            putString("video", Gson().toJson(data?.obj))
                        }
                    )
                }

                override fun onDownload(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    logd("下载")
                    // 检测是否有下载缓存
                    if (mVideoManageViewModel.checkCacheResult(data?.obj!!)) {
                        ToastUtils.showShort("该视频已在下载队列")
                        return
                    }

                    val uDiskPath = UsbHelper.getUDiskPath()
                    if (uDiskPath.isEmpty()) {
                        ToastUtils.showShort(getString(R.string.tip_find_no_udisk))
                        return
                    } else if (uDiskPath.size > 1) {
                        ToastUtils.showShort(getString(R.string.tip_remain_only_one_udisk))
                        return
                    }

                    if (UsbHelper.getHelper().usbUri == null) {
                        val uDiskStorageVolume: StorageVolume?
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uDiskStorageVolume = UsbHelper.getUDiskStorageVolume()
                            if (uDiskStorageVolume != null) {
                                mTempRecordFile = data?.obj!!
                                val accessIntent = uDiskStorageVolume.createAccessIntent(null)
                                startActivityForResult(accessIntent, 3)
                            }
                        }
                        return
                    } else {
                        showSelectDiskDialog(data?.obj!!)
                    }
                }

                override fun onUpload(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    logd("上传")
                }

                override fun onDelete(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    logd("删除")
                }
            })
        }

        mBinding.rvResourceList.adapter = mSearchVideoItemAdapter
        mBinding.rvResourceList.layoutManager = LinearLayoutManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showSelectDiskDialog(data: RecordFilesInfo.RecordFile?) {

        val paths = UsbHelper.getUDiskPath()
        logd("U盘路径: ${paths.toString()}")

        val dialog = SelectDiskDialog(paths , { path ->
            logd("下载路径： $path")
            downloadVideo(data!!)
        })
        dialog.show(childFragmentManager, "")
    }

    // 下载视频
    fun downloadVideo(data: RecordFilesInfo.RecordFile) {

        mVideoManageViewModel.saveCacheVideo(arrayListOf(data))

        mDownloadService?.downloadVideo(data)
    }

    override fun observeVM() {
        mVideoManageViewModel.searchKey.observe(viewLifecycleOwner) { key ->
            logd("searchKey: $key")
            mBinding.btnClear.isVisible = !TextUtils.isEmpty(key)
        }

        mVideoManageViewModel.filterVideos.observe(viewLifecycleOwner) {
            if (TextUtils.isEmpty(mVideoManageViewModel.searchKey.value)) {
                logd("清空搜索框")
                mSearchVideoItemAdapter?.setDatas(arrayListOf<StatefulView<RecordFilesInfo.RecordFile>>())
            } else {
                logd("更新filter: $it")
                mSearchVideoItemAdapter?.setDatas(it)
            }
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            logd(data.toString())
            val data1 = data!!.data
            UsbHelper.getHelper().setUsbStorageUri(data1)
            showSelectDiskDialog(mTempRecordFile)
        }
    }

    override fun onDestroyView() {
        mBinding.rvResourceList.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        unbindService()
        super.onDestroy()
    }

}