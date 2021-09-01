package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemSignalSelectBinding

class SignalSelectAdapter(val callback: ((LinkedUser) -> Unit)? = null) : BaseAdapter<LinkedUser>() {
    override fun createDiffCallback(): AdapterDiffCallback<LinkedUser> {
        return object : AdapterDiffCallback<LinkedUser>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old == new
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old == new
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_signal_select
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<LinkedUser, ViewDataBinding> {
        return SignalSelectViewHolder(binding,callback) as BaseViewHolder<LinkedUser, ViewDataBinding>
    }


    class SignalSelectViewHolder(binding: ViewDataBinding,val callback: ((LinkedUser) -> Unit)? = null) :
        BaseViewHolder<LinkedUser, ItemSignalSelectBinding>(binding as ItemSignalSelectBinding) {
        override fun setDataToBinding(binding: ItemSignalSelectBinding, data: LinkedUser) {
            binding.user = data
        }


        override fun setListenerToBinding(binding: ItemSignalSelectBinding) {
            binding.root.setOnClickListener {
                callback?.invoke(mData!!)
            }
        }

    }
}