package cn.com.ava.zqproject.ui.record.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.Cache
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemPresetControlBinding
import cn.com.ava.zqproject.databinding.ItemVideoWindowBinding
import cn.com.ava.zqproject.databinding.ItemWindowLayoutBinding
import cn.com.ava.zqproject.vo.*

class CustomKeyAdapter(val callback: CustomKeyCallback?=null) : BaseAdapter<StatefulView<CommandButton>>() {
    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<CommandButton>> {
        return object : AdapterDiffCallback<StatefulView<CommandButton>>() {
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

    override fun getItemViewType(position: Int): Int {
        val button = mDatas[position]
        return button.obj.type
    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            TYPE_VIDEO -> R.layout.item_video_window
            TYPE_VIDEO_LAYOUT
            -> R.layout.item_window_layout
            TYPE_PRE_STAGE
            -> R.layout.item_preset_control
            else -> 0
        }
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<CommandButton>, ViewDataBinding> {
        return when (viewType) {
            TYPE_VIDEO -> VideoWindowViewHolder(binding,callback) as BaseViewHolder<StatefulView<CommandButton>, ViewDataBinding>
            TYPE_VIDEO_LAYOUT
            -> LayoutViewHolder(binding,callback) as BaseViewHolder<StatefulView<CommandButton>, ViewDataBinding>
            TYPE_PRE_STAGE
            -> PresetViewHolder(binding,callback) as BaseViewHolder<StatefulView<CommandButton>, ViewDataBinding>

            else -> PresetViewHolder(binding,callback) as BaseViewHolder<StatefulView<CommandButton>, ViewDataBinding>
        }
    }

    interface CustomKeyCallback{
        fun buttonClicked(button:StatefulView<CommandButton>)
    }


    class VideoWindowViewHolder(binding: ViewDataBinding,val callback: CustomKeyCallback?=null) :
        BaseViewHolder<StatefulView<VideoWindowButton>, ItemVideoWindowBinding>(binding as ItemVideoWindowBinding) {
        override fun setDataToBinding(
            binding: ItemVideoWindowBinding,
            data: StatefulView<VideoWindowButton>
        ) {
            data.obj.windowName = Cache.getCache().getWindowsByIndex(data.obj.windowIndex).windowName
            binding.button = data
        }

        override fun setListenerToBinding(binding: ItemVideoWindowBinding) {
            binding.root.setOnClickListener {
                callback?.buttonClicked(mData!! as StatefulView<CommandButton>)
            }
        }
    }

    class LayoutViewHolder(binding: ViewDataBinding,val callback: CustomKeyCallback?=null) :
        BaseViewHolder<StatefulView<LayoutButton>, ItemWindowLayoutBinding>(binding as ItemWindowLayoutBinding) {
        override fun setDataToBinding(
            binding: ItemWindowLayoutBinding,
            data: StatefulView<LayoutButton>
        ) {
            //TODO 有越界崩溃
            val layoutButtonInfo = Cache.getCache().layoutInfosCache[data.obj.layoutIndex]
            data.obj.layoutCmd = layoutButtonInfo.cmd
            data.obj.layoutDrawable = layoutButtonInfo.drawableId
            binding.button = data
        }

        override fun setListenerToBinding(binding: ItemWindowLayoutBinding) {
            binding.root.setOnClickListener {
                callback?.buttonClicked(mData!! as StatefulView<CommandButton>)
            }
        }
    }


    class PresetViewHolder(binding: ViewDataBinding,val callback: CustomKeyCallback?=null) :
        BaseViewHolder<StatefulView<VideoPresetButton>, ItemPresetControlBinding>(binding as ItemPresetControlBinding) {
        override fun setDataToBinding(
            binding: ItemPresetControlBinding,
            data: StatefulView<VideoPresetButton>
        ) {
            val window = Cache.getCache().getWindowsByIndex(data.obj.videoWindowIndex)
            binding.button = data
        }

        override fun setListenerToBinding(binding: ItemPresetControlBinding) {
            binding.root.setOnClickListener {
                callback?.buttonClicked(mData!! as StatefulView<CommandButton>)
            }
        }
    }
}
