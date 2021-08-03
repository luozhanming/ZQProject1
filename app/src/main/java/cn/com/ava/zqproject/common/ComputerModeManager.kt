package cn.com.ava.zqproject.common

import android.util.SparseArray
import androidx.annotation.IntDef
import androidx.core.util.valueIterator
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.GeneralManager
import io.reactivex.schedulers.Schedulers

/**
 * 录课下接入电脑模式管理
 * */
object ComputerModeManager {

    const val MODE1 = 0
    const val MODE2 = 1
    const val MODE3 = 2
    const val MODE4 = 3

    @IntDef(MODE1, MODE2, MODE3, MODE4)
    annotation class Mode


    /**当前选择的接入电脑Mode*/
    private var curSelectedComputerMode: Int = MODE1

    /**电脑索引*/
    private var computerIndex: String = "V1"


    /**
     * 各个接入模式的指令代码
     * */
    private val modeCommands: SparseArray<String> by lazy {
        SparseArray()
    }

    /**
     * 获取电脑索引
     * */
    fun getComputerIndex() {
        GeneralManager.getLuboInfo()
            .map {
                it.computerIndex
            }.subscribeOn(Schedulers.io())
            .subscribe({
                computerIndex = it
                logd("获取电脑窗口索引：${it}")
                addCommands(it)
            }, {
                logPrint2File(it)
            })
    }

    /**
     * 判断是否正接入电脑
     * */
    fun isPluginComputer(curPreview: String?): Boolean {

        val valueIterator = modeCommands.valueIterator()
        valueIterator.forEach {
            if (it == curPreview) return true
        }
        return false

    }

    /**
     * @return 返回当前电脑窗口索引
     * */
    fun computerIndex(): String {
        return computerIndex
    }

    /**
     * 判断是否电脑画面模式
     * @param window
     * */
    fun isComputerMode(window: String): Boolean {
        val valueIterator = modeCommands.valueIterator()
        valueIterator.forEach {
            if (it == window) return true
        }
        return false
    }


    /**
     * 获取当前电脑画面模式的指令
     * */
    fun getCurModeWindowCmd(): String {
        return modeCommands.get(curSelectedComputerMode)
    }

    /**
     * 获取当前电脑画面模式
     * */
    fun getCurMode(): Int {
        return curSelectedComputerMode
    }

    /**
     * 获取哪个电脑模式
     * */
    fun getComputerMode(window: String): Int {
        val modeArray = arrayOf(MODE1, MODE2, MODE3, MODE4)
        modeArray.forEach {
            if(modeCommands.get(it).equals(window))return it
        }
        return MODE1
    }


    fun setCurComputerMode(@Mode mode: Int) {
        curSelectedComputerMode = mode
    }

    private fun addCommands(computerIndex: String) {
        var cIndex = 0
        if (computerIndex.contains("V")) {
            cIndex = computerIndex.replace("V", "").toInt()
        }
        modeCommands.put(MODE1, computerIndex)
        modeCommands.put(MODE2, "V1A${cIndex}")
        modeCommands.put(MODE3, "${computerIndex}PIP1")
        modeCommands.put(MODE4, "${computerIndex}PIP1_2")
        logd("接入电脑模式：${modeCommands}")
    }


}