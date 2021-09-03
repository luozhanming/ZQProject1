package cn.com.ava.zqproject.common

import cn.com.ava.lubosdk.entity.zq.ApplySpeakUser
import com.blankj.utilcode.util.CacheMemoryUtils

/**
 * 申请发言需求管理
 * */
object ApplySpeakManager {

    private val mCacheMemoryUtils by lazy {
        CacheMemoryUtils.getInstance("apply_speak", 20)
    }

    private val keyCache: MutableList<String> by lazy {
        arrayListOf()
    }

    fun addApplySpeakUser(user: ApplySpeakUser) {
        val userInCache = mCacheMemoryUtils.get<ApplySpeakUser>(user.name ?: "")
        if (userInCache == null) {  //找不到就是过期或者没有
            if (user.name !in keyCache) {
                keyCache.add(user.name ?: "")
            } else {
                keyCache.remove(user.name ?: "")
                keyCache.add(user.name ?: "")
            }
            mCacheMemoryUtils.put(user.name ?: "", user, 20)
        }
    }

    fun removeApplySpeakUser(user: ApplySpeakUser) {
        val userInCache = mCacheMemoryUtils.get<ApplySpeakUser>(user.name ?: "")
        if (userInCache != null) {
            mCacheMemoryUtils.remove(user.name ?: "")
        }
        if (user.name ?: "" in keyCache) {
            keyCache.remove(user.name ?: "")
        }
    }

    /**
     * 获取所有申请发言的用户
     * */
    fun getApplySpeakUsers(): List<ApplySpeakUser> {
        val list = arrayListOf<ApplySpeakUser>()
        val iterator = keyCache.iterator()
        while (iterator.hasNext()) {
            val name = iterator.next()
            val applySpeakUser = mCacheMemoryUtils.get<ApplySpeakUser>(name)
            if(applySpeakUser==null){  //找不到表示已经过期了
                iterator.remove()
                continue
            }else{
                list.add(applySpeakUser)
            }
        }
        return list
    }

    /**
     * 重置
     * */
    fun reset() {
        mCacheMemoryUtils.clear()
        keyCache.clear()
    }
}