package cn.com.ava.zqproject.ui.createmeeting.adpter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemGroupSubUserBinding
import cn.com.ava.zqproject.vo.ContractUser

class GroupSubUserItemAdapter : BaseAdapter<ContractUser>() {
    override fun createDiffCallback(): AdapterDiffCallback<ContractUser> {
        return object : AdapterDiffCallback<ContractUser>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old == new
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old == new
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_group_sub_user
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<ContractUser, ViewDataBinding> {
        return GroupSubUserViewHolder(binding) as BaseViewHolder<ContractUser, ViewDataBinding>

    }


    class GroupSubUserViewHolder(binding: ViewDataBinding) :
        BaseViewHolder<ContractUser, ItemGroupSubUserBinding>(binding as ItemGroupSubUserBinding) {
        override fun setDataToBinding(binding: ItemGroupSubUserBinding, data: ContractUser) {
            binding.user = data
        }

    }
}