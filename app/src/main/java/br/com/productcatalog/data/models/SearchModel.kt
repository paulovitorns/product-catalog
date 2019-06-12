package br.com.productcatalog.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    @SerialName("site_id") val siteId: String,
    val query: String,
    val paging: SearchPaging,
    val results: List<ProductResult>
)

@Serializable
data class SearchPaging(
    val total: Int,
    val offset: Int,
    val limit: Int,
    @SerialName("primary_results") val primaryResult: Int
)

@Serializable
data class ProductResult(
    val id: String,
    val title: String,
    val price: Double,
    @SerialName("currency_id") val currency: String,
    val thumbnail: String,
    val installments: Installments?,
    val shipping: Shipping?
)
