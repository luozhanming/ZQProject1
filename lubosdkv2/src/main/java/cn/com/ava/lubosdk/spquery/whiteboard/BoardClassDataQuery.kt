package cn.com.ava.lubosdk.spquery.whiteboard

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.whiteboard.BoardClassDataState
import cn.com.ava.lubosdk.entity.whiteboard.ClassroomControlEntity
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils


/**
 * 获取所有课室白板信息
 * */
class BoardClassDataQuery(override var onResult: (QueryResult) -> Unit,
                          override var onError: ((Throwable) -> Unit)? = null) : ISPQuery<BoardClassDataState> {
    override val name: String
        get() = "BoardClassData"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = "WhiteBoard_getAllWhiteBoardInfo"
        }
    }

    override fun build(response: String): BoardClassDataState {
        if ("error" in response) throw IllegalStateException("$name build failed.")
        if (response.startsWith("WhiteBoardRet_getAllWhiteBoardInfo")) {
            val state: String = response.substring(response.indexOf("=") + 1).trim()
            val result = state.split("_".toRegex()).toTypedArray()
            val boardState = BoardClassDataState()
            val classNum = result[0].toInt()
            val curPage = result[1].toInt()
            boardState.classNum = classNum
            boardState.curPage = curPage
            val entities = arrayListOf<ClassroomControlEntity>()
            for (i in 2 until result.size) {
                val entity = ClassroomControlEntity()
                val info = result[i].replace("{", "").replace("}", "").split(",".toRegex()).toTypedArray()
                val className = info[0].toInt()
                entity.classRoomName = when (className) {
                    1 -> "本地"
                    else -> "远程${className - 1}"
                }
                val canWrite = info[3].toInt() == 1
                val isConnected = info[1].toInt() == 1
                val id = info[0]
                entity.isCanWrite = canWrite
                entity.isConnected = isConnected
                entity.id = id
                entities.add(entity)
            }
            boardState.entities = entities
            return boardState
        } else throw IllegalStateException("$name build failed.")
    }
}