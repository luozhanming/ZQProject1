package cn.com.ava.zqproject.ui.common

import androidx.lifecycle.MutableLiveData
import cn.com.ava.zqproject.vo.LoadMoreState

interface CanLoadMore {
    val loadMoreState: MutableLiveData<LoadMoreState>
}