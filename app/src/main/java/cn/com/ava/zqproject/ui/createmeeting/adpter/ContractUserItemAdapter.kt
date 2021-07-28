package cn.com.ava.zqproject.ui.createmeeting.adpter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemContractBinding
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView

class ContractUserItemAdapter(val mContractCallback: ContractUserCallback? = null) :
    BaseAdapter<StatefulView<ContractUser>>() {


    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<ContractUser>> {
        return object : AdapterDiffCallback<StatefulView<ContractUser>>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].obj == newList[newItemPosition].obj
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.obj == new.obj && old.isSelected == new.isSelected
            }
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_contract
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<ContractUser>, ViewDataBinding> {
        return ContractUserViewHolder(
            binding,
            mContractCallback
        ) as BaseViewHolder<StatefulView<ContractUser>, ViewDataBinding>
    }





    interface ContractUserCallback {
        fun onSelectedUser(user: StatefulView<ContractUser>, isSelected: Boolean)
    }


    class ContractUserViewHolder(
        binding: ViewDataBinding,
        val callback: ContractUserCallback? = null
    ) : BaseViewHolder<StatefulView<ContractUser>, ItemContractBinding>(binding as ItemContractBinding) {
        override fun setDataToBinding(
            binding: ItemContractBinding,
            data: StatefulView<ContractUser>
        ) {
            binding.user = data
        }

        override fun setListenerToBinding(binding: ItemContractBinding) {
            binding.root.setOnClickListener {
                callback?.onSelectedUser(mData!!, !mData!!.isSelected)
            }
        }
    }

}