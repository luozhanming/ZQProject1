package cn.com.ava.common.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import cn.com.ava.common.util.Extra

inline fun Activity.bindExtras(){
        val clazz = this::class.java
        val array = clazz.declaredFields
        val args = intent.extras
        array.forEach { field->
            field.isAccessible = true
            val annotations = field.annotations
            annotations.forEach { annotation->
                if(annotation is Extra){
                    val key = annotation.value
                    if(args?.containsKey(key) == true){//赋值
                        field.set(this,args[key])
                    }
                }
            }
        }
}


inline fun Fragment.bindExtras(){
        val clazz = this::class.java
        val array = clazz.declaredFields
        val args = arguments
        array.forEach { field->
            field.isAccessible = true
            val annotations = field.annotations
            annotations.forEach { annotation->
                if(annotation is Extra){
                    val key = annotation.value
                    if(args?.containsKey(key) == true){//赋值
                        field.set(this,args[key])
                    }
                }
            }
        }
}
