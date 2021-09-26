package cn.com.ava.zqproject.ui.meeting

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogMemeberManagerBinding
import cn.com.ava.zqproject.ui.meeting.adapter.OnMeetingMemberAdapter
import cn.com.ava.zqproject.ui.meeting.adapter.WaitingRoomMemberAdapter
import cn.com.ava.zqproject.vo.MeetingMember
import com.blankj.utilcode.util.Utils
import com.google.android.material.tabs.TabLayout

class MemberManagerDialog : BaseDialogV2<DialogMemeberManagerBinding>(),
    TabLayout.OnTabSelectedListener {

    private val mMemberManagerViewModel by viewModels<MemeberManagerViewModel>({ requireParentFragment() })

    private val mMasterViewModel by viewModels<MasterViewModel>({requireParentFragment()})

    private var mOnMeetingMemberAdapter by autoCleared<OnMeetingMemberAdapter>()

    private var mWaitingRoomMemberAdapter by autoCleared<WaitingRoomMemberAdapter>()


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(1140), SizeUtils.dp2px(690), Gravity.CENTER)
    }

    override fun initView(root: View) {
        mBinding.tabLayout.addOnTabSelectedListener(this)
        val tabNames =
            arrayOf(
                getString(R.string.has_enter),
                getString(R.string.waiting_room)
            )
        tabNames.forEach {
            val tab = mBinding.tabLayout.newTab()
            val textView = TextView(context)

            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(
                Utils.getApp().resources.getColor(R.color.color_318EF8),
                Utils.getApp().resources.getColor(R.color.color_222222)
            )
            val colorStateList = ColorStateList(states, colors)
            textView.text = it
            textView.textSize = 21f
            textView.gravity = Gravity.CENTER

            textView.setTextColor(colorStateList)
            textView.typeface = Typeface.DEFAULT_BOLD
            tab.customView = textView
            mBinding.tabLayout.addTab(tab)
        }
        mBinding.rvHasEnter.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        mOnMeetingMemberAdapter = OnMeetingMemberAdapter(object :OnMeetingMemberAdapter.ItemCallback{
            override fun onAudioButtonClicked(member: MeetingMember, audioEnable: Boolean) {
                mMemberManagerViewModel.setMemberMicEnable(member.user.number,audioEnable)
            }

            override fun onVideoButtonClicked(member: MeetingMember, videoEnable: Boolean) {
                mMemberManagerViewModel.setMemberCamEnable(member.user.number,videoEnable)
            }

            override fun onRemoveButtonClicked(member: MeetingMember) {
                mMemberManagerViewModel.removeMemberToWaiting(member.user.number)
            }
        })

        mBinding.rvHasEnter.adapter = mOnMeetingMemberAdapter

        mBinding.rvWaiting.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        mWaitingRoomMemberAdapter = WaitingRoomMemberAdapter(object :WaitingRoomMemberAdapter.ItemCallback{
            override fun onAcceptButtonClicked(member: LinkedUser) {
                mMemberManagerViewModel.acceptMemberToMeeting(member.number)
            }
        })
        mBinding.rvWaiting.adapter = mWaitingRoomMemberAdapter


        mBinding.btnLockMeeting.setOnClickListener {
            //TODO 这里需要改，不应该用MasterViewModel
            mMasterViewModel.toggleLockMeeting()
        }
        mBinding.btnAllAccept.setOnClickListener {
            mMemberManagerViewModel.acceptAllMemberToMeeting()
        }
        mBinding.btnAllRemove.setOnClickListener {
            mMemberManagerViewModel.removeAllMemberToWaiting()
        }
        mBinding.btnAllSilent.setOnClickListener {
            mMemberManagerViewModel.silentAllMembers()
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_memeber_manager
    }

    override fun onBindViewModel2Layout(binding: DialogMemeberManagerBinding) {
        binding.masterViewModel = mMasterViewModel
    }

    override fun onDestroyView() {
        mBinding.tabLayout.removeOnTabSelectedListener(this)
        super.onDestroyView()
    }

    override fun observeVM() {
        mMemberManagerViewModel.onMeetingMembers.observe(this) {
            mOnMeetingMemberAdapter?.setDatasWithDiff(it)
        }
        mMemberManagerViewModel.onWaitingMembers.observe(this){
            mWaitingRoomMemberAdapter?.setDatasWithDiff(it)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab?.position == 0) {
            mBinding.group1.visibility = View.VISIBLE
            mBinding.group2.visibility = View.GONE
        } else if (tab?.position == 1) {
            mBinding.group1.visibility = View.GONE
            mBinding.group2.visibility = View.VISIBLE
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}