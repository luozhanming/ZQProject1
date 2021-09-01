package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemOnMeetingMemberBinding
import cn.com.ava.zqproject.vo.MeetingMember
/**
 * 已入会成员Adapter
 * */
class OnMeetingMemberAdapter(val callback:ItemCallback?=null):BaseAdapter<MeetingMember>() {
    override fun createDiffCallback(): AdapterDiffCallback<MeetingMember> {
        return  object :AdapterDiffCallback<MeetingMember>(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition]==newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition]==newList[newItemPosition]
            }
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_on_meeting_member
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<MeetingMember, ViewDataBinding> {
        return MeetingMemberViewHolder(binding) as BaseViewHolder<MeetingMember, ViewDataBinding>
    }



    interface ItemCallback{
        fun onAudioButtonClicked(member:MeetingMember,audioEnable:Boolean)
        fun onVideoButtonClicked(member: MeetingMember,videoEnable:Boolean)
        fun onRemoveButtonClicked(member:MeetingMember)
    }




    class MeetingMemberViewHolder(binding: ViewDataBinding,val callback:ItemCallback?=null):BaseViewHolder<MeetingMember,ItemOnMeetingMemberBinding>(binding as ItemOnMeetingMemberBinding){
        override fun setDataToBinding(binding: ItemOnMeetingMemberBinding, data: MeetingMember) {
            binding.member = mData!!
        }

        override fun setListenerToBinding(binding: ItemOnMeetingMemberBinding) {
            binding.btnAudio.setOnClickListener {
                callback?.onAudioButtonClicked(mData!!,!mData!!.isAudioEnable)
            }

            binding.btnVideo.setOnClickListener {
                callback?.onVideoButtonClicked(mData!!,!mData!!.isVideoEnable)
            }

            binding.btnRubbish.setOnClickListener {
                callback?.onRemoveButtonClicked(mData!!)
            }
        }

    }
}