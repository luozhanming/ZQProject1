package cn.com.ava.base.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<DATA, DB : ViewDataBinding>(val binding: DB) :
    RecyclerView.ViewHolder(binding.root) {

    protected var mData: DATA? = null

    var isFirst:Boolean = false

    var isLast:Boolean = false

    var itemCount:Int = 0

    fun bind(data: DATA) {
        mData = data
        setDataToBinding(binding,data)
        setListenerToBinding(binding)
        binding.executePendingBindings()
    }

    /**
     * 将实体类传递到binding
     */
    protected abstract fun setDataToBinding(binding: DB, data: DATA)

    /**
     * 给相关控件设置监听器
     */
    protected open fun setListenerToBinding(binding: DB){

    }
}