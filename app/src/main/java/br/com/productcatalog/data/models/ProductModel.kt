package br.com.productcatalog.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Installments(
    val quantity: Int,
    val amount: Double,
    val rate: Double,
    @SerialName("currency_id") val currency: String
)

@Serializable
data class Shipping(
    @SerialName("free_shipping") val freeShipping: Boolean,
    val mode: String,
    val tags: List<String>,
    @SerialName("logistic_type") val logisticType: String,
    @SerialName("store_pick_up") val storePickup: Boolean
)
