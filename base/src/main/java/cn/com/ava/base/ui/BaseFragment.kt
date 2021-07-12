package cn.com.ava.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<B : ViewDataBinding>:Fragment() {

    protected  lateinit var  mBinding:B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    protected abstract fun getLayoutId(): Int
}