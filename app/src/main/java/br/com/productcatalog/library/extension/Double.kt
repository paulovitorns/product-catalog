package br.com.productcatalog.library.extension

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Function to format Double as money format.
 *
 * @param currency to format the value based in some currency.
 * If it's not passed this will use the phone's current locale.
 *
 * @return a money format
 */
fun Double.toMoney(currency: String = ""): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    if (currency.isNotEmpty()) formatter.currency = Currency.getInstance(currency)

    return formatter.format(this)
}
