package cn.com.ava.base.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * Base RecyclerView Adapter class for DataBinding feature.
 */
abstract class BaseAdapter<DATA>:RecyclerView.Adapter<BaseViewHolder<DATA,ViewDataBinding>>() {

    val mDatas: MutableList<DATA> by lazy {
        arrayListOf()
    }

    private val mDiffCallback:AdapterDiffCallback<DATA> by lazy {
        createDiffCallback()
    }

    abstract fun createDiffCallback():AdapterDiffCallback<DATA>

    @LayoutRes abstract fun getLayoutId(viewType:Int):Int

    abstract fun getViewHolder(viewType: Int,binding: ViewDataBinding): BaseViewHolder<DATA, ViewDataBinding>

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<DATA, ViewDataBinding> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            getLayoutId(viewType), parent, false
        )
        return getViewHolder(viewType,binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<DATA, ViewDataBinding>, position: Int) {
        val data = mDatas[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    fun setDatas(newData:List<DATA>){
        val diffResult = DiffUtil.calculateDiff(updateDiffCallback(mDatas,newData))
        mDatas.clear()
        mDatas.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }


    private fun updateDiffCallback(oldDatas: List<DATA>, newDatas: List<DATA>): AdapterDiffCallback<DATA>{
        return mDiffCallback.apply {
            oldList.clear()
            oldList.addAll(oldDatas)
            newList.clear()
            newList.addAll(newDatas)
        }
    }

    abstract class AdapterDiffCallback<DATA>():DiffUtil.Callback(){

        val oldList:MutableList<DATA> by lazy {
            arrayListOf()
        }

        val newList:MutableList<DATA> by lazy {
            arrayListOf()
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

    }


}