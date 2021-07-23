package cn.com.ava.base.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<DATA, DB : ViewDataBinding>(val binding: DB) :
    RecyclerView.ViewHolder(binding.root) {

    private var mData: DATA? = null

    fun bind(data: DATA) {
        setDataToBinding(binding,data)
        binding.executePendingBindings()
    }

    /**
     * 将实体类传递到binding
     */
    protected abstract fun setDataToBinding(binding: DB, data: DATA)

    protected open fun setListenerToBinding(binding: DB){

    }
}