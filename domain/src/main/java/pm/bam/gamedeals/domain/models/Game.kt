package pm.bam.gamedeals.domain.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pm.bam.gamedeals.common.datetime.formatting.DateTimeFormatter
import pm.bam.gamedeals.domain.transformations.CurrencyTransformation
import pm.bam.gamedeals.remote.cheapshark.models.RemoteGame
import pm.bam.gamedeals.remote.cheapshark.models.RemoteGameDetails
import kotlin.math.roundToInt

@Entity(tableName = "Game")
@Serializable
data class Game(
    @PrimaryKey
    @SerialName("gameID")
    val gameID: Int,
    @SerialName("steamAppID")
    val steamAppID: Int? = null,
    @SerialName("cheapestValue")
    val cheapestValue: Double,
    @SerialName("cheapestDenominated")
    val cheapestDenominated: String,
    @SerialName("cheapestDealID")
    val cheapestDealID: String,
    @SerialName("title")
    val title: String,
    @SerialName("internalName")
    val internalName: String,
    @SerialName("thumb")
    val thumb: String
)

@Serializable
data class GameDetails(
    @SerialName("info")
    val info: GameInfo,
    @SerialName("cheapestPriceEver")
    val cheapestPriceEver: GameCheapestPriceEver,
    @SerialName("deals")
    val deals: List<GameDeal>
) {
    @Serializable
    data class GameInfo(
        @SerialName("title")
        val title: String,
        @SerialName("steamAppID")
        val steamAppID: Int? = null,
        @SerialName("thumb")
        val thumb: String
    )

    @Serializable
    data class GameCheapestPriceEver(
        @SerialName("priceValue")
        val priceValue: Double,
        @SerialName("priceDenominated")
        val priceDenominated: String,
        @SerialName("date")
        val date: String
    )

    @Serializable
    data class GameDeal(
        @SerialName("storeID")
        val storeID: Int,
        @SerialName("dealID")
        val dealID: String,
        @SerialName("priceValue")
        val priceValue: Double,
        @SerialName("priceDenominated")
        val priceDenominated: String,
        @SerialName("retailPriceValue")
        val retailPriceValue: Double,
        @SerialName("retailPriceDenominated")
        val retailPriceDenominated: String,
        @SerialName("savings")
        val savings: Int
    )
}

internal fun RemoteGame.toGame(
    currencyTransformation: CurrencyTransformation
): Game =
    Game(
        gameID = gameID,
        steamAppID = steamAppID,
        cheapestValue = cheapest,
        cheapestDenominated = currencyTransformation.valueToDenominated(cheapest),
        cheapestDealID = cheapestDealID,
        title = external,
        internalName = internalName,
        thumb = thumb,
    )

internal fun RemoteGameDetails.RemoteGameDeal.toGameDeal(
    currencyTransformation: CurrencyTransformation
): GameDetails.GameDeal =
    GameDetails.GameDeal(
        storeID = storeID,
        dealID = dealID,
        priceValue = price,
        priceDenominated = currencyTransformation.valueToDenominated(price),
        retailPriceValue = retailPrice,
        retailPriceDenominated = currencyTransformation.valueToDenominated(retailPrice),
        savings = savings.roundToInt(),
    )

internal fun RemoteGameDetails.RemoteGameCheapestPriceEver.toGameCheapestPriceEver(
    currencyTransformation: CurrencyTransformation,
    datetimeFormatter: DateTimeFormatter
): GameDetails.GameCheapestPriceEver =
    GameDetails.GameCheapestPriceEver(
        priceValue = price,
        priceDenominated = currencyTransformation.valueToDenominated(price),
        date = datetimeFormatter.formatToISODate(date)
    )

internal fun RemoteGameDetails.RemoteGameInfo.toGameInfo(): GameDetails.GameInfo =
    GameDetails.GameInfo(
        title = title,
        steamAppID = steamAppID,
        thumb = thumb,
    )

internal fun RemoteGameDetails.toGameDetails(
    currencyTransformation: CurrencyTransformation,
    datetimeFormatter: DateTimeFormatter
): GameDetails =
    GameDetails(
        info = info.toGameInfo(),
        cheapestPriceEver = cheapestPriceEver.toGameCheapestPriceEver(currencyTransformation, datetimeFormatter),
        deals = deals.map { it.toGameDeal(currencyTransformation) },
    )