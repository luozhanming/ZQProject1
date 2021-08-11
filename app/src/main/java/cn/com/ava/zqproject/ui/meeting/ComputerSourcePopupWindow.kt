package cn.com.ava.zqproject.ui.meeting

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.ui.meeting.adapter.ComputerSourceAdapter
import cn.com.ava.zqproject.vo.InteractComputerSource
import io.reactivex.schedulers.Schedulers
/**
 * 听课电脑视频源选择弹窗
 * */
class ComputerSourcePopupWindow(context: Context) : BasePopupWindow(context) {

    private var rvSourceSelect: RecyclerView? = null

    private var mSourcesAdapter:ComputerSourceAdapter? = null


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(336), ViewGroup.LayoutParams.WRAP_CONTENT, true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_computer_source
    }

    override fun initView(root: View) {
        rvSourceSelect = root.findViewById(R.id.rv_computer_select)
        rvSourceSelect?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mSourcesAdapter = ComputerSourceAdapter{
            val window = PreviewVideoWindow()
            window.index = it.computerIndex
            //业务太简单直接在这里调用接口
            WindowLayoutManager.setWindowSource(window,it.sourceCmd)
                .subscribeOn(Schedulers.io())
                .subscribe({

                },{
                    logPrint2File(it)
                })

            dismiss()
        }
        rvSourceSelect?.adapter = mSourcesAdapter
    }

    fun setSources(sources:List<InteractComputerSource>){
        mSourcesAdapter?.setDatasWithDiff(sources)
    }
}