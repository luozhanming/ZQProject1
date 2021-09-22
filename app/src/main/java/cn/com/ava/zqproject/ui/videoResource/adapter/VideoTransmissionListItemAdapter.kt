package cn.com.ava.zqproject.ui.videoResource.adapter

import android.text.TextUtils
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

            if (data.transmissionType == 1) { // 下载
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
            } else if (data.transmissionType == 2) { // 上传
                if (TextUtils.isEmpty(data.uploadProgress) || data.uploadProgress == "0") {
                    binding.tvProgress.setText("0%")
                } else {
                    binding.tvProgress.setText("${data.uploadProgress}")
                }
                if (data.uploadState == 1) {
                    binding.tvState.setText("上传完成")
                    binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_318EF8))
                } else if (data.uploadState == 0) {
                    binding.tvState.setText("等待上传")
                    binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_666666))
                } else if (data.uploadState == 3) {
                    binding.tvState.setText("上传失败")
                    binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_FF4646))
                } else {
                    binding.tvState.setText("上传中")
                    binding.tvState.setTextColor(Utils.getApp().resources.getColor(R.color.color_666666))
                }
            } else {
                binding.tvProgress.setText("")
                binding.tvState.setText("")
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