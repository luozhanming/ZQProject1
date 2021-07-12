package cn.com.ava.lubosdk.spquery

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.Login
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.RSAKey
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.RSAUtils
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
/**
 * 登录
 * */
class LoginSPQuery(
    val username: String,
    val password: String,
    val isEx: Boolean = false,
    override var onResult: (QueryResult) -> Unit,
    val rsaKey: RSAKey? = null,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<Login> {

    override val name: String
        get() = "Login"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        if (isEx) {
            return linkedMapOf<String, String>().apply {
                this["action"] = "1"
                this["cmd"] = "25"
                val user = generateUser(username, password, rsaKey!!) ?: ""
                this["user"] = user
                this["app"] = "0"
            }
        } else {
            return linkedMapOf<String, String>().apply {
                this["action"] = "1"
                this["cmd"] = "25"
                this["username"] = username
                this["password"] = EncryptUtil.encryptMD5ToString(password).toLowerCase()
                this["app"] = "0"
            }
        }
    }


    override fun build(response: String): Login {
        val infos = response.split("\\|".toRegex()).toTypedArray()
        val result = Login()
        if ("2" == infos[0] && response.contains("SLEEP")) {   //休眠且用户密码正确
            result.key = infos[1]
            result.isLoginSuccess = true
            result.isSleep = true
            result.resultCode = infos[0]
            result.resultMsg = "设备休眠中"
        } else if ("1" == infos[0]) {       //登录成功
            result.key = infos[1]
            result.isLoginSuccess = true
            result.isSleep = false
            result.resultCode = infos[0]
            result.resultMsg = "登录成功"
            if(isEx){
                result.token = infos[4]
            }
        } else if ("-1" == infos[0]) {       //用户名密码不正确
            result.key = ""
            result.isLoginSuccess = false
            result.isSleep = false
            result.resultCode = infos[0]
            result.resultMsg = "用户名或密码不正确"
        } else if ("-6" == infos[0]) {
            result.key = ""
            result.isLoginSuccess = false
            result.isSleep = false
            result.resultCode = infos[0]
            result.resultMsg = "系统已关闭或未开启"
        } else if ("-4" == infos[0]) {
            result.key = ""
            result.isLoginSuccess = false
            result.isSleep = false
            result.resultCode = infos[0]
            result.resultMsg = "用户名或密码不正确"
        } else {
            result.key = ""
            result.isLoginSuccess = false
            result.isSleep = false
            result.resultCode = infos[0]
            result.resultMsg = "系统无响应"
        }
        result.setUsername(username)
        result.setPassword(password)
        return result
    }


    /**
     * 加密获得user参数
     */
    private fun generateUser(
        username: String,
        password: String,
        rsaKey: RSAKey
    ): String? {
        val userpsw =
            username + "&" + EncryptUtil.encryptMD5ToString(password).toLowerCase()
        try {
            val publicKey: RSAPublicKey =
                RSAUtils.getPublicKey(rsaKey.getModulus(), rsaKey.getExponent())
            return RSAUtils.encryptByPublicKey(userpsw, publicKey)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}