package cn.com.ava.zqproject.ui.meeting.adapter

import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.common.extension.avoidDropdownFocus
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemSelectLayoutSignalBinding
import cn.com.ava.zqproject.vo.LayoutSignalSelect

class SelectLayoutSignalAdapter(val onSignalSelect: ((Int, View) -> Unit)? = null) :
    BaseAdapter<LayoutSignalSelect>() {
    override fun createDiffCallback(): AdapterDiffCallback<LayoutSignalSelect> {
        return object : AdapterDiffCallback<LayoutSignalSelect>() {
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
        return R.layout.item_select_layout_signal
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<LayoutSignalSelect, ViewDataBinding> {
        return SelectLayoutSignalViewHolder(
            binding,
            onSignalSelect
        ) as BaseViewHolder<LayoutSignalSelect, ViewDataBinding>
    }


    class SelectLayoutSignalViewHolder(
        binding: ViewDataBinding,
        val onSignalSelect: ((Int, View) -> Unit)? = null
    ) :
        BaseViewHolder<LayoutSignalSelect, ItemSelectLayoutSignalBinding>(binding as ItemSelectLayoutSignalBinding) {


        init {
            val islsBinding = binding as ItemSelectLayoutSignalBinding

        }

        override fun setListenerToBinding(binding: ItemSelectLayoutSignalBinding) {
            binding.flSpinner.setOnClickListener {
                //信号索引和触发的View
                onSignalSelect?.invoke(mData!!.signalIndex,it)
            }
        }


        override fun setDataToBinding(
            binding: ItemSelectLayoutSignalBinding,
            data: LayoutSignalSelect
        ) {
            binding.layoutsignal = data
//            val data = arrayListOf<Map<String, String>>()
//            val map = hashMapOf<String, String>().apply {
//                this["name"] = "无信号"
//            }
//            data.add(map)
//            mData!!.unselectedMember.forEach {
//                val map = hashMapOf<String, String>().apply {
//                    this["name"] = it.nickname
//                }
//                data.add(map)
//            }
//            mSpinnerAdapter = SimpleAdapter(
//                binding.root.context, data, R.layout.item_spinner_text,
//                arrayOf("name"), intArrayOf(R.id.text)
//            )
//            binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
//                override fun onItemSelected(
//                    parent: AdapterView<*>,
//                    view: View,
//                    position: Int,
//                    id: Long
//                ) {
//                    binding.spinner.onItemSelectedListener = null
//                    //第一个是无信号，不属于unselectedMember
//                    if(position==0){
//                        onSignalSelect?.invoke(mData!!.signalIndex,null)
//                    }else{
//                        val mSelected = mData!!.unselectedMember[position]
//                        onSignalSelect?.invoke(mData!!.signalIndex,mSelected)
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    binding.spinner.onItemSelectedListener = null
//                    onSignalSelect?.invoke(mData!!.signalIndex,null)
//                }
//            }

        //    binding.spinner.adapter = mSpinnerAdapter
        }
    }
}