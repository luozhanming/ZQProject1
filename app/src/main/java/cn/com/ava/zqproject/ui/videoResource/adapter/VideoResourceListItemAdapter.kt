package cn.com.ava.zqproject.ui.videoResource.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemResourceListBinding
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoResource

class VideoResourceListItemAdapter(private val mCallback: VideoResourceListCallback? = null) : BaseAdapter<StatefulView<RecordFilesInfo.RecordFile>>() {

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
        return R.layout.item_resource_list
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<StatefulView<RecordFilesInfo.RecordFile>, ViewDataBinding> {
        return VideoResourceListViewHolder(binding, mCallback) as BaseViewHolder<StatefulView<RecordFilesInfo.RecordFile>, ViewDataBinding>
    }

    /**
     * 定义一个操作回调
     * */
    interface VideoResourceListCallback {
        fun onDidClickedItem(data:StatefulView<RecordFilesInfo.RecordFile>?)
        fun onDownload(data:StatefulView<RecordFilesInfo.RecordFile>?)
        fun onUpload(data:StatefulView<RecordFilesInfo.RecordFile>?)
        fun onDelete(data:StatefulView<RecordFilesInfo.RecordFile>?)
    }

    class VideoResourceListViewHolder(binding: ViewDataBinding, val callback: VideoResourceListCallback? = null) :
        BaseViewHolder<StatefulView<RecordFilesInfo.RecordFile>, ItemResourceListBinding>(binding as ItemResourceListBinding) {

        override fun setDataToBinding(binding: ItemResourceListBinding, data: StatefulView<RecordFilesInfo.RecordFile>) {
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