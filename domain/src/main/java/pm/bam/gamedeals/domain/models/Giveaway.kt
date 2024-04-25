package pm.bam.gamedeals.domain.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.domain.utils.LocalDateSerializer
import pm.bam.gamedeals.remote.gamerpower.models.RemoteGiveaway
import pm.bam.gamedeals.remote.gamerpower.models.RemoteGiveawayType
import java.time.LocalDateTime

private const val WORTH_NOT_AVAILABLE = "N/A"

@Entity(tableName = "Giveaway")
@Serializable
data class Giveaway(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("worthDenominated")
    val worthDenominated: String?,
    @SerialName("worth")
    val worth: Double?,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("image")
    val image: String,
    @SerialName("description")
    val description: String,
    @SerialName("instructions")
    val instructions: String,
    @SerialName("open_giveaway_url")
    val openGiveawayUrl: String,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("publishedDate")
    val publishedDate: LocalDateTime,
    @SerialName("type")
    val type: GiveawayType,
    @SerialName("platforms")
    val platforms: List<GiveawayPlatform>,
    @SerialName("end_date")
    val endDate: String?,
    @SerialName("users")
    val users: Int,
    @SerialName("status")
    val status: String,
    @SerialName("gamerpower_url")
    val gamerpowerUrl: String,
    @SerialName("open_giveaway")
    val openGiveaway: String,
)

enum class GiveawayType {
    @SerialName("Game")
    GAME,

    @SerialName("DLC")
    DLC,

    @SerialName("Early_Access")
    BETA,

    @SerialName("Other")
    OTHER,
}

enum class GiveawayPlatform(val platformValue: String) {
    @SerialName("PC")
    PC("PC"),

    @SerialName("PS4")
    PS4("Playstation 4"),

    @SerialName("PS5")
    PS5("Playstation 5"),

    @SerialName("XBOX_360")
    XBOX_360("Xbox 360"),

    @SerialName("XBOX_ONE")
    XBOX_ONE("Xbox One"),

    @SerialName("XBOX_SERIES_X")
    XBOX_X("Xbox Series X|S"),

    @SerialName("NINTENDO_SWITCH")
    NINTENDO_SWITCH("Nintendo Switch"),

    @SerialName("ANDROID")
    ANDROID("Android"),

    @SerialName("IOS")
    IOS("iOS"),

    @SerialName("Steam")
    STEAM("Steam"),

    @SerialName("Itch.io")
    ITCH_IO("Itch.io"),

    @SerialName("Epic")
    EPIC("Epic Games Store"),

    @SerialName("GOG")
    GOG("GOG"),

    @SerialName("DRM_Free")
    DRM_FREE("DRM-Free"),

    @SerialName("OTHER")
    OTHER("Other"),
}

enum class GiveawaySortBy {
    @SerialName("date")
    DATE,

    @SerialName("value")
    VALUE,

    @SerialName("popularity")
    POPULARITY
}


@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GiveawaySearchParameters(
    val platforms: List<Pair<GiveawayPlatform, Boolean>> = GiveawayPlatform.entries.map { it to false },
    val types: List<Pair<GiveawayType, Boolean>> = GiveawayType.entries.map { it to false },
    val sortBy: GiveawaySortBy = GiveawaySortBy.DATE,
) {
    /**
     * Encodes properties from the this [GiveawaySearchParameters] to a map.
     * `null` values are omitted from the output.
     *
     * @see GiveawaySearchParameters.from
     */
    fun asMap() = Properties.encodeToMap(serializer(), this)

    companion object {
        /**
         * Decodes properties from the given [map] to a value of type [GiveawaySearchParameters].
         * [GiveawaySearchParameters] may contain properties of nullable types; they will be filled by non-null values from the [map], if present.
         */
        fun from(map: Map<String, Any?>): GiveawaySearchParameters = Properties.decodeFromMap(
            serializer(),
            // Removes any map Key/Value pairs where the Value is NULL.
            map.mapNotNull { (key, value) -> value?.let { key to it } }
                .toMap())
    }
}


internal fun RemoteGiveaway.toGiveaway(
    datetimeParsing: DatetimeParsing
): Giveaway =
    Giveaway(
        id = id,
        title = title,
        worthDenominated = worth.takeUnless { it == WORTH_NOT_AVAILABLE },
        worth = worth.takeUnless { it == WORTH_NOT_AVAILABLE }?.replace("$", "")?.toDoubleOrNull(),
        thumbnail = thumbnail,
        image = image,
        description = description,
        instructions = instructions,
        openGiveawayUrl = openGiveawayUrl,
        publishedDate = datetimeParsing.parseDatetime(publishedDate),
        type = type.toGiveawayType(),
        platforms = platforms.toGiveawayPlatform(),
        endDate = endDate,
        users = users,
        status = status,
        gamerpowerUrl = gamerpowerUrl,
        openGiveaway = openGiveaway,
    )

internal fun RemoteGiveawayType.toGiveawayType(): GiveawayType =
    when (this) {
        RemoteGiveawayType.GAME -> GiveawayType.GAME
        RemoteGiveawayType.DLC -> GiveawayType.DLC
        RemoteGiveawayType.BETA -> GiveawayType.BETA
        RemoteGiveawayType.OTHER -> GiveawayType.OTHER
    }

private fun String.toGiveawayPlatform(): List<GiveawayPlatform> =
    this.split(", ")
        .map {
            when (it) {
                GiveawayPlatform.PC.platformValue -> GiveawayPlatform.PC
                GiveawayPlatform.PS4.platformValue -> GiveawayPlatform.PS4
                GiveawayPlatform.PS5.platformValue -> GiveawayPlatform.PS5
                GiveawayPlatform.XBOX_360.platformValue -> GiveawayPlatform.XBOX_360
                GiveawayPlatform.XBOX_ONE.platformValue -> GiveawayPlatform.XBOX_ONE
                GiveawayPlatform.XBOX_X.platformValue -> GiveawayPlatform.XBOX_X
                GiveawayPlatform.NINTENDO_SWITCH.platformValue -> GiveawayPlatform.NINTENDO_SWITCH
                GiveawayPlatform.ANDROID.platformValue -> GiveawayPlatform.ANDROID
                GiveawayPlatform.IOS.platformValue -> GiveawayPlatform.IOS
                GiveawayPlatform.STEAM.platformValue -> GiveawayPlatform.STEAM
                GiveawayPlatform.ITCH_IO.platformValue -> GiveawayPlatform.ITCH_IO
                GiveawayPlatform.EPIC.platformValue -> GiveawayPlatform.EPIC
                GiveawayPlatform.GOG.platformValue -> GiveawayPlatform.GOG
                GiveawayPlatform.DRM_FREE.platformValue -> GiveawayPlatform.DRM_FREE
                else -> GiveawayPlatform.OTHER
            }
        }