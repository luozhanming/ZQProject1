package cn.com.ava.zqproject.ui.createmeeting

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentCreateMeetingBinding
import cn.com.ava.zqproject.ui.common.ConfirmDialog
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.google.android.material.tabs.TabLayoutMediator


class CreateMeetingFragment : BaseFragment<FragmentCreateMeetingBinding>() {


    private val mCreateMeetingViewModel by viewModels<CreateMeetingViewModel>()


    private val mFragments: List<Fragment> by lazy {
        val fragments = arrayListOf<Fragment>()
        fragments.add(RecentCallFragment())
        fragments.add(ContractBookFragment())
        fragments.add(ContractGroupFragment())
        fragments
    }

    private val mFragmentTitles: List<String> by lazy {
        val titles = arrayListOf<String>()
        titles.add(Utils.getApp().getString(R.string.recent_call))
        titles.add(Utils.getApp().getString(R.string.contract_book))
        titles.add(Utils.getApp().getString(R.string.contract_group))
        titles
    }

    private var mTabLayoutMediator by autoCleared<TabLayoutMediator>()



    override fun getLayoutId(): Int {
        return R.layout.fragment_create_meeting
    }


    override fun initView() {
        super.initView()
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@CreateMeetingFragment) {
                override fun getItemCount(): Int {
                    return mFragments.size
                }

                override fun createFragment(position: Int): Fragment {
                    return mFragments[position]
                }
            }
            offscreenPageLimit = 3
        }

        mTabLayoutMediator =
            TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
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
        mBinding.btnCall.setOnClickListener {

        }

        mBinding.ivAddGroup.setOnClickListener {

        }

        mBinding.ivBack.setOnClickListener {

        }

        mBinding.rvSelectedUser.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }
}