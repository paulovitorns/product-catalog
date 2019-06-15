package br.com.productcatalog.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    @SerialName("site_id") val siteId: String = "",
    val query: String = "",
    val paging: SearchPaging = SearchPaging(),
    var results: MutableList<ProductResult> = mutableListOf()
)

@Serializable
data class SearchPaging(
    val total: Int = 0,
    val offset: Int = 0,
    val limit: Int = 0,
    @SerialName("primary_results") val primaryResult: Int = 0
)

@Serializable
data class ProductResult(
    val id: String = "",
    val title: String = "",
    val price: Double = 0.0,
    @SerialName("currency_id") val currency: String = "",
    val thumbnail: String = "",
    val installments: Installments? = null,
    val shipping: Shipping? = null
)
