package cn.com.ava.zqproject.ui.record.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.Cache
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemVideoWindowBinding
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoWindowButton

class VideoWindowAdapter(val callback: OnVideoWindowCallback? = null) : BaseAdapter<StatefulView<VideoWindowButton>>() {
    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<VideoWindowButton>> {
        return object : AdapterDiffCallback<StatefulView<VideoWindowButton>>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].obj.windowIndex == newList[newItemPosition].obj.windowIndex
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].obj.windowIndex == newList[newItemPosition].obj.windowIndex && oldList[oldItemPosition].isSelected == newList[newItemPosition].isSelected

            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_video_window
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<VideoWindowButton>, ViewDataBinding> {
        return VideoWindowViewHolder(binding,callback) as BaseViewHolder<StatefulView<VideoWindowButton>, ViewDataBinding>
    }


    interface OnVideoWindowCallback {
        fun onClicked(view: StatefulView<VideoWindowButton>)
    }


    class VideoWindowViewHolder(binding: ViewDataBinding, val callback: OnVideoWindowCallback? = null) :
        BaseViewHolder<StatefulView<VideoWindowButton>, ItemVideoWindowBinding>(binding as ItemVideoWindowBinding) {

        override fun setDataToBinding(
            binding: ItemVideoWindowBinding,
            data: StatefulView<VideoWindowButton>
        ) {
            binding.button = data
        }

        override fun setListenerToBinding(binding: ItemVideoWindowBinding) {
            binding.root.setOnClickListener {
                callback?.onClicked(mData!!)
            }
        }

    }
}