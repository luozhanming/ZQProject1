package cn.com.ava.zqproject.ui.record.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemSubPresetBinding
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoPresetButton

class PresetSubAdapter(val callback: WindowPresetAdapter.WindowPresetCallback?=null) : BaseAdapter<StatefulView<VideoPresetButton>>() {
    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<VideoPresetButton>> {
        return object : AdapterDiffCallback<StatefulView<VideoPresetButton>>() {
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
        return R.layout.item_sub_preset
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<VideoPresetButton>, ViewDataBinding> {
        return PresetSubViewHolder(binding,callback) as BaseViewHolder<StatefulView<VideoPresetButton>, ViewDataBinding>
    }


    class PresetSubViewHolder(binding: ViewDataBinding,val callback: WindowPresetAdapter.WindowPresetCallback?=null) :
        BaseViewHolder<StatefulView<VideoPresetButton>, ItemSubPresetBinding>(binding as ItemSubPresetBinding) {
        override fun setDataToBinding(
            binding: ItemSubPresetBinding,
            data: StatefulView<VideoPresetButton>
        ) {
            binding.button = data
        }

        override fun setListenerToBinding(binding: ItemSubPresetBinding) {
            binding.root.setOnClickListener {
                callback?.onPresetClicked(mData!!)
            }
        }

    }
}