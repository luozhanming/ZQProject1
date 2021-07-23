package cn.com.ava.zqproject.databinding

import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapters {


    @JvmStatic
    @BindingAdapter("android:selected")
    fun setSelected(view:View,selected:Boolean){
        view.isSelected = selected
    }
}