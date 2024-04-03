package pm.bam.gamedeals.domain.models


import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pm.bam.gamedeals.common.datetime.formatting.DateTimeFormatter
import pm.bam.gamedeals.domain.transformations.CurrencyTransformation
import pm.bam.gamedeals.domain.utils.millisInHour
import pm.bam.gamedeals.remote.models.RemoteDeal
import pm.bam.gamedeals.remote.models.RemoteDealDetails

@OptIn(ExperimentalSerializationApi::class)
@Immutable
@Entity(tableName = "Deal")
@Serializable
data class Deal(
    @PrimaryKey
    @SerialName("dealID")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val dealID: String,
    @SerialName("internalName")
    val internalName: String,
    @SerialName("title")
    val title: String,
    @SerialName("metacriticLink")
    val metacriticLink: String? = null,
    @SerialName("storeID")
    val storeID: Int,
    @SerialName("gameID")
    val gameID: Int,
    @SerialName("salePriceValue")
    val salePriceValue: Double,
    @SerialName("salePriceDenominated")
    val salePriceDenominated: String,
    @SerialName("normalPriceValue")
    val normalPriceValue: Double,
    @SerialName("normalPriceDenominated")
    val normalPriceDenominated: String,
    @SerialName("isOnSale")
    val isOnSale: Boolean,
    @SerialName("savings")
    val savings: Double,
    @SerialName("metacriticScore")
    val metacriticScore: Int,
    @SerialName("steamRatingText")
    val steamRatingText: String? = null,
    @SerialName("steamRatingPercent")
    val steamRatingPercent: Int,
    @SerialName("steamRatingCount")
    val steamRatingCount: String,
    @SerialName("steamAppID")
    val steamAppID: Int? = null,
    @SerialName("releaseDate")
    val releaseDate: Int,
    @SerialName("lastChange")
    val lastChange: Int,
    @SerialName("dealRating")
    val dealRating: Double,
    @SerialName("thumb")
    val thumb: String,

    /**
     * An expiration date has been artificially to determine when
     * the Store should be considered as expired, set as now + (something time).
     *
     * @see millisInHour
     */
    @SerialName("expires")
    val expires: Long = System.currentTimeMillis().plus(millisInHour * 8)
)

@Serializable
data class DealDetails(
    @SerialName("gameInfo")
    val gameInfo: GameInfo,
    @SerialName("cheaperStores")
    val cheaperStores: List<CheaperStore>,
    @SerialName("cheapestPrice")
    val cheapestPrice: CheapestPrice? = null
) {

    @Serializable
    data class GameInfo(
        @SerialName("storeID")
        val storeID: Int,
        @SerialName("gameID")
        val gameID: Int,
        @SerialName("name")
        val name: String,
        @SerialName("steamAppID")
        val steamAppID: Int? = null,
        @SerialName("salePriceValue")
        val salePriceValue: Double,
        @SerialName("salePriceDenominated")
        val salePriceDenominated: String,
        @SerialName("retailPriceValue")
        val retailPriceValue: Double,
        @SerialName("retailPriceDenominated")
        val retailPriceDenominated: String,
        @SerialName("steamRatingText")
        val steamRatingText: String? = null,
        @SerialName("steamRatingPercent")
        val steamRatingPercent: Int? = null,
        @SerialName("steamRatingCount")
        val steamRatingCount: String,
        @SerialName("metacriticScore")
        val metacriticScore: Int? = null,
        @SerialName("metacriticLink")
        val metacriticLink: String? = null,
        @SerialName("releaseDate")
        val releaseDate: String? = null,
        @SerialName("publisher")
        val publisher: String,
        @SerialName("steamworks")
        val steamworks: Boolean? = null,
        @SerialName("thumb")
        val thumb: String
    )

    @Serializable
    data class CheaperStore(
        @SerialName("dealID")
        val dealID: String,
        @SerialName("storeID")
        val storeID: Int,
        @SerialName("salePriceValue")
        val salePriceValue: Double,
        @SerialName("salePriceDenominated")
        val salePriceDenominated: String,
        @SerialName("retailPrice")
        val retailPriceValue: Double,
        @SerialName("retailPriceDenominated")
        val retailPriceDenominated: String
    )

    @Serializable
    data class CheapestPrice(
        @SerialName("priceValue")
        val priceValue: Double,
        @SerialName("priceDenominated")
        val priceDenominated: String,
        @SerialName("date")
        val date: String
    )
}

