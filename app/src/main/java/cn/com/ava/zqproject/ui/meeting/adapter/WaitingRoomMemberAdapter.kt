package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemWaitingRoomMemberBinding
import cn.com.ava.zqproject.vo.MeetingMember

class WaitingRoomMemberAdapter(val callback:ItemCallback?=null):BaseAdapter<MeetingMember>() {
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
        return R.layout.item_waiting_room_member
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<MeetingMember, ViewDataBinding> {
        return WaitingRoomMemberViewHolder(binding,callback) as BaseViewHolder<MeetingMember, ViewDataBinding>
    }


    interface ItemCallback{
        fun onAcceptButtonClicked(member: MeetingMember)
    }

    class WaitingRoomMemberViewHolder(binding: ViewDataBinding,val callback:ItemCallback?=null)
        : BaseViewHolder<MeetingMember,ItemWaitingRoomMemberBinding>(binding as ItemWaitingRoomMemberBinding){
        override fun setDataToBinding(binding: ItemWaitingRoomMemberBinding, data: MeetingMember) {
            binding.member = data
        }

        override fun setListenerToBinding(binding: ItemWaitingRoomMemberBinding) {
            binding.btnAccept.setOnClickListener {
                callback?.onAcceptButtonClicked(mData!!)
            }
        }
    }
}