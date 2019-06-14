package br.com.productcatalog.screens.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.productcatalog.R
import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.library.extension.color
import br.com.productcatalog.library.extension.toMoney
import br.com.productcatalog.library.recyclerview.SimpleAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.search_item.view.description
import kotlinx.android.synthetic.main.search_item.view.image
import kotlinx.android.synthetic.main.search_item.view.installments
import kotlinx.android.synthetic.main.search_item.view.price
import kotlinx.android.synthetic.main.search_item.view.rate
import kotlinx.android.synthetic.main.search_item.view.shipping

class SearchAdapter(
    private val context: Context,
    products: MutableList<ProductResult> = mutableListOf()
) : SimpleAdapter<ProductResult, SearchAdapter.ViewHolder>(products) {

    override fun onCreateItemViewHolder(parent: ViewGroup): ViewHolder {
        val searchItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return ViewHolder(searchItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: ProductResult) {
        with(holder) {
            description.text = item.title
            price.text = item.price.toMoney(item.currency)

            setupInstallments(this, item)

            shipping.isVisible = item.shipping != null && item.shipping.freeShipping

            if (!item.thumbnail.isBlank()) {
                Glide.with(context)
                    .load(item.thumbnail)
                    .into(image)
            }
        }
    }

    private fun setupInstallments(holder: ViewHolder, item: ProductResult) {
        if (item.installments != null) {
            with(holder.installments) {
                val installments = "%s %s"
                text = installments.format(
                    "${item.installments.quantity}x",
                    item.installments.amount.toMoney(item.currency)
                )
                isVisible = true
            }

            if (item.installments.rate <= 0) {
                holder.rate.isVisible = true
                holder.installments.setTextColor(context.color(R.color.green))
            }
        }
    }

    inner class ViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {
        val image: ImageView = itemVIew.image
        val price: TextView = itemVIew.price
        val description: TextView = itemVIew.description
        val installments: TextView = itemVIew.installments
        val rate: TextView = itemVIew.rate
        val shipping: TextView = itemVIew.shipping
    }
}