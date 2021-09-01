package cn.com.ava.zqproject.databinding

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.databinding.BindingAdapter

object BindingAdapters {


    @JvmStatic
    @BindingAdapter("android:selected")
    fun setSelected(view:View,selected:Boolean){
        view.isSelected = selected
    }


    @JvmStatic
    @BindingAdapter("android:srcId")
    fun setDrawableId(view:ImageView,resId:Int){
        if(resId>0){
            view.setImageResource(resId)
        }
    }


    @JvmStatic
    @BindingAdapter("android:childEnabled")
    fun setChildEnable(view:ViewGroup,enable:Boolean){
        view.isEnabled = enable
        view.children.forEach {
            it.isEnabled = enable
        }
    }



}