package cn.com.ava.zqproject.ui.videoResource.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemResourceListBinding

class VideoResourceListItemAdapter(private val titles : List<String>, private val mCallback: VideoResourceListCallback? = null) : BaseAdapter<String>() {

    private val TAG = "VideoResourceListItemAd"

    override fun createDiffCallback(): AdapterDiffCallback<String> {
        return object : AdapterDiffCallback<String>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return false
            }
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_resource_list
    }

    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<String, ViewDataBinding> {
        return VideoResourceListViewHolder(binding, mCallback) as BaseViewHolder<String, ViewDataBinding>
    }

    /**
     * 定义一个操作回调
     * */
    interface VideoResourceListCallback {
        fun onCancel(data:String?)
    }

    class VideoResourceListViewHolder(binding: ViewDataBinding, val callback: VideoResourceListCallback? = null) :
        BaseViewHolder<String, ItemResourceListBinding>(binding as ItemResourceListBinding) {

        private val TAG = "VideoResourceListItemAd"

        override fun setDataToBinding(binding: ItemResourceListBinding, data: String) {

        }

        override fun setListenerToBinding(binding: ItemResourceListBinding) {
            binding.root.setOnClickListener {
                callback?.onCancel(null)
            }
            binding.ivDownload.setOnClickListener {
                Log.d(TAG, "setListenerToBinding: download")
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String, ViewDataBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
    }

}