package cn.com.ava.zqproject.ui.videoResource

import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.storage.StorageVolume
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.entity.TransmissionProgressEntity
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoResourceListBinding
import cn.com.ava.zqproject.ui.videoResource.adapter.VideoResourceListItemAdapter
import cn.com.ava.zqproject.ui.videoResource.dialog.DeleteVideoDialog
import cn.com.ava.zqproject.ui.videoResource.dialog.SelectDiskDialog
import cn.com.ava.zqproject.ui.videoResource.dialog.UploadVideoDialog
import cn.com.ava.zqproject.ui.videoResource.service.DownloadService
import cn.com.ava.zqproject.ui.videoResource.service.VideoSingleton
import cn.com.ava.zqproject.usb.UsbHelper
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import java.util.concurrent.ConcurrentMap

class VideoResourceListFragment : BaseFragment<FragmentVideoResourceListBinding>() {

    private val mVideoManageViewModel by viewModels<VideoManageViewModel>({ requireActivity() })

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private val mVideoResourceListViewModel by viewModels<VideoResourceListViewModel>()

    private val usbHelper = UsbHelper.getHelper()

    // 应对申请完权限自动下载的需求的临时成员
    private var mTempRecordFile: RecordFilesInfo.RecordFile? = null

    // 下载服务
    private var mDownloadService: DownloadService? = null

    private var mDownloadServiceConnection :ServiceConnection? =null



    override fun getLayoutId(): Int {
        return R.layout.fragment_video_resource_list
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }




    override fun onResume() {
        super.onResume()
        logd("listfragment onResume")
    }

    override fun initView() {
        super.initView()
        startService()
        bindService()
        logd("listfragment initView")

        mBinding.refreshLayout.setOnRefreshListener {
            mVideoManageViewModel.getVideoResourceList()
        }
        mBinding.refreshLayout.autoRefresh()
        if ((mVideoManageViewModel.videoResources.value?.size ?: 0) > 0) {
            mVideoManageViewModel.getVideoResourceList()
        }
        if (mVideoResourceListItemAdapter == null) {
            mVideoResourceListItemAdapter = VideoResourceListItemAdapter(object : VideoResourceListItemAdapter.VideoResourceListCallback {
                override fun onDidClickedItem(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    logd("查看")

                    findNavController().navigate(R.id.action_videoResourceFragment_to_videoPlayFragment,
                        Bundle().apply {
                            putString("rtspUrl", data?.obj?.rtspUrl)
                            putString("downloadFileName", data?.obj?.downloadFileName)
                            putString("video", Gson().toJson(data?.obj))
                        }
                    )
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDownload(data: StatefulView<RecordFilesInfo.RecordFile>?) {
//                    var permission = PermissionUtils.permission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    permission.callback(object : PermissionUtils.SingleCallback {
//                        @RequiresApi(Build.VERSION_CODES.N)
//                        override fun callback(
//                            isAllGranted: Boolean,
//                            granted: MutableList<String>,
//                            deniedForever: MutableList<String>,
//                            denied: MutableList<String>
//                        ) {
//                            if (isAllGranted == true) {
//                                logd("已同意用户申请权限, ${granted.toString()}")
//                                showSelectDiskDialog(data)
//                            } else {
//                                logd("用户拒绝申请权限, deniedForever: ${deniedForever.toString()}, denied: ${denied.toString()}")
//                            }
//                        }
//                    })
//                    // 申请权限
//                    permission.request()

                    val video = RecordFilesInfo.RecordFile(data?.obj!!)
                    video.transmissionType = 1
                    // 检测是否有下载缓存
                    if (mVideoManageViewModel.checkCacheResult(video)) {
                        ToastUtils.showShort(getString(R.string.tip_download_exist))
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
                                mTempRecordFile = video
                                val accessIntent = uDiskStorageVolume.createAccessIntent(null)
                                startActivityForResult(accessIntent, 3)
                            }
                        }
                        return
                    } else {
                        showSelectDiskDialog(video)
                    }
                }

                override fun onUpload(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    logd("上传")
                    val video = RecordFilesInfo.RecordFile(data?.obj!!)
                    video.transmissionType = 2
                    // 检测是否有上传缓存
                    if (mVideoManageViewModel.checkCacheResult(video)) {
                        ToastUtils.showShort(getString(R.string.tip_upload_exist))
                        return
                    }
                    val dialog = UploadVideoDialog({
                        logd("视频信息： $it")
                        mDownloadService?.uploadVideo(video, "it")
                    })
                    dialog.show(childFragmentManager, "")
                }

                override fun onDelete(data: StatefulView<RecordFilesInfo.RecordFile>?) {
                    val dialog = DeleteVideoDialog(getString(R.string.tip_delete_video), {
                        logd("删除")
                        mVideoManageViewModel.deleteVideo(data?.obj!!)
                    })
                    dialog.show(childFragmentManager, "")
                }
            })
        }

        mBinding.rvResourceList.adapter = mVideoResourceListItemAdapter
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
        mVideoManageViewModel.videoResources.observe(viewLifecycleOwner) {
            mVideoResourceListItemAdapter?.setDatas(it)
        }
        mVideoManageViewModel.refreshState.observe(viewLifecycleOwner) {
            if (it.isRefresh) {
                mBinding.refreshLayout.finishRefresh(!it.hasError)
            }
        }
//        mVideoManageViewModel.uploadFileNames.observe(viewLifecycleOwner) {
//            mDownloadService?.uploadFileNames = it
//        }
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
            mDownloadServiceConnection =  mDownloadServiceConnection?:  object : ServiceConnection {
                override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                    mDownloadService = p1?.let { (it as DownloadService.DownloadBinder).service }
                    logd("下载服务")
                    mDownloadService?.registerDownloadCallback(object : DownloadService.DownloadCallback {
                        override fun onDownloadStateChanged(info: ConcurrentMap<String, RecordFilesInfo.RecordFile>) {
                            mVideoManageViewModel.refreshDownloadProgress(info)
                        }

                        override fun onSubmitUploadCallback(
                            isSuccess: Boolean,
                            msg: String,
                            data: RecordFilesInfo.RecordFile
                        ) {
                            ToastUtils.showShort(msg)
                            if (isSuccess) { // 提交上传成功
                                mVideoManageViewModel.transmissionVideos.value = VideoSingleton.cacheVideos
//                            mVideoManageViewModel.saveCacheVideo(arrayListOf(data))
                            }
                        }

                        override fun onUploadStateChanged(info: ConcurrentMap<String, TransmissionProgressEntity>) {
                            logd("onUploadStateChanged thread = ${Thread.currentThread()}")
                            mVideoManageViewModel.transmissionVideos.postValue(VideoSingleton.cacheVideos)
//                        mVideoManageViewModel.refreshUploadProgress(info)
                        }
                    })
                }
                override fun onServiceDisconnected(p0: ComponentName?) {

                }
            }
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
            mDownloadServiceConnection=null
            mDownloadService?.unRegisterDownloadCallback()
            mDownloadService = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == RESULT_OK) {
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