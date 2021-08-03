package cn.com.ava.zqproject.ui.videoResource

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import cn.com.ava.zqproject.ui.videoResource.dialog.DeleteVideoDialog
import cn.com.ava.zqproject.ui.videoResource.dialog.SelectDiskDialog
import cn.com.ava.zqproject.ui.videoResource.dialog.UploadVideoDialog
import cn.com.ava.zqproject.usb.UsbHelper
import cn.com.ava.zqproject.vo.VideoResource
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import java.util.jar.Manifest

class VideoResourceListFragment : BaseFragment<FragmentVideoResourceListBinding>() {

    private val mVideoManageViewModel by viewModels<VideoManageViewModel>({ requireParentFragment() })

    private var mVideoResourceListItemAdapter by autoCleared<VideoResourceListItemAdapter>()

    private val mVideoResourceListViewModel by viewModels<VideoResourceListViewModel>()

    private val usbHelper = UsbHelper.getHelper()

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
                    var permission = PermissionUtils.permission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    permission.callback(object : PermissionUtils.SingleCallback {
                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun callback(
                            isAllGranted: Boolean,
                            granted: MutableList<String>,
                            deniedForever: MutableList<String>,
                            denied: MutableList<String>
                        ) {
                            if (isAllGranted == true) {
                                logd("已同意用户申请权限, ${granted.toString()}")
                                showSelectDiskDialog(data)
                            } else {
                                logd("用户拒绝申请权限, deniedForever: ${deniedForever.toString()}, denied: ${denied.toString()}")
                            }
                        }
                    })
                    // 申请权限
                    permission.request()
                }

                override fun onUpload(data: RecordFilesInfo.RecordFile?) {
                    logd("上传")
                    val dialog = UploadVideoDialog({
                        logd("视频信息： $it")
                    })
                    dialog.show(childFragmentManager, "")
                }

                override fun onDelete(data: RecordFilesInfo.RecordFile?) {
                    val dialog = DeleteVideoDialog("您确定要删除该视频吗？", {
                        logd("删除")
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

        val dialog = SelectDiskDialog(paths , {
            logd("视频信息： $it")
        })
        dialog.show(childFragmentManager, "")
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
    }

    override fun onDestroy() {
        mBinding.rvResourceList.adapter = null
        super.onDestroy()
    }
}