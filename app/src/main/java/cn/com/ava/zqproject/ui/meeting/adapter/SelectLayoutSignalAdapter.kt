package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemSelectLayoutSignalBinding
import cn.com.ava.zqproject.vo.LayoutSignalSelect

//class SelectLayoutSignalAdapter : BaseAdapter<LayoutSignalSelect>() {
//    override fun createDiffCallback(): AdapterDiffCallback<LayoutSignalSelect> {
//
//    }
//
//    override fun getLayoutId(viewType: Int): Int {
//        return R.layout.item_select_layout_signal
//    }
//
//    override fun getViewHolder(
//        viewType: Int,
//        binding: ViewDataBinding
//    ): BaseViewHolder<LayoutSignalSelect, ViewDataBinding> {
//
//    }
//
//
//    class SelectLayoutSignalViewHolder(binding: ViewDataBinding) :
//        BaseViewHolder<LayoutSignalSelect, ItemSelectLayoutSignalBinding>(binding as ItemSelectLayoutSignalBinding) {
//        override fun setDataToBinding(
//            binding: ItemSelectLayoutSignalBinding,
//            data: LayoutSignalSelect
//        ) {
//            binding.layoutsignal = data
//        }
//
//    }
//}