package br.com.productcatalog.library.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class SimpleAdapter<in C, CVH : ViewHolder>(
    private val itemList: MutableList<C> = mutableListOf()
) : Adapter<ViewHolder>() {

    override fun getItemCount(): Int = itemList.size

    abstract fun onCreateItemViewHolder(parent: ViewGroup): CVH

    abstract fun onBindViewHolder(holder: CVH, position: Int, item: C)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return onCreateItemViewHolder(parent)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val childItem = itemList[position]
        onBindViewHolder(holder as CVH, position, childItem)
    }

    protected fun inflateView(
        @LayoutRes layoutResource: Int,
        rootView: ViewGroup,
        attachToRoot: Boolean = false
    ): View {
        val inflater = LayoutInflater.from(rootView.context)
        return inflater.inflate(layoutResource, rootView, attachToRoot)
    }

    open fun addItem(item: C) {
        itemList.add(item)
        notifyDataSetChanged()
    }

    open fun setItems(items: List<C>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    open fun clearAll() {
        itemList.clear()
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean = itemList.isEmpty()
}
