package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.zq.ApplySpeakUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemApplySpeakUserBinding

/**
 * 申请发言列表Adapter
 * */
class ApplySpeakUserAdapter(val callback: Callback? = null) : BaseAdapter<ApplySpeakUser>() {

    companion object {
        var isExpand: Boolean = false
    }


    private var mCallback: Callback? = null


    init {
        mCallback = object : Callback {
            override fun onAgreeOrDisagree(user: ApplySpeakUser, agree: Boolean) {

            }

            override fun expandMore() {
                isExpand = true
                notifyDataSetChanged()
            }

            override fun shrinkMore() {
                isExpand = false
                notifyDataSetChanged()
            }

        }
    }

    override fun createDiffCallback(): AdapterDiffCallback<ApplySpeakUser> {
        return object : AdapterDiffCallback<ApplySpeakUser>() {
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
        return R.layout.item_apply_speak_user
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<ApplySpeakUser, ViewDataBinding> {
        return ApplySpeakUserViewHolder(
            binding,
            callback = mCallback
        ) as BaseViewHolder<ApplySpeakUser, ViewDataBinding>
    }

    interface Callback {
        fun onAgreeOrDisagree(user: ApplySpeakUser, agree: Boolean)

        fun expandMore()

        fun shrinkMore()
    }


    class ApplySpeakUserViewHolder(binding: ViewDataBinding, val callback: Callback? = null) :
        BaseViewHolder<ApplySpeakUser, ItemApplySpeakUserBinding>(binding as ItemApplySpeakUserBinding) {

        override fun setDataToBinding(binding: ItemApplySpeakUserBinding, data: ApplySpeakUser) {
            binding.user = data
            binding.isFirst = isFirst
            binding.isLast = isLast
            binding.isExpand = isExpand
        }

        override fun setListenerToBinding(binding: ItemApplySpeakUserBinding) {
            binding.btnAgree.setOnClickListener {
                callback?.onAgreeOrDisagree(mData!!, true)
            }
            binding.btnAgree.setOnClickListener {
                callback?.onAgreeOrDisagree(mData!!, false)
            }
            binding.ivMore.setOnClickListener {
                callback?.expandMore()
            }
            binding.ivShrink.setOnClickListener {
                callback?.shrinkMore()
            }
        }

    }
}