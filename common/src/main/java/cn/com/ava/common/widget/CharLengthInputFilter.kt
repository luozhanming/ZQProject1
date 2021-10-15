package cn.com.ava.common.widget

import android.text.InputFilter
import android.text.Spanned
import cn.com.ava.common.extension.isChinese

class CharLengthInputFilter(val maxLength: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var length = 0
        val diterator = dest?.iterator()
        while (diterator?.hasNext() == true) {
            val nextChar = diterator.nextChar()
            if (nextChar.isChinese()) {
                length += 3
            } else {
                length += 1
            }
        }
        val siterator = source?.iterator()
        val charArray = arrayListOf<Char>()
        while (siterator?.hasNext() == true) {
            val nextChar = siterator.nextChar()
            if (nextChar.isChinese()) {
                if (length + 3 > maxLength) {
                    continue
                } else {
                    charArray.add(nextChar)
                    length += 3
                }
            } else {
                if (length + 1 > maxLength) {
                    continue
                } else {
                    charArray.add(nextChar)
                    length += 1
                }
            }
        }
        val sb = StringBuffer()
        sb.append(charArray.toCharArray())
        return sb.toString()
    }
}