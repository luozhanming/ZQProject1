package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemAutoPatrolMemberBinding
import cn.com.ava.zqproject.vo.StatefulView


/**
 * 轮播成员选择列表的Adapter
 * */
class AutoPatrolMemberAdapter : BaseAdapter<StatefulView<LinkedUser>>() {

    private val mSelectedUsers by lazy {
        mutableListOf<LinkedUser>()
    }


    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<LinkedUser>> {
        return object : AdapterDiffCallback<StatefulView<LinkedUser>>() {
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

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_auto_patrol_member
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<LinkedUser>, ViewDataBinding> {
        return AutoPatrolMemeberViewHolder(binding){stateful,pos->
            if(!stateful.isSelected&&mSelectedUsers.size<9){   //添加
                mSelectedUsers.add(stateful.obj)
                stateful.isSelected = !stateful.isSelected
                notifyItemChanged(pos)
            }else if(stateful.isSelected){
                mSelectedUsers.remove(stateful.obj)
                stateful.isSelected = !stateful.isSelected
                notifyItemChanged(pos)
            }

        } as BaseViewHolder<StatefulView<LinkedUser>, ViewDataBinding>
    }

    fun getSelectedUser():List<LinkedUser>{
        return mSelectedUsers
    }


    class AutoPatrolMemeberViewHolder(binding: ViewDataBinding,val callback:((StatefulView<LinkedUser>,Int)->Unit)?=null) :
        BaseViewHolder<StatefulView<LinkedUser>,
                ItemAutoPatrolMemberBinding>(binding as ItemAutoPatrolMemberBinding) {
        override fun setDataToBinding(
            binding: ItemAutoPatrolMemberBinding,
            data: StatefulView<LinkedUser>
        ) {
            binding.user = data
        }


        override fun setListenerToBinding(binding: ItemAutoPatrolMemberBinding) {
            binding.root.setOnClickListener {
                callback?.invoke(mData!!,adapterPosition)
            }
        }
    }
}