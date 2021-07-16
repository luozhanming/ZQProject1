package cn.com.ava.zqproject.vo

/**
 * 为对象添加状态
 */
class StatefulView<T>(val obj: T) {

    var isSelected: Boolean = false
    var isChecked: Boolean = false
}