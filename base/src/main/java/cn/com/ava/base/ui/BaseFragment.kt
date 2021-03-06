package cn.com.ava.base.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.com.ava.common.extension.bindExtras
import cn.com.ava.common.util.ScreenCompatUtil
import cn.com.ava.common.util.logd

abstract class BaseFragment<B : ViewDataBinding> : Fragment(), MVVMView<B> {

    protected lateinit var mBinding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logd("onCreateView")
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logd("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        onBindViewModel2Layout(mBinding)
        initView()
        bindListener()
        observeVM()
    }

    open fun bindListener() {

    }

    override fun onBindViewModel2Layout(binding: B) {

    }

    protected abstract fun getLayoutId(): Int


    override fun onAttach(context: Context) {
        super.onAttach(context)
        logd("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("onCreate")
        ScreenCompatUtil.initScreenCompat(requireActivity() as AppCompatActivity,requireActivity().application,true,1280)
        bindExtra()

    }

    open fun initView() {

    }

    private fun bindExtra() {
        bindExtras()
    }

    override fun observeVM() {

    }

    override fun onPause() {
        super.onPause()
        childFragmentManager.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        logd("onStart")
    }


    override fun onStop() {
        super.onStop()
        logd("onStop")
    }



    override fun onDestroy() {
        super.onDestroy()
        logd("onDestroy")
    }

    private fun releaseBinding() {
        try {
          //  val fields = this::class.java.declaredFields  //?????????????????????????????????????????????????????????
            val fields = BaseFragment::class.java.declaredFields
            val mBinding = fields.firstOrNull {
                it.name == "mBinding"
            }
            mBinding?.isAccessible = true
            mBinding?.set(this,null)
            logd("releaseBinding")
        }catch (e:NoSuchFieldException){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
        //????????????
        releaseBinding()
        logd("onDestroyView")
    }

    open fun onBackPressed(): Boolean {
        return false
    }
}