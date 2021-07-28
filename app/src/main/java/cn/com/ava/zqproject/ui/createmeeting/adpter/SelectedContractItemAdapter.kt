package cn.com.ava.zqproject.ui.createmeeting.adpter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemSelectedContractBinding
import cn.com.ava.zqproject.vo.ContractUser

class SelectedContractItemAdapter(private var mCallback: SelectedContractCallback? = null) :
    BaseAdapter<ContractUser>() {


    override fun createDiffCallback(): AdapterDiffCallback<ContractUser> {
        return object : AdapterDiffCallback<ContractUser>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_selected_contract
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<ContractUser, ViewDataBinding> {
        return SelectedContractViewHolder(
            binding,
            mCallback
        ) as BaseViewHolder<ContractUser, ViewDataBinding>
    }


    /**
     * 定义一个操作回调
     */
    interface SelectedContractCallback {
        fun onCancel(data: ContractUser?)
    }


    class SelectedContractViewHolder(
        binding: ViewDataBinding,
        val callback: SelectedContractCallback? = null
    ) :
        BaseViewHolder<ContractUser, ItemSelectedContractBinding>(binding as ItemSelectedContractBinding) {


        override fun setDataToBinding(binding: ItemSelectedContractBinding, data: ContractUser) {
            binding.user = data
        }

        override fun setListenerToBinding(binding: ItemSelectedContractBinding) {
            binding.btnCancel.setOnClickListener {
                callback?.onCancel(mData)
            }
        }

    }
}