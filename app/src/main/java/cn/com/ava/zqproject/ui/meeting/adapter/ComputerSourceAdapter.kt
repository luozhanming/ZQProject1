package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemComputerSourceBinding
import cn.com.ava.zqproject.vo.InteractComputerSource

class ComputerSourceAdapter(val callback:((InteractComputerSource)->Unit)?=null):BaseAdapter<InteractComputerSource>() {


    override fun createDiffCallback(): AdapterDiffCallback<InteractComputerSource> {
        return object :AdapterDiffCallback<InteractComputerSource>(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_computer_source
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<InteractComputerSource, ViewDataBinding> {
        return ComputerSourceViewHolder(binding,callback) as BaseViewHolder<InteractComputerSource, ViewDataBinding>
    }





    class ComputerSourceViewHolder(bingding:ViewDataBinding,val callback:((InteractComputerSource)->Unit)?=null):BaseViewHolder<InteractComputerSource,ItemComputerSourceBinding>(bingding as ItemComputerSourceBinding){
        override fun setDataToBinding(binding: ItemComputerSourceBinding, data: InteractComputerSource) {
            binding.source = data
        }

        override fun setListenerToBinding(binding: ItemComputerSourceBinding) {
            binding.root.setOnClickListener {
                callback?.invoke(mData!!)
            }
        }

    }


}