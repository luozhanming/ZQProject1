package cn.com.ava.base.ui

import androidx.databinding.ViewDataBinding

interface MVVMView<B : ViewDataBinding> {
    fun onBindViewModel2Layout(binding: B)
    fun observeVM()


}