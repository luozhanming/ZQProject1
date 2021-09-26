package cn.com.ava.zqproject.ui.videoResource

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentVideoManageBinding
import cn.com.ava.zqproject.ui.splash.SplashFragment
import cn.com.ava.zqproject.ui.videoResource.pop.VRManageMenuWindow
import cn.com.ava.zqproject.ui.videoResource.pop.VideoResourceSortMenuWindow
import cn.com.ava.zqproject.ui.videoResource.service.VideoSingleton
import cn.com.ava.zqproject.ui.videoResource.util.AESUtils
import cn.com.ava.zqproject.usb.UsbHelper
import com.blankj.utilcode.util.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoManageFragment : BaseFragment<FragmentVideoManageBinding>() {

    private val mVideoManageViewModel by activityViewModels<VideoManageViewModel>()
    // 排序弹框
    var sortWindow: VideoResourceSortMenuWindow ?= null
    // 批量管理弹框
    var batchManageWindow: VRManageMenuWindow ?= null

    private val mFragments: List<Fragment> by lazy {
        val fragments = arrayListOf<Fragment>()
        fragments.add(VideoResourceListFragment())
        fragments.add(VideoTransmissionListFragment())
        fragments
    }

    private val mFragmentTitles: List<String> by lazy {
        val titles = arrayListOf<String>()
        titles.add(Utils.getApp().getString(R.string.resource_list))
        titles.add(Utils.getApp().getString(R.string.transmission_list))
        titles
    }

    private var mTabLayoutMediator by autoCleared<TabLayoutMediator>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_manage
    }

    override fun initView() {
        super.initView()

        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@VideoManageFragment) {
                override fun getItemCount(): Int {
                    return mFragments.size
                }

                override fun createFragment(position: Int): Fragment {
                    return mFragments[position]
                }
            }
            // 预加载页面数
            offscreenPageLimit = 2
        }
        // 禁止滑动
        mBinding.viewPager.isUserInputEnabled = false

        mTabLayoutMediator = TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) {tab, position ->
            val textView = TextView(requireView().context)

            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(
                Utils.getApp().resources.getColor(R.color.color_318EF8),
                Utils.getApp().resources.getColor(R.color.color_222222)
            )
            val colorStateList = ColorStateList(states, colors)
            textView.text = mFragmentTitles[position]
            textView.textSize = 19f

            textView.setTextColor(colorStateList)
            textView.typeface = Typeface.DEFAULT_BOLD
            tab.customView = textView
        }
        mTabLayoutMediator?.attach()

        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                logd("position: ${tab?.position}")
                mBinding.btnClearAllRecord.visibility = if(tab?.position == 1) View.VISIBLE else View.GONE
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        // 返回
        mBinding.ivBack.setOnClickListener {

        }
        // 搜索
        mBinding.ivSearch.setOnClickListener {
            findNavController().navigate(
                VideoManageFragmentDirections.actionVideoFragmentToSearchFragment(
                    GsonUtil.toJson(mVideoManageViewModel.videoResources.value)
                )
            )
        }
        // 排序
        mBinding.ivSort.setOnClickListener {
            if (sortWindow == null) {
                sortWindow = VideoResourceSortMenuWindow(requireContext(), SizeUtils.dp2px(210), SizeUtils.dp2px(140))
            }
            sortWindow?.setIndex(mVideoManageViewModel.sortIndex)
            sortWindow?.sortBySize {
                logd("根据文件大小排序")
                mVideoManageViewModel.sortByFileSize()
                mVideoManageViewModel.sortIndex = 0
            }
            sortWindow?.sortByTime {
                logd("根据录制时间排序")
                mVideoManageViewModel.sortByRecordTime()
                mVideoManageViewModel.sortIndex = 1
            }
            if (sortWindow?.isShowing == false) {
                sortWindow?.showAsDropDown(it)
            } else {
                sortWindow?.dismiss()
            }
        }
        // 批量管理
        mBinding.ivManage.setOnClickListener {
            if (batchManageWindow == null) {
                batchManageWindow = VRManageMenuWindow(requireContext(), SizeUtils.dp2px(180), SizeUtils.dp2px(100))
            }
            batchManageWindow?.batchManage {
                findNavController().navigate(
                    VideoManageFragmentDirections.actionVideoFragmentToManageFragment(
                        GsonUtil.toJson(mVideoManageViewModel.videoResources.value)
                    )
                )
            }
            if (batchManageWindow?.isShowing == false) {
                batchManageWindow?.showAsDropDown(it)
            } else {
                batchManageWindow?.dismiss()
            }
        }
        // 清除全部记录
        mBinding.btnClearAllRecord.setOnClickListener {
            UsbHelper.getHelper().cancelAllTask()
            mVideoManageViewModel.clearAllCacheVideos()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        logd("videoManagerFragment onDestroyView")
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(VideoSingleton.cacheVideos))
        super.onDestroyView()
    }

    override fun onPause() {
        logd("videoManagerFragment onPause")
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(VideoSingleton.cacheVideos))
        super.onPause()
    }
}