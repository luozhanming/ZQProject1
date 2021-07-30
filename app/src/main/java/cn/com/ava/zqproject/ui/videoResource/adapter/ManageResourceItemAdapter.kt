package cn.com.ava.zqproject.ui.videoResource.adapter

import android.widget.BaseAdapter
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemManageResourceListBinding
import cn.com.ava.zqproject.databinding.ItemResourceListBinding
import cn.com.ava.zqproject.vo.VideoResource

class ManageResourceItemAdapter(private val mCallback: ManageResourceCallback? = null) : cn.com.ava.base.recyclerview.BaseAdapter<VideoResource>() {

    interface ManageResourceCallback {
        fun onDidClickedItem(data:VideoResource?)
    }

    class ManageResourceViewHolder(binding: ViewDataBinding, val callback: ManageResourceCallback? = null) :
            BaseViewHolder<VideoResource, ItemManageResourceListBinding>(binding as ItemManageResourceListBinding) {
        override fun setDataToBinding(binding: ItemManageResourceListBinding, data: VideoResource) {
            binding.video = data
        }

        override fun setListenerToBinding(binding: ItemManageResourceListBinding) {
            binding.root.setOnClickListener {
                callback?.onDidClickedItem(mData)
            }
        }
    }

    override fun createDiffCallback(): AdapterDiffCallback<VideoResource> {
        return object : AdapterDiffCallback<VideoResource>() {
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
    ): BaseViewHolder<VideoResource, ViewDataBinding> {
        return ManageResourceViewHolder(binding, mCallback) as BaseViewHolder<VideoResource, ViewDataBinding>
    }
}