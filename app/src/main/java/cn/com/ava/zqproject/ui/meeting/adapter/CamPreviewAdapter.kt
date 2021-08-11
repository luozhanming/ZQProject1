package cn.com.ava.zqproject.ui.meeting.adapter

import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemCamPreviewBinding
import cn.com.ava.zqproject.vo.StatefulView

class CamPreviewAdapter(val callback: ((PreviewVideoWindow) -> Unit)? = null) :
    BaseAdapter<StatefulView<PreviewVideoWindow>>() {

    override fun createDiffCallback(): AdapterDiffCallback<StatefulView<PreviewVideoWindow>> {
        return object : AdapterDiffCallback<StatefulView<PreviewVideoWindow>>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.obj.windowName == old.obj.windowName
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.obj.windowName == old.obj.windowName && old.isSelected == new.isSelected
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_cam_preview
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<PreviewVideoWindow>, ViewDataBinding> {
        return CamPreviewViewHolder(binding,callback) as BaseViewHolder<StatefulView<PreviewVideoWindow>, ViewDataBinding>
    }


    class CamPreviewViewHolder(binding: ViewDataBinding,val callback: ((PreviewVideoWindow) -> Unit)? = null) :
        BaseViewHolder<StatefulView<PreviewVideoWindow>, ItemCamPreviewBinding>(binding as ItemCamPreviewBinding) {
        override fun setDataToBinding(
            binding: ItemCamPreviewBinding,
            data: StatefulView<PreviewVideoWindow>
        ) {
            binding.window = data
        }

        override fun setListenerToBinding(binding: ItemCamPreviewBinding) {
            binding.ivSelected.setOnClickListener {
                callback?.invoke(mData!!.obj)
            }
        }

    }
}