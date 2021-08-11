package cn.com.ava.zqproject.ui.meeting

import androidx.annotation.IntDef
import androidx.collection.arrayMapOf
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.zqproject.R
import io.reactivex.schedulers.Schedulers

class SelectLayoutManagerViewModel : BaseViewModel() {

    companion object{
        const val LAYOUT_AUTO = 1
        const val LAYOUT_1 = 2
        const val LAYOUT_2 = 3
        const val LAYOUT_3 = 4
        const val LAYOUT_4 = 5
        const val LAYOUT_6 = 6
        const val LAYOUT_8 = 7

        const val STEP_1 =1
        const val STEP_2_1 =2
        const val STEP_2_2 =3
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
     * 选择第几个布局
     * */
    val layoutSelect:MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val bigLayoutDrawableId:MediatorLiveData<Int> by lazy {
        MediatorLiveData<Int>().apply {
            addSource(layoutSelect){
                val drawableId = when(it){
                    LAYOUT_1-> R.mipmap.ic_layout1_big
                    LAYOUT_2-> R.mipmap.ic_layout2_big
                    LAYOUT_3-> R.mipmap.ic_layout3_big
                    LAYOUT_4-> R.mipmap.ic_layout4_big
                    LAYOUT_6-> R.mipmap.ic_layout6_big
                    LAYOUT_8-> R.mipmap.ic_layout8_big
                    else->0
                }
                value = drawableId
            }
        }
    }








    fun getInteracMemberInfo(){
        mDisposables.add(InteracManager.getInteracInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({
                it.toString()
            },{
                logPrint2File(it)
            }))
    }
}