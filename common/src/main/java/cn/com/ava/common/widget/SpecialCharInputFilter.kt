package cn.com.ava.common.widget

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import cn.com.ava.common.extension.isChinese

/**
 * 过滤特殊字符
 */
class SpecialCharInputFilter:InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var sourceStr = source?.toString()
        if(TextUtils.isEmpty(sourceStr))return ""
        //过滤空格
        var replace1 = sourceStr?.replace(" ", "")
        //过滤特殊字符
        val specialChars = arrayListOf<Char>()
        replace1?.iterator()?.apply {
            while (hasNext()){
                val char = nextChar()
                if(!char.isChinese() && !char.isLetterOrDigit() &&char !='-'){  //是特殊字符
                    specialChars.add(char)
                }
            }
        }
        specialChars.forEach {
            replace1 = replace1?.replace(it.toString(), "")
        }

        return if(TextUtils.isEmpty(replace1))"" else replace1!!
    }

}

