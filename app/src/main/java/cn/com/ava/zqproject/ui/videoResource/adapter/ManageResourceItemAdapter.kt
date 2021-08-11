package cn.com.ava.zqproject.ui.videoResource.adapter

import android.widget.BaseAdapter
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemManageResourceListBinding
import cn.com.ava.zqproject.databinding.ItemResourceListBinding
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoResource

class ManageResourceItemAdapter(private val mCallback: ManageResourceCallback? = null) :
    cn.com.ava.base.recyclerview.BaseAdapter<StatefulView<RecordFilesInfo.RecordFile>>() {

    interface ManageResourceCallback {
        fun onSelectVideo(data: StatefulView<RecordFilesInfo.RecordFile>, isSelected: Boolean)
    }

    class ManageResourceViewHolder(binding: ViewDataBinding, val callback: ManageResourceCallback? = null) :
            BaseViewHolder<StatefulView<RecordFilesInfo.RecordFile>, ItemManageResourceListBinding>(binding as ItemManageResourceListBinding) {
        override fun setDataToBinding(binding: ItemManageResourceListBinding, data: StatefulView<RecordFilesInfo.RecordFile>) {
            binding.video = data
        }

        override fun setListenerToBinding(binding: ItemManageResourceListBinding) {
            binding.btnCheckBox.setOnClickListener {
                callback?.onSelectVideo(mData!!, !mData!!.isSelected)
            }
        }
    }

    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<RecordFilesInfo.RecordFile>> {
        return object : AdapterDiffCallback<StatefulView<RecordFilesInfo.RecordFile>>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_manage_resource_list
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<RecordFilesInfo.RecordFile>, ViewDataBinding> {
        return ManageResourceViewHolder(binding, mCallback) as BaseViewHolder<StatefulView<RecordFilesInfo.RecordFile>, ViewDataBinding>
    }
}