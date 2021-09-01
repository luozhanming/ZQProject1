package cn.com.ava.zqproject.common

import cn.com.ava.zqproject.R

object LayoutButtonHelper {
    private val layoutMap by lazy {
        hashMapOf<String,Int>().apply {
            this["V1&2"] = R.mipmap.btn_layout1
            this["V6_1"] = R.mipmap.btn_layout2
            this["V6_2"] = R.mipmap.btn_layout3
            this["V5_4"]= R.mipmap.btn_layout4
            this["V2PIP1"] = R.mipmap.btn_layout5
            this["V6PIP1"] = R.mipmap.btn_layout6
            this["V6PIP1_2"] = R.mipmap.btn_layout7
            this["V3_1"] = R.mipmap.btn_layout8
            this["V3_2"]= R.mipmap.btn_layout9
            this["V3_2_2"] = R.mipmap.btn_layout10

            this["V3PIP1"] = R.mipmap.btn_layout11
            this["V3PIP1_2"] = R.mipmap.btn_layout12
            this["V5_1"] = R.mipmap.btn_layout13
            this["V5_2"]= R.mipmap.btn_layout14
            this["V5PIP1"] = R.mipmap.btn_layout15
            this["V5PIP1_2"] = R.mipmap.btn_layout16
            this["V2_1"] = R.mipmap.btn_layout17
            this["V1_2"] = R.mipmap.btn_layout18
            this["V1_1"]= R.mipmap.btn_layout18
            this["V4_1"] = R.mipmap.btn_layout19

            this["V4_2"] = R.mipmap.btn_layout20
            this["V4_3"] = R.mipmap.btn_layout21
            this["V4PIP1"] = R.mipmap.btn_layout22
            this["V4PIP1_2"]= R.mipmap.btn_layout23
            this["V6_4"] = R.mipmap.btn_layout24
            this["V8_1"] = R.mipmap.btn_layout25
            this["V8_2"] = R.mipmap.btn_layout26
            this["V8_4"] = R.mipmap.btn_layout27
            this["V8PIP1"]= R.mipmap.btn_layout28
            this["V8PIP1_2"] = R.mipmap.btn_layout29

            this["V7_1"] = R.mipmap.btn_layout30
            this["V7_2"] = R.mipmap.btn_layout31
            this["V7_4"] = R.mipmap.btn_layout32
            this["V7PIP1"]= R.mipmap.btn_layout33
            this["V7PIP1_2"] = R.mipmap.btn_layout34
            this["V1A3"] = R.mipmap.btn_layout45
            this["V1A4"] = R.mipmap.btn_layout35
            this["V1A5"] = R.mipmap.btn_layout37
            this["V1A6"]= R.mipmap.btn_layout39
            this["V1A7"]= R.mipmap.btn_layout41
            this["V1A8"] = R.mipmap.btn_layout43

        }
    }

    fun getLayoutDrawable(layoutCmd: String?): Int {
        return if (layoutMap.containsKey(layoutCmd)) {
            layoutMap[layoutCmd]!!
        } else {
            -1
        }
    }

}