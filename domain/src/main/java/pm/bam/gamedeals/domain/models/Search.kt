package pm.bam.gamedeals.domain.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import pm.bam.gamedeals.remote.cheapshark.api.models.deals.RemoteDealsQuery
import pm.bam.gamedeals.remote.cheapshark.api.models.deals.RemoteDealsSortBy

@ExperimentalSerializationApi
@Serializable
data class SearchParameters(
    val storeID: Int? = null,
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val sortBy: DealsSortBy? = DealsSortBy.DEALRATING,
    val desc: Int? = null,
    val lowerPrice: Int? = null,
    val upperPrice: Int? = null,
    val metacritic: Int? = null,
    val steamMinRating: Int? = null,
    val maxAge: Int? = null,
    val steamAppID: Int? = null,
    val title: String? = null,
    val exact: Boolean? = null,
    val aaa: Boolean? = null,
    val steamworks: Boolean? = null,
    val onSale: Boolean? = null
) {

    /**
     * Encodes properties from the this [SearchParameters] to a map.
     * `null` values are omitted from the output.
     *
     * @see SearchParameters.from
     */
    fun asMap() = Properties.encodeToMap(serializer(), this)

    companion object {
        /**
         * Decodes properties from the given [map] to a value of type [SearchParameters].
         * [SearchParameters] may contain properties of nullable types; they will be filled by non-null values from the [map], if present.
         */
        fun from(map: Map<String, Any?>): SearchParameters = Properties.decodeFromMap(serializer(),
            // Removes any map Key/Value pairs where the Value is NULL.
            map.mapNotNull { (key, value) -> value?.let { key to it } }
                .toMap())
    }

    /**
     * Returning `false` to avoid the default implementation of `equals` when attempting to emit a new value in a `StateFlow`.
     * See "Strong equality-based conflation" in the StateFlow documentation.
     */
    override fun equals(other: Any?): Boolean = false
}

enum class DealsSortBy {

    @SerialName("DealRating")
    DEALRATING,

    @SerialName("Title")
    TITLE,

    @SerialName("Savings")
    SAVINGS,

    @SerialName("Price")
    PRICE,

    @SerialName("Metacritic")
    METACRITIC,

    @SerialName("Reviews")
    REVIEWS,

    @SerialName("Release")
    RELEASE,

    @SerialName("Store")
    STORE,

    @SerialName("Recent")
    RECENT

}

@OptIn(ExperimentalSerializationApi::class)
internal fun SearchParameters.toRemoteDealsQuery(): RemoteDealsQuery = RemoteDealsQuery(
    storeID = storeID,
    pageNumber = pageNumber,
    pageSize = pageSize,
    sortBy = sortBy?.toRemoteDealsSortBy(),
    desc = desc,
    lowerPrice = lowerPrice,
    upperPrice = upperPrice,
    metacritic = metacritic,
    steamRating = steamMinRating,
    maxAge = maxAge,
    steamAppID = steamAppID,
    title = title,
    exact = exact?.toInt(),
    aaa = aaa?.toInt(),
    steamworks = steamworks?.toInt(),
    onSale = onSale?.toInt()
)

internal fun DealsSortBy.toRemoteDealsSortBy(): RemoteDealsSortBy = when (this) {
    DealsSortBy.DEALRATING -> RemoteDealsSortBy.DEALRATING
    DealsSortBy.TITLE -> RemoteDealsSortBy.TITLE
    DealsSortBy.SAVINGS -> RemoteDealsSortBy.SAVINGS
    DealsSortBy.PRICE -> RemoteDealsSortBy.PRICE
    DealsSortBy.METACRITIC -> RemoteDealsSortBy.METACRITIC
    DealsSortBy.REVIEWS -> RemoteDealsSortBy.REVIEWS
    DealsSortBy.RELEASE -> RemoteDealsSortBy.RELEASE
    DealsSortBy.STORE -> RemoteDealsSortBy.STORE
    DealsSortBy.RECENT -> RemoteDealsSortBy.RECENT
}

private fun Boolean.toInt() = if(this) 1 else 0