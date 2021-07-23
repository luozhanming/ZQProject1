package cn.com.ava.zqproject.ui.createmeeting.adpter

import android.view.View
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemSelectedContractBinding

class SelectedContractItemAdapter:BaseAdapter<String>() {
    override fun createDiffCallback(): AdapterDiffCallback<String> {
        return object :AdapterDiffCallback<String>(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return false
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_selected_contract
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<String, ViewDataBinding> {
        return SelectedContractViewHolder(binding) as BaseViewHolder<String, ViewDataBinding>
    }




    class SelectedContractViewHolder(binding:ViewDataBinding):BaseViewHolder<String,ItemSelectedContractBinding>(binding as ItemSelectedContractBinding){
        override fun setDataToBinding(binding: ItemSelectedContractBinding, data: String) {

        }

        override fun setListenerToBinding(binding: ItemSelectedContractBinding) {
            binding.btnCancel.setOnClickListener {

            }
        }

    }
}