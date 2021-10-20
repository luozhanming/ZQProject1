package cn.com.ava.zqproject.ui.meeting.adapter

import android.widget.SeekBar
import androidx.databinding.ViewDataBinding
import cn.com.ava.base.recyclerview.BaseAdapter
import cn.com.ava.base.recyclerview.BaseViewHolder
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.ItemVolumechannelBinding

class VolumeChannelAdapter(val callback:Callback?=null) : BaseAdapter<VolumeChannel>() {
    override fun createDiffCallback(): AdapterDiffCallback<VolumeChannel>? {
        return object : AdapterDiffCallback<VolumeChannel>() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.channelName == new.channelName
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldList[oldItemPosition]
                val new = newList[newItemPosition]
                return old == new
            }

        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_volumechannel
    }


    override fun getViewHolder(
        viewType: Int,
        binding: ViewDataBinding
    ): BaseViewHolder<VolumeChannel, ViewDataBinding> {
        return VolumeChannelViewHolder(binding,callback) as BaseViewHolder<VolumeChannel, ViewDataBinding>
    }


    interface Callback {
        fun onClickedSilentButton(channel: VolumeChannel, isSilent: Boolean)

        fun onValueChanged(channel: VolumeChannel, value: Int)
    }


    class VolumeChannelViewHolder(binding: ViewDataBinding,val callback:Callback?=null) :
        BaseViewHolder<VolumeChannel, ItemVolumechannelBinding>(binding as ItemVolumechannelBinding) {
        override fun setDataToBinding(binding: ItemVolumechannelBinding, data: VolumeChannel) {
            binding.volumechannel = data
        }

        override fun setListenerToBinding(binding: ItemVolumechannelBinding) {
            binding.cbSilent2.setOnClickListener {
                callback?.onClickedSilentButton(mData!!,mData!!.isSilent)

            }
            binding.sbAudio2.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    callback?.onValueChanged(mData!!,seekBar?.progress?:0)
                }

            })
        }
    }
}