@Entity(tableName = "DealPage")
internal data class DealPage(
    @PrimaryKey
    @ColumnInfo(name = "storeID", collate = ColumnInfo.NOCASE)
    val storeID: Int,
    @SerialName("page")
    val page: Int
)

internal fun RemoteDeal.toDeal(currencyTransformation: CurrencyTransformation): Deal =
    Deal(
        dealID = dealID,
        internalName = internalName,
        title = title,
        metacriticLink = metacriticLink,
        storeID = storeID,
        gameID = gameID,
        salePriceValue = salePrice,
        salePriceDenominated = currencyTransformation.valueToDenominated(salePrice),
        normalPriceValue = normalPrice,
        normalPriceDenominated = currencyTransformation.valueToDenominated(normalPrice),
        isOnSale = isOnSale.toBooleanStrict(),
        savings = savings,
        metacriticScore = metacriticScore,
        steamRatingText = steamRatingText,
        steamRatingPercent = steamRatingPercent,
        steamRatingCount = steamRatingCount,
        steamAppID = steamAppID,
        releaseDate = releaseDate,
        lastChange = lastChange,
        dealRating = dealRating,
        thumb = thumb,
    )

internal fun RemoteDealDetails.RemoteCheapestPrice.toCheapestPrice(
    currencyTransformation: CurrencyTransformation,
    datetimeFormatter: DateTimeFormatter
): DealDetails.CheapestPrice? =
    price?.let {
        DealDetails.CheapestPrice(
            priceValue = it,
            priceDenominated = currencyTransformation.valueToDenominated(it),
            date = datetimeFormatter.formatToISODate(date)
        )
    }

internal fun RemoteDealDetails.RemoteCheaperStore.toCheaperStore(
    currencyTransformation: CurrencyTransformation
): DealDetails.CheaperStore =
    DealDetails.CheaperStore(
        dealID = dealID,
        storeID = storeID,
        salePriceValue = salePrice,
        salePriceDenominated = currencyTransformation.valueToDenominated(salePrice),
        retailPriceValue = retailPrice,
        retailPriceDenominated = currencyTransformation.valueToDenominated(retailPrice),
    )

internal fun RemoteDealDetails.RemoteGameInfo.toGameInfo(
    currencyTransformation: CurrencyTransformation,
    datetimeFormatter: DateTimeFormatter
): DealDetails.GameInfo =
    DealDetails.GameInfo(
        storeID = storeID,
        gameID = gameID,
        name = name,
        steamAppID = steamAppID,
        salePriceValue = salePrice,
        salePriceDenominated = currencyTransformation.valueToDenominated(salePrice),
        retailPriceValue = retailPrice,
        retailPriceDenominated = currencyTransformation.valueToDenominated(retailPrice),
        steamRatingText = steamRatingText,
        steamRatingPercent = steamRatingPercent.takeIf { it > 0 },
        steamRatingCount = steamRatingCount,
        metacriticScore = metacriticScore.takeIf { it > 0 },
        metacriticLink = metacriticLink,
        releaseDate = datetimeFormatter.formatToISODateNullable(releaseDate),
        publisher = publisher,
        steamworks = steamworks?.toBooleanStrict(),
        thumb = thumb
    )

internal fun RemoteDealDetails.toDealDetails(
    currencyTransformation: CurrencyTransformation,
    datetimeFormatter: DateTimeFormatter
): DealDetails =
    DealDetails(
        gameInfo = gameInfo.toGameInfo(currencyTransformation, datetimeFormatter),
        cheaperStores = cheaperStores.map { it.toCheaperStore(currencyTransformation) },
        cheapestPrice = cheapestPrice.toCheapestPrice(currencyTransformation, datetimeFormatter)
    )