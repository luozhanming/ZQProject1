package cn.com.ava.zqproject.ui.videoResource.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemResourceListBinding
import cn.com.ava.zqproject.vo.VideoResource

class VideoResourceListItemAdapter(private val mCallback: VideoResourceListCallback? = null) : BaseAdapter<RecordFilesInfo.RecordFile>() {

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

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_resource_list
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<RecordFilesInfo.RecordFile, ViewDataBinding> {
        return VideoResourceListViewHolder(binding, mCallback) as BaseViewHolder<RecordFilesInfo.RecordFile, ViewDataBinding>
    }

    /**
     * 定义一个操作回调
     * */
    interface VideoResourceListCallback {
        fun onDidClickedItem(data:RecordFilesInfo.RecordFile?)
        fun onDownload(data:RecordFilesInfo.RecordFile?)
        fun onUpload(data:RecordFilesInfo.RecordFile?)
        fun onDelete(data:RecordFilesInfo.RecordFile?)
    }

    class VideoResourceListViewHolder(binding: ViewDataBinding, val callback: VideoResourceListCallback? = null) :
        BaseViewHolder<RecordFilesInfo.RecordFile, ItemResourceListBinding>(binding as ItemResourceListBinding) {

        override fun setDataToBinding(binding: ItemResourceListBinding, data: RecordFilesInfo.RecordFile) {
            binding.video = data
        }

        override fun setListenerToBinding(binding: ItemResourceListBinding) {
            binding.root.setOnClickListener {
                callback?.onDidClickedItem(mData)
            }
            binding.ivDownload.setOnClickListener {
                callback?.onDownload(mData)
            }
            binding.ivUpload.setOnClickListener {
                callback?.onUpload(mData)
            }
            binding.btnDelete.setOnClickListener {
                callback?.onDelete(mData)
            }
        }
    }

}