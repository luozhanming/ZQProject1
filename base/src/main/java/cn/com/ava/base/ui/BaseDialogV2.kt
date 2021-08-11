package cn.com.ava.base.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import cn.com.ava.common.R
import cn.com.ava.common.util.logd
/**
 * MVVM模式的dialog
 *
 * */
abstract class BaseDialogV2<B : ViewDataBinding>(val style: Int = R.style.CommonDialogStyle) :
    DialogFragment(), MVVMView<B> {


    protected lateinit var mBinding:B

    private var mDismissCallback: ((DialogInterface) -> Unit)? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        logd("onCreateDialog")
        val dialog = Dialog(activity, style)
        mBinding = DataBindingUtil.inflate(layoutInflater,getLayoutId(),null,false)
        mBinding.lifecycleOwner = this
        dialog.setContentView(mBinding.root)
        initView(mBinding.root)
        bindListener()
        onDialogCreated(dialog)
        val windowOptions: WindowOptions = getWindowOptions()
        dialog.window.setLayout(windowOptions.width, windowOptions.height)
        dialog.window.setGravity(windowOptions.gravity)
        dialog.setCanceledOnTouchOutside(windowOptions.canTouchOutsideCancel)
        return dialog
    }

    open fun bindListener() {

    }

    open fun onDialogCreated(dialog: Dialog) {
        onBindViewModel2Layout(mBinding)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logd("onViewCreated")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        logd("onDissmiss")
        mDismissCallback?.invoke(dialog)
    }

    override fun onStart() {
        super.onStart()
        observeVM()
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

    override fun onDestroy() {
        super.onDestroy()
        releaseBinding()
    }

    private fun releaseBinding() {
        mBinding.unbind()
        try {
            //  val fields = this::class.java.declaredFields  //错误的用法，父类的属性不能通过反射获取
            val fields = BaseDialogV2::class.java.declaredFields
            val mBinding = fields.firstOrNull {
                it.name == "mBinding"
            }
            mBinding?.isAccessible = true
            mBinding?.set(this,null)
            logd("releaseBinding")
        }catch (e:NoSuchFieldException){

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