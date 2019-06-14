package br.com.productcatalog.library.extension

import java.text.DecimalFormat
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
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()) as DecimalFormat
    if (currency.isNotEmpty()) formatter.currency = Currency.getInstance(currency)
    val symbol = formatter.currency.symbol

    formatter.isGroupingUsed = true
    formatter.positivePrefix = "$symbol "
    formatter.negativePrefix = "-$symbol "
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2

    return formatter.format(this)
}
