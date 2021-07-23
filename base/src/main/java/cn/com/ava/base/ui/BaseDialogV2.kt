package cn.com.ava.base.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import cn.com.ava.common.R

abstract class BaseDialogV2<B : ViewDataBinding>(val style: Int = R.style.CommonDialogStyle) :
    DialogFragment(), MVVMView<B> {


    protected val mBinding: B by lazy {
        val binding = DataBindingUtil.inflate<B>(layoutInflater,getLayoutId(),null,false)
        binding
    }

    private var mDismissCallback: ((DialogInterface) -> Unit)? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, style)
        mBinding.lifecycleOwner = this
        initView(dialog.window.decorView)
        onDialogCreated(dialog)
        val windowOptions: WindowOptions = getWindowOptions()
        dialog.window.setLayout(windowOptions.width, windowOptions.height)
        dialog.window.setGravity(windowOptions.gravity)
        dialog.setCanceledOnTouchOutside(windowOptions.canTouchOutsideCancel)
        return dialog
    }

    fun onDialogCreated(dialog: Dialog) {
        onBindViewModel2Layout(mBinding)
        observeVM()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mDismissCallback?.invoke(dialog)
    }

    override fun onStart() {
        super.onStart()
        setWindowImmersive(true)
    }

    fun setDismissCallback(callback: (DialogInterface) -> Unit) {
        mDismissCallback = callback
    }


    private fun setWindowImmersive(immersive: Boolean) {
        dialog?.apply {
            val setUI: () -> Unit = {
                val systemUIVisibility: Int = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                window.decorView.systemUiVisibility = systemUIVisibility
            }
            setUI()
            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    setUI()
                }
            }
        }

    }


    abstract fun getWindowOptions(): WindowOptions

    abstract fun initView(root: View)


    abstract fun getLayoutId(): Int


    data class WindowOptions(
        val width: Int,
        val height: Int,
        val gravity: Int,
        val canTouchOutsideCancel: Boolean = true
    )
}