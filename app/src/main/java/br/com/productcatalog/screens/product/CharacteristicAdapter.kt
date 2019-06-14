package br.com.productcatalog.screens.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.productcatalog.R
import br.com.productcatalog.data.models.Characteristic
import br.com.productcatalog.library.recyclerview.SimpleAdapter
import kotlinx.android.synthetic.main.characteristic_item.view.item
import kotlinx.android.synthetic.main.characteristic_item.view.itemDescription

class CharacteristicAdapter(
    characteristics: MutableList<Characteristic> = mutableListOf()
) : SimpleAdapter<Characteristic, CharacteristicAdapter.ViewHolder>(characteristics) {

    override fun onCreateItemViewHolder(parent: ViewGroup): ViewHolder {
        val searchItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.characteristic_item, parent, false)
        return ViewHolder(searchItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: Characteristic) {
        with(holder) {
            textItem.text = item.name
            itemDescription.text = item.valueName ?: ""
        }
    }

    inner class ViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {
        val textItem: TextView = itemVIew.item
        val itemDescription: TextView = itemVIew.itemDescription
    }
}
