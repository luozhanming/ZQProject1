package cn.com.ava.zqproject.ui.meeting

import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.vo.LayoutSignalSelect
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.schedulers.Schedulers

class SelectLayoutManagerViewModel : BaseViewModel() {

    companion object {
        const val LAYOUT_AUTO = 0
        const val LAYOUT_1 = 1
        const val LAYOUT_2 = 2
        const val LAYOUT_3 = 3
        const val LAYOUT_4 = 4
        const val LAYOUT_6 = 6
        const val LAYOUT_8 = 8

        const val STEP_1 = 1
        const val STEP_2_1 = 2
        const val STEP_2_2 = 3
    }


    private val noSignalUser: LinkedUser = LinkedUser().apply {
        number = -1
    }

    /**
     * 操作第几步
     * */
    val step: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = STEP_1
        }
    }

    /**
     * 间隔周期
     * */
    val patrolPeriod:MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply {
            value = "10"
        }
    }

    /**
     * 选择第几个布局
     * */
    val layoutSelect: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = -1
        }
    }

    val bigLayoutDrawableId: MediatorLiveData<Int> by lazy {
        MediatorLiveData<Int>().apply {
            addSource(layoutSelect) {
                val drawableId = when (it) {
                    LAYOUT_1 -> R.mipmap.ic_layout1_big
                    LAYOUT_2 -> R.mipmap.ic_layout2_big
                    LAYOUT_3 -> R.mipmap.ic_layout3_big
                    LAYOUT_4 -> R.mipmap.ic_layout4_big
                    LAYOUT_6 -> R.mipmap.ic_layout6_big
                    LAYOUT_8 -> R.mipmap.ic_layout8_big
                    else -> 0
                }
                value = drawableId
            }
        }
    }


    val linkUsers: MutableLiveData<List<LinkedUser>> by lazy {
        MutableLiveData()
    }


    /**
     * 未选择的人
     * */
    val unSelectSignals: MediatorLiveData<List<LinkedUser>> by lazy {
        MediatorLiveData<List<LinkedUser>>().apply {
            addSource(linkUsers) {
                //第一个可选择信号一定是无信号
                val list = mutableListOf<LinkedUser>()
                list.add(noSignalUser)
                list.addAll(it)
                postValue(list)
            }
        }
    }

    val layoutSignals: MediatorLiveData<List<LayoutSignalSelect>> by lazy {
        MediatorLiveData<List<LayoutSignalSelect>>().apply {
            addSource(layoutSelect) {
                //选择了布局后
                val arrayList = arrayListOf<LayoutSignalSelect>()
                val count = it
//                for (i in 0 until count) {
//                   val layoutselected = LayoutSignalSelect(i+1,null)
//                    arrayList.add(layoutselected)
//                }
                signalSelect.value?.clear()
                signalSelect.postValue(signalSelect.value ?: SparseArray())
                postValue(arrayList)
            }
            addSource(signalSelect) {
                val arrayList = arrayListOf<LayoutSignalSelect>()
                val count = layoutSelect.value ?: 0
                val lastSignals = value ?: emptyList()
                for (i in 0 until count) {
                    if (it.containsKey(i + 1)) {   //如果存在
                        val user = it.get(i + 1)
                        val layoutselect = LayoutSignalSelect(i + 1, user)
                        arrayList.add(layoutselect)
                    } else {  //无信号
                        val layoutselect = LayoutSignalSelect(i + 1, null)
                        arrayList.add(layoutselect)
                    }
                }
                postValue(arrayList)
            }
        }
    }

    /**
     * 已选择信号(无信号则请移除信号索引的值)
     * */
    val signalSelect: MutableLiveData<SparseArray<LinkedUser>> by lazy {
        MutableLiveData<SparseArray<LinkedUser>>().apply {
            value = SparseArray()
        }
    }


    val layoutSure: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    fun getInteracMemberInfo() {
        layoutSignals
        unSelectSignals
        mDisposables.add(InteracManager.getInteracInfo()
            .map {
                it.onlineList.filter {
                    it.onlineState == 1
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                linkUsers.postValue(it)
            }, {
                logPrint2File(it)
            })
        )
    }

    fun selectSignal(i: Int, user: LinkedUser) {
        logd("选择信号序号$i,信号$user")
        //如果之前此信号
        val signalSelectValue = signalSelect.value
        val unselects = mutableListOf<LinkedUser>()
        unselects.addAll(unSelectSignals.value ?: emptyList())
        if (user.number == -1) {   //选择无信号
            val pre = signalSelectValue?.get(i)
            if (pre != null && pre?.number != -1) {
                unselects.add(pre)
                unSelectSignals.postValue(unselects)
            }
            signalSelectValue?.remove(i)
            signalSelect.postValue(signalSelectValue)
        } else if (signalSelectValue?.containsKey(i) == true) {  //存在信号
            val pre = signalSelectValue?.get(i)
            //移除
            signalSelectValue?.remove(i)
            //添加到未选择信号
            unselects.add(pre)
            unselects.remove(user)
            unSelectSignals.postValue(unselects)

            signalSelectValue?.put(i, user)
            signalSelect.postValue(signalSelectValue)
        } else {
            unselects.remove(user)
            unSelectSignals.postValue(unselects)

            signalSelectValue?.put(i, user)
            signalSelect.postValue(signalSelectValue)
        }
    }

    /**
     * 布局确定
     * */
    fun layoutSure() {
        val layoutMode = layoutSelect.value
        if (layoutMode != LAYOUT_AUTO) {
            val signalSelectValue = signalSelect.value
            val windowCount = layoutMode ?: 0
            val sendLayout = mutableListOf<Int>()
            for (i in 0 until windowCount) {
                if (signalSelectValue?.containsKey(i + 1) == true) {
                    val get = signalSelectValue.get(i + 1)
                    if (get != null) {
                        sendLayout.add(get.number)
                    }
                } else {  //无信号
                    sendLayout.add(-1)
                }
            }
            mDisposables.add(
                ZQManager.setVideoLayout(windowCount, sendLayout)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        layoutSure.postValue(OneTimeEvent(true))
                    }, {
                        ToastUtils.showShort(getResources().getString(R.string.set_meeting_layout_failed))
                        logPrint2File(it)
                    })
            )
        }
    }

    /**
     * 轮播成员确定
     * @param selectedUser
     * */
    fun patrolSure(selectedUser: List<LinkedUser>) {
        logd("轮播成员确定：$selectedUser")
        //TODO
    }
}