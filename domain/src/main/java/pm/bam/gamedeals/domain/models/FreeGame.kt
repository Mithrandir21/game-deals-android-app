package pm.bam.gamedeals.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.domain.utils.LocalDateSerializer
import pm.bam.gamedeals.remote.freetogame.models.RemoteFreeGame
import java.time.LocalDateTime


@Entity(tableName = "FreeGame")
@Serializable
data class FreeGame(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("description")
    val description: String,
    @SerialName("game_url")
    val gameUrl: String,
    @SerialName("genre")
    val genre: String,
    @SerialName("platform")
    val platform: String,
    @SerialName("publisher")
    val publisher: String,
    @SerialName("developer")
    val developer: String,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("release_date")
    val releaseDate: LocalDateTime,
    @SerialName("freetogame_profile_url")
    val freeToGameProfileUrl: String
)


enum class FreeGameSortBy {
    @SerialName("date")
    RELEASE_DATE,

    @SerialName("alphabetical")
    ALPHABETICAL
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FreeGameSearchParameters(
    val platforms: List<Pair<String, Boolean>> = mutableListOf(),
    val genres: List<Pair<String, Boolean>> = mutableListOf(),
    val sortBy: FreeGameSortBy = FreeGameSortBy.RELEASE_DATE,
) {
    /**
     * Encodes properties from the this [FreeGameSearchParameters] to a map.
     * `null` values are omitted from the output.
     *
     * @see FreeGameSearchParameters.from
     */
    fun asMap() = Properties.encodeToMap(serializer(), this)

    companion object {
        /**
         * Decodes properties from the given [map] to a value of type [FreeGameSearchParameters].
         * [FreeGameSearchParameters] may contain properties of nullable types; they will be filled by non-null values from the [map], if present.
         */
        fun from(map: Map<String, Any?>): FreeGameSearchParameters = Properties.decodeFromMap(
            serializer(),
            // Removes any map Key/Value pairs where the Value is NULL.
            map.mapNotNull { (key, value) -> value?.let { key to it } }
                .toMap())
    }
}


internal fun RemoteFreeGame.toFreeGame(
    datetimeParsing: DatetimeParsing
): FreeGame =
    FreeGame(
        id = id,
        title = title,
        thumbnail = thumbnail,
        description = shortDescription,
        gameUrl = gameUrl,
        genre = genre,
        platform = platform,
        publisher = publisher,
        developer = developer,
        releaseDate = datetimeParsing.parseDate(releaseDate),
        freeToGameProfileUrl = freeToGameProfileUrl
    )