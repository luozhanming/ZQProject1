package cn.com.ava.zqproject.ui.record.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemPresetBinding
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoPresetButton

class WindowPresetAdapter(val callback: WindowPresetCallback? = null) :
    BaseAdapter<PreviewVideoWindow>() {
    override fun createDiffCallback(): AdapterDiffCallback<PreviewVideoWindow> {
        return object : AdapterDiffCallback<PreviewVideoWindow>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.windowName == new.windowName
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.windowName == new.windowName
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_preset
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<PreviewVideoWindow, ViewDataBinding> {
        return WindowPresetViewHolder(
            binding,
            callback
        ) as BaseViewHolder<PreviewVideoWindow, ViewDataBinding>
    }


    interface WindowPresetCallback {
        fun onPresetClicked(button: StatefulView<VideoPresetButton>)
    }


    class WindowPresetViewHolder(
        binding: ViewDataBinding,
        val callback: WindowPresetCallback? = null
    ) :
        BaseViewHolder<PreviewVideoWindow, ItemPresetBinding>(binding as ItemPresetBinding) {


        private var mPresetSubAdapter: PresetSubAdapter? = null

        init {
            mPresetSubAdapter = PresetSubAdapter(callback)
            if (binding is ItemPresetBinding) {
                binding.rvPreset.layoutManager =
                    GridLayoutManager(binding.root.context, 6, GridLayoutManager.VERTICAL, false)
                binding.rvPreset.adapter = mPresetSubAdapter
            }

        }


        override fun setDataToBinding(binding: ItemPresetBinding, data: PreviewVideoWindow) {
            binding.window = data
            val presetButtons = arrayListOf<StatefulView<VideoPresetButton>>()
            if (mPresetSubAdapter?.getDatas()?.isNotEmpty() == true) {
                mPresetSubAdapter?.notifyDataSetChanged()
            } else {
                for (i in 1..8) {
                    val button = VideoPresetButton(mData!!.ptzIdx, i)
                    presetButtons.add(StatefulView(button))
                }
                mPresetSubAdapter?.setDatas(presetButtons)
            }
        }

    }
}