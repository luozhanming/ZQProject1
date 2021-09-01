package cn.com.ava.zqproject.ui.videoResource.adapter

import android.widget.BaseAdapter
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemTransmissionListBinding
import cn.com.ava.zqproject.vo.VideoResource
import com.blankj.utilcode.util.Utils

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
        fun onDelete(data:RecordFilesInfo.RecordFile?)
    }

    class VideoTransmissionListViewHolder(
        binding: ViewDataBinding,
        val callback: VideoTransmissionCallback? = null
    ) : BaseViewHolder<RecordFilesInfo.RecordFile, ItemTransmissionListBinding>(binding as ItemTransmissionListBinding) {
        override fun setDataToBinding(binding: ItemTransmissionListBinding, data: RecordFilesInfo.RecordFile) {
            binding.video = data

            binding.tvProgress.setText("${data.downloadProgress}%")
            if (data.downloadProgress == 100) {
                binding.tvState.setText("下载完成")
                binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_318EF8))
            } else if (data.downloadProgress == -1) {
                binding.tvState.setText("下载失败")
                binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_FF4646))
            } else {
                binding.tvState.setText("下载中")
                binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_666666))
            }
        }

        override fun setListenerToBinding(binding: ItemTransmissionListBinding) {
            binding.root.setOnClickListener {
                callback?.onDidClickedItem(mData)
            }

            binding.btnDelete.setOnClickListener {
                callback?.onDelete(mData)
            }
        }
    }
}