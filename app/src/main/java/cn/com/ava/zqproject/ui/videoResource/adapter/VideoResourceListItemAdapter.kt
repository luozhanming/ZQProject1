package cn.com.ava.zqproject.ui.videoResource.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemResourceListBinding
import cn.com.ava.zqproject.vo.VideoResource

class VideoResourceListItemAdapter(private val mCallback: VideoResourceListCallback? = null) : BaseAdapter<VideoResource>() {

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
        return R.layout.item_resource_list
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<VideoResource, ViewDataBinding> {
        return VideoResourceListViewHolder(binding, mCallback) as BaseViewHolder<VideoResource, ViewDataBinding>
    }

    /**
     * 定义一个操作回调
     * */
    interface VideoResourceListCallback {
        fun onDidClickedItem(data:VideoResource?)
        fun onDownload(data:VideoResource?)
        fun onUpload(data:VideoResource?)
    }

    class VideoResourceListViewHolder(binding: ViewDataBinding, val callback: VideoResourceListCallback? = null) :
        BaseViewHolder<VideoResource, ItemResourceListBinding>(binding as ItemResourceListBinding) {

        private val TAG = "VideoResourceListItemAd"

        override fun setDataToBinding(binding: ItemResourceListBinding, data: VideoResource) {
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
        }
    }

}