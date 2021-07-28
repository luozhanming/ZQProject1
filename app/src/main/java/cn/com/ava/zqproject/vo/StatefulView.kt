package cn.com.ava.zqproject.vo

/**
 * 为对象添加按钮状态
 */
class StatefulView<T>(val obj: T) : Cloneable {

    var isSelected: Boolean = false
    var isChecked: Boolean = false

    public override fun clone(): Any {
        val stateful = StatefulView(obj)
        return stateful
    }

    override fun equals(other: Any?): Boolean {
        return obj == (other as? StatefulView<*>)?.obj
    }
}