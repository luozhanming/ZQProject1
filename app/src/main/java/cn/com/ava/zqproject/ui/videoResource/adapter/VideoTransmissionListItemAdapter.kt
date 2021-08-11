package cn.com.ava.zqproject.ui.videoResource.adapter

import android.widget.BaseAdapter
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemTransmissionListBinding
import cn.com.ava.zqproject.vo.VideoResource

class VideoTransmissionListItemAdapter(private val mCallback: VideoTransmissionCallback? = null) :
    cn.com.ava.base.recyclerview.BaseAdapter<RecordFilesInfo.RecordFile>() {

    override fun createDiffCallback(): AdapterDiffCallback<RecordFilesInfo.RecordFile> {
        return object : AdapterDiffCallback<RecordFilesInfo.RecordFile>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<RecordFilesInfo.RecordFile, ViewDataBinding> {
        return VideoTransmissionListViewHolder(binding, mCallback) as BaseViewHolder<RecordFilesInfo.RecordFile, ViewDataBinding>
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_transmission_list
    }

    interface VideoTransmissionCallback {
        fun onDidClickedItem(data:RecordFilesInfo.RecordFile?)
    }

    class VideoTransmissionListViewHolder(
        binding: ViewDataBinding,
        val callback: VideoTransmissionCallback? = null
    ) : BaseViewHolder<RecordFilesInfo.RecordFile, ItemTransmissionListBinding>(binding as ItemTransmissionListBinding) {
        override fun setDataToBinding(binding: ItemTransmissionListBinding, data: RecordFilesInfo.RecordFile) {
            binding.video = data
        }

        override fun setListenerToBinding(binding: ItemTransmissionListBinding) {
            binding.root.setOnClickListener {
                callback?.onDidClickedItem(mData)
            }
        }
    }
}