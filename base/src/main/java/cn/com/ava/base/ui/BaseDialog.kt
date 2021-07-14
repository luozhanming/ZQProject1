package cn.com.ava.base.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import cn.com.ava.common.R

abstract class BaseDialog(val style: Int = R.style.CommonDialogStyle) : DialogFragment() {


    private var mDismissCallback: ((DialogInterface) -> Unit)? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, style)
        dialog.setContentView(getLayoutId())
        initView(dialog.window.decorView)
        onDialogCreated(dialog)
        val windowOptions: WindowOptions = getWindowOptions()
        dialog.window.setLayout(windowOptions.width, windowOptions.height)
        dialog.window.setGravity(windowOptions.gravity)
        dialog.setCanceledOnTouchOutside(windowOptions.canTouchOutsideCancel)
        return dialog
    }

    protected fun onDialogCreated(dialog: Dialog) {

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

    fun setDismissCallback(callback:(DialogInterface) -> Unit){
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


    data class WindowOptions(val width: Int, val height: Int, val gravity: Int,val canTouchOutsideCancel:Boolean)
}