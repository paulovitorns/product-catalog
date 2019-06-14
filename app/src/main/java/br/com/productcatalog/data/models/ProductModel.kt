package br.com.productcatalog.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDetail(
    val id: String,
    val title: String,
    @SerialName("subtitle") val subTitle: String?,
    @SerialName("seller_id") val sellerId: Long,
    val price: Double,
    @SerialName("base_price") val basePrice: Double,
    @SerialName("currency_id") val currencyId: String,
    @SerialName("initial_quantity") val initialQuantity: Int,
    @SerialName("available_quantity") val availableQuantity: Int,
    @SerialName("sold_quantity") val soldQuantity: Int,
    val condition: String,
    val permalink: String,
    val pictures: List<Picture>,
    val shipping: Shipping?,
    @SerialName("attributes") val characteristics: List<Characteristic>?,
    var description: ProductDescription? = null,
    var installments: Installments? = null
)

@Serializable
data class Picture(
    val id: String,
    val url: String
)

@Serializable
data class Characteristic(
    val id: String,
    val name: String,
    @SerialName("value_name") val valueName: String?
)

@Serializable
data class ProductDescription(
    val text: String = "",
    @SerialName("plain_text") val plainText: String = ""
)

@Serializable
data class Installments(
    val quantity: Int,
    val amount: Double,
    val rate: Double,
    @SerialName("currency_id") val currency: String
)

@Serializable
data class Shipping(
    val mode: String,
    val tags: List<String>,
    @SerialName("free_shipping") val freeShipping: Boolean,
    @SerialName("logistic_type") val logisticType: String,
    @SerialName("store_pick_up") val storePickup: Boolean
)
