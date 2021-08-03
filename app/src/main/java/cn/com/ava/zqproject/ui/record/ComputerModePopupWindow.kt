package cn.com.ava.zqproject.ui.record

import android.content.Context
import android.view.View
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.ComputerModeManager

class ComputerModePopupWindow(context: Context,val onModeChanged:(()->Unit)?=null) : BasePopupWindow(context), View.OnClickListener {

    private var btnMode1: View? = null
    private var btnMode2: View? = null
    private var btnMode3: View? = null
    private var btnMode4: View? = null


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(192), SizeUtils.dp2px(400), true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_computer_mode
    }

    override fun initView(root: View) {
        btnMode1 = root.findViewById(R.id.btn_mode1)
        btnMode2 = root.findViewById(R.id.btn_mode2)
        btnMode3 = root.findViewById(R.id.btn_mode3)
        btnMode4 = root.findViewById(R.id.btn_mode4)
        btnMode1?.setOnClickListener(this)
        btnMode2?.setOnClickListener(this)
        btnMode3?.setOnClickListener(this)
        btnMode4?.setOnClickListener(this)
        showSelectedMode()
    }

    private fun showSelectedMode() {
        btnMode1?.isSelected = ComputerModeManager.getCurMode()==ComputerModeManager.MODE1
        btnMode2?.isSelected = ComputerModeManager.getCurMode()==ComputerModeManager.MODE2
        btnMode3?.isSelected = ComputerModeManager.getCurMode()==ComputerModeManager.MODE3
        btnMode4?.isSelected = ComputerModeManager.getCurMode()==ComputerModeManager.MODE4
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_mode1->ComputerModeManager.setCurComputerMode(ComputerModeManager.MODE1)
            R.id.btn_mode2->ComputerModeManager.setCurComputerMode(ComputerModeManager.MODE2)
            R.id.btn_mode3->ComputerModeManager.setCurComputerMode(ComputerModeManager.MODE3)
            R.id.btn_mode4->ComputerModeManager.setCurComputerMode(ComputerModeManager.MODE4)
        }
        showSelectedMode()
        onModeChanged?.invoke()
    }




}