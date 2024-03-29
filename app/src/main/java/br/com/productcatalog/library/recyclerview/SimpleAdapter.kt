package br.com.productcatalog.library.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.productcatalog.data.models.ProductResult
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class SimpleAdapter<I, VH : ViewHolder>(
    private val itemList: MutableList<I> = mutableListOf()
) : Adapter<ViewHolder>() {

    private val itemClickedSubject: PublishSubject<I> = PublishSubject.create()

    override fun getItemCount(): Int = itemList.size

    abstract fun onCreateItemViewHolder(parent: ViewGroup): VH

    abstract fun onBindViewHolder(holder: VH, position: Int, item: I)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return onCreateItemViewHolder(parent)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val childItem = itemList[position]

        holder.itemView.clicks()
            .map { childItem }
            .subscribe(itemClickedSubject)

        onBindViewHolder(holder as VH, position, childItem)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        itemClickedSubject.onComplete()
    }

    protected fun inflateView(
        @LayoutRes layoutResource: Int,
        rootView: ViewGroup,
        attachToRoot: Boolean = false
    ): View {
        val inflater = LayoutInflater.from(rootView.context)
        return inflater.inflate(layoutResource, rootView, attachToRoot)
    }

    open fun onItemSelected(): Observable<I> {
        return itemClickedSubject.map { it }
    }

    open fun setItems(newItems: List<I>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    open fun addItems(newItems: List<I>) {
        val oldItems = itemList
        if (oldItems.isEmpty()) {
            itemList.addAll(newItems)
            notifyDataSetChanged()
        } else {
            itemList.clear()
            itemList.addAll(newItems)

            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldItems.size

                override fun getNewListSize(): Int = newItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItems.getOrNull(oldItemPosition)
                    val newItem = newItems[newItemPosition]

                    if (oldItem == null) return false

                    if ((oldItem is ProductResult && newItem is ProductResult) && oldItem.id == newItem.id) {
                        return true
                    }

                    return false
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItems.getOrNull(oldItemPosition)
                    val newItem = newItems[newItemPosition]

                    return (oldItem is ProductResult && newItem is ProductResult) && oldItem.id == newItem.id
                }
            }, false).dispatchUpdatesTo(this)
        }
    }

    open fun clearAll() {
        itemList.clear()
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean = itemList.isEmpty()
}
