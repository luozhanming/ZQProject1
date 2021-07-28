package cn.com.ava.zqproject.ui.createmeeting.adpter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemContractGroupBinding
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.StatefulView

class ContractGroupItemAdapter(var callback: ContractGroupCallback? = null) :
    BaseAdapter<StatefulView<ContractGroup>>() {


    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<ContractGroup>> {
        return object : AdapterDiffCallback<StatefulView<ContractGroup>>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.isSelected == new.isSelected && old.obj == new.obj
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.isSelected == new.isSelected && old.obj == new.obj
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_contract_group
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<ContractGroup>, ViewDataBinding> {
        return ContractGroupViewHolder(
            binding,
            callback
        ) as BaseViewHolder<StatefulView<ContractGroup>, ViewDataBinding>
    }


    interface ContractGroupCallback {
        fun onSelectedGroup(user: StatefulView<ContractGroup>, isSelected: Boolean)
    }

    class ContractGroupViewHolder(
        binding: ViewDataBinding,
        val callback: ContractGroupCallback? = null
    ) : BaseViewHolder<StatefulView<ContractGroup>, ItemContractGroupBinding>(binding as ItemContractGroupBinding) {

        private var mGroupSubUserAdapter: GroupSubUserItemAdapter? = null

        init {
            if (binding is ItemContractGroupBinding) {
                binding.rvSubUser.layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                mGroupSubUserAdapter = GroupSubUserItemAdapter()
                binding.rvSubUser.adapter = mGroupSubUserAdapter
                binding.rvSubUser.visibility =
                    if (binding.btnExpand.isSelected) View.VISIBLE else View.GONE
            }
        }

        override fun setListenerToBinding(binding: ItemContractGroupBinding) {
            //收缩按钮
            binding.btnExpand.setOnClickListener {
                binding.btnExpand.isSelected = !binding.btnExpand.isSelected
                binding.rvSubUser.visibility =
                    if (binding.btnExpand.isSelected) View.VISIBLE else View.GONE

            }
            binding.cbSelected.setOnClickListener {
                callback?.onSelectedGroup(mData!!,!mData!!.isSelected)
            }
        }


        override fun setDataToBinding(
            binding: ItemContractGroupBinding,
            data: StatefulView<ContractGroup>
        ) {
            binding.group = data
            mGroupSubUserAdapter?.setDatasWithDiff(data.obj.userIdList)
        }

    }
}