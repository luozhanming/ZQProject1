package cn.com.ava.zqproject.ui.meeting

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseDialogV2
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.DialogVolumeSceneBinding
import cn.com.ava.zqproject.ui.meeting.adapter.VolumeChannelAdapter
import com.blankj.utilcode.util.Utils
import com.google.android.material.tabs.TabLayout

class VolumeSceneDialog : BaseDialogV2<DialogVolumeSceneBinding>(), TabLayout.OnTabSelectedListener,
    SeekBar.OnSeekBarChangeListener {

    private val mVolumeSceneViewModel by viewModels<VolumeSceneViewModel>({ requireParentFragment() })

    private var mInAdapter by autoCleared<VolumeChannelAdapter>()
    private var mOutAdapter by autoCleared<VolumeChannelAdapter>()


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(720), SizeUtils.dp2px(453), Gravity.CENTER)
    }

    override fun initView(root: View) {
        mBinding.tabLayout.addOnTabSelectedListener(this)
        mBinding.previewWidget.onMainStreamSelected = { i ->
            mVolumeSceneViewModel.postMainStream(i)
        }
        val tabNames =
            arrayOf(
                getString(R.string.select_scene_signal),
                getString(R.string.mic),
                getString(R.string.loudspeaker)
            )
        tabNames.forEach {
            val tab = mBinding.tabLayout.newTab()
            val textView = TextView(context)
            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(
                Utils.getApp().resources.getColor(R.color.color_318EF8),
                Utils.getApp().resources.getColor(R.color.color_222222)
            )
            val colorStateList = ColorStateList(states, colors)
            textView.text = it
            textView.textSize = 21f
            textView.gravity = Gravity.CENTER

            textView.setTextColor(colorStateList)
            textView.typeface = Typeface.DEFAULT_BOLD
            tab.customView = textView
            mBinding.tabLayout.addTab(tab)
        }
        mBinding.btnClose.setOnClickListener {
            dismiss()
        }
        initSeekbar()
        initSilentButton()
        initAudioList()
    }

    private fun initAudioList() {
        mInAdapter = mInAdapter ?: VolumeChannelAdapter(object : VolumeChannelAdapter.Callback {
            override fun onClickedSilentButton(channel: VolumeChannel, isSilent: Boolean) {
                val level =
                    if (channel.isSilent) channel.volumnLevel - 256 else channel.volumnLevel + 256
                mVolumeSceneViewModel.setAudioLevel(channel.channelName,level)
            }

            override fun onValueChanged(channel: VolumeChannel, value: Int) {
                mVolumeSceneViewModel.setAudioLevel(channel.channelName,value)
            }

        })
        mBinding.rvInAudio.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvInAudio.adapter = mInAdapter
        mOutAdapter = mOutAdapter ?: VolumeChannelAdapter(object : VolumeChannelAdapter.Callback {
            override fun onClickedSilentButton(channel: VolumeChannel, isSilent: Boolean) {
                val level =
                    if (channel.isSilent) channel.volumnLevel - 256 else channel.volumnLevel + 256
                mVolumeSceneViewModel.setAudioLevel(channel.channelName,level)
            }

            override fun onValueChanged(channel: VolumeChannel, value: Int) {
                mVolumeSceneViewModel.setAudioLevel(channel.channelName,value)
            }

        })
        mBinding.rvOutAudio.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvOutAudio.adapter = mOutAdapter
    }


    private fun initSilentButton() {
//        val onClicked = View.OnClickListener {
//            var level: Int = 0
//            var name: String = ""
//            if (it.id == R.id.cb_silent1) {
//                val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//                if (channels.size < 2) return@OnClickListener
//                level =
//                    if (channels[1].isSilent) channels[1].volumnLevel - 256 else channels[1].volumnLevel + 256
//                name = channels[1].channelName
//            } else if (it.id == R.id.cb_silent2) {
//                val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//                if (channels.size < 3) return@OnClickListener
//                level =
//                    if (channels[2].isSilent) channels[2].volumnLevel - 256 else channels[2].volumnLevel + 256
//                name = channels[2].channelName
//            } else if (it.id == R.id.cb_silent3) {
//                val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//                if (channels.size < 4) return@OnClickListener
//                level =
//                    if (channels[3].isSilent) channels[3].volumnLevel - 256 else channels[3].volumnLevel + 256
//                name = channels[3].channelName
//            } else if (it.id == R.id.cb_silent4) {
//                val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//                if (channels.size < 5) return@OnClickListener
//                level =
//                    if (channels[4].isSilent) channels[4].volumnLevel - 256 else channels[4].volumnLevel + 256
//                name = channels[4].channelName
//            } else if (it.id == R.id.cb_master_silent) {
//                val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//                if (channels.size < 1) return@OnClickListener
//                level =
//                    if (channels[0].isSilent) channels[0].volumnLevel - 256 else channels[0].volumnLevel + 256
//                name = channels[0].channelName
//            } else if (it.id == R.id.cb_silent5) {
//                val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//                if (channels.size < 6) return@OnClickListener
//                level =
//                    if (channels[5].isSilent) channels[5].volumnLevel - 256 else channels[5].volumnLevel + 256
//                name = channels[5].channelName
//            }
//            mVolumeSceneViewModel.setAudioLevel(name, level)
//        }
//        mBinding.cbSilent1.setOnClickListener(onClicked)
//        mBinding.cbSilent2.setOnClickListener(onClicked)
//        mBinding.cbSilent3.setOnClickListener(onClicked)
//        mBinding.cbSilent4.setOnClickListener(onClicked)
//        mBinding.cbSilent5.setOnClickListener(onClicked)
//        mBinding.cbMasterSilent.setOnClickListener(onClicked)
    }

    private fun initSeekbar() {
//        mBinding.sbAudio1.setOnSeekBarChangeListener(this)
//        mBinding.sbAudio2.setOnSeekBarChangeListener(this)
//        mBinding.sbAudio3.setOnSeekBarChangeListener(this)
//        mBinding.sbAudio4.setOnSeekBarChangeListener(this)
//        mBinding.sbAudio5.setOnSeekBarChangeListener(this)
//        mBinding.sbMaster.setOnSeekBarChangeListener(this)
    }

    override fun onDialogCreated(dialog: Dialog) {
        super.onDialogCreated(dialog)
        mVolumeSceneViewModel.loadCamPreviewInfo()
        mVolumeSceneViewModel.loadVolumeChannels()
    }

    override fun onDestroy() {
        mBinding.previewWidget.onMainStreamSelected = null
        super.onDestroy()
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_volume_scene
    }

    override fun onBindViewModel2Layout(binding: DialogVolumeSceneBinding) {
        binding.volumeSceneViewModel = mVolumeSceneViewModel
    }

    override fun observeVM() {
        mVolumeSceneViewModel.camPreviewInfo.observe(this) {
            //post?????????previewWidget?????????????????????????????????setCamPreviewInfo???????????????
            mBinding.previewWidget.post {

                mBinding.previewWidget.setCamPreviewInfo(it)
            }
        }
        mVolumeSceneViewModel.audioInChannels.observe(this) {
            mInAdapter?.setDatasWithDiff(it)
        }

        mVolumeSceneViewModel.audioOutChannels.observe(this) {
            mOutAdapter?.setDatasWithDiff(it)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        mBinding.group1.visibility = if (tab.position == 0) View.VISIBLE else View.GONE
        if (tab.position == 1) {
            mBinding.group2.visibility = View.VISIBLE
        } else {
            mBinding.group2.visibility = View.GONE
        }
        mBinding.group3.visibility = if (tab.position == 2) View.VISIBLE else View.GONE
        mVolumeSceneViewModel.tabIndex.value = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
//        val channels = mVolumeSceneViewModel.volumeChannels.value ?: emptyList()
//        if (seekBar.id == R.id.sb_audio1) {
//            if (channels.size < 2) return
//            mVolumeSceneViewModel.setAudioLevel(channels[1].channelName, seekBar.progress)
//        } else if (seekBar.id == R.id.sb_audio2) {
//            if (channels.size < 3) return
//            mVolumeSceneViewModel.setAudioLevel(channels[2].channelName, seekBar.progress)
//        } else if (seekBar.id == R.id.sb_audio3) {
//            if (channels.size < 4) return
//            mVolumeSceneViewModel.setAudioLevel(channels[3].channelName, seekBar.progress)
//        } else if (seekBar.id == R.id.sb_audio4) {
//            if (channels.size < 5) return
//            mVolumeSceneViewModel.setAudioLevel(channels[4].channelName, seekBar.progress)
//        } else if (seekBar.id == R.id.sb_master) {
//            if (channels.size < 5) return
//            mVolumeSceneViewModel.setAudioLevel(channels[0].channelName, seekBar.progress)
//        } else if (seekBar.id == R.id.sb_audio5) {
//            if (channels.size < 6) return
//            mVolumeSceneViewModel.setAudioLevel(channels[5].channelName, seekBar.progress)
//        }
    }

}