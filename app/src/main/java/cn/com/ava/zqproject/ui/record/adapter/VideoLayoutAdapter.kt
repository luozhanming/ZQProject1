package cn.com.ava.zqproject.ui.record.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemWindowLayoutBinding
import cn.com.ava.zqproject.vo.LayoutButton
import cn.com.ava.zqproject.vo.StatefulView

class VideoLayoutAdapter(val callback:VideoLayoutCallback?=null) : BaseAdapter<StatefulView<LayoutButton>>() {
    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<LayoutButton>> {
        return object : AdapterDiffCallback<StatefulView<LayoutButton>>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.obj == new.obj
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.obj == new.obj && old.isSelected == new.isSelected
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_window_layout
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<LayoutButton>, ViewDataBinding> {
        return VideoLayoutViewHolder(binding,callback) as BaseViewHolder<StatefulView<LayoutButton>, ViewDataBinding>
    }

    interface VideoLayoutCallback{
        fun onVideoLayoutSelected(button:StatefulView<LayoutButton>)
    }


    class VideoLayoutViewHolder(binding: ViewDataBinding,val callback:VideoLayoutCallback?=null):BaseViewHolder<StatefulView<LayoutButton>,ItemWindowLayoutBinding>(binding as ItemWindowLayoutBinding){
        override fun setDataToBinding(
            binding: ItemWindowLayoutBinding,
            data: StatefulView<LayoutButton>
        ) {
            binding.button = data
        }

        override fun setListenerToBinding(binding: ItemWindowLayoutBinding) {
            binding.root.setOnClickListener {
                callback?.onVideoLayoutSelected(mData!!)
            }
        }

    }
}