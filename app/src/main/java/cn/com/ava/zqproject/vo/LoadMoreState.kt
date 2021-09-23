package cn.com.ava.zqproject.vo

data class LoadMoreState(    val isLoadMore: Boolean,  /*true:刷新 false:加载*/
                             val hasError: Boolean/*是否有错误*/)
