package cn.com.ava.lubosdk

interface IControl {

    val name: String


    fun getControlParams(): LinkedHashMap<String, String>

    fun build(response: String): Boolean {
        return "-1" !in response
    }

    var onResult: (Boolean) -> Unit

    var onError: ((Throwable) -> Unit)?
}