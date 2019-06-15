package br.com.productcatalog.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDetail(
    val id: String = "",
    val title: String = "",
    @SerialName("subtitle") val subTitle: String? = null,
    @SerialName("seller_id") val sellerId: Long = 0,
    val price: Double = 0.0,
    @SerialName("base_price") val basePrice: Double = 0.0,
    @SerialName("currency_id") val currencyId: String = "",
    @SerialName("initial_quantity") val initialQuantity: Int = 0,
    @SerialName("available_quantity") val availableQuantity: Int = 0,
    @SerialName("sold_quantity") val soldQuantity: Int = 0,
    val condition: String = "",
    val permalink: String = "",
    val pictures: List<Picture> = emptyList(),
    val shipping: Shipping? = null,
    @SerialName("attributes") val characteristics: List<Characteristic>? = null,
    var description: ProductDescription? = null,
    var installments: Installments? = null
)

@Serializable
data class Picture(
    val id: String = "",
    val url: String = ""
)

@Serializable
data class Characteristic(
    val id: String = "",
    val name: String = "",
    @SerialName("value_name") val valueName: String? = null
)

@Serializable
data class ProductDescription(
    val text: String = "",
    @SerialName("plain_text") val plainText: String = ""
)

@Serializable
data class Installments(
    val quantity: Int = 0,
    val amount: Double = 0.0,
    val rate: Double = 0.0,
    @SerialName("currency_id") val currency: String = ""
)

@Serializable
data class Shipping(
    @SerialName("free_shipping") val freeShipping: Boolean = false
)
