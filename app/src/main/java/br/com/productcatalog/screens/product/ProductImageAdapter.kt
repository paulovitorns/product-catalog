package br.com.productcatalog.screens.product

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import br.com.productcatalog.data.models.Picture
import com.bumptech.glide.Glide

class ProductImageAdapter(
    private val context: Context,
    private val pictures: List<Picture>
) : PagerAdapter() {

    override fun getCount(): Int = pictures.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        val picture = pictures[position]

        Glide.with(context)
            .load(picture.url)
            .centerCrop()
            .into(imageView)

        container.addView(imageView, 0)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}
