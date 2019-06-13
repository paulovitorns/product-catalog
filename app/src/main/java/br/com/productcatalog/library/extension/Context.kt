package br.com.productcatalog.library.extension

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.color(@ColorRes resourceId: Int): Int = ContextCompat.getColor(this, resourceId)