package cn.com.ava.zqproject.ui.common

import androidx.lifecycle.MutableLiveData
import cn.com.ava.zqproject.vo.RefreshState

interface CanRefresh {
    val refreshState: MutableLiveData<RefreshState>
}