package pm.bam.gamedeals.domain.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import pm.bam.gamedeals.common.serializer.Serializer
import pm.bam.gamedeals.common.serializer.deserialize
import pm.bam.gamedeals.common.serializer.serialize
import pm.bam.gamedeals.domain.models.GiveawayPlatform
import pm.bam.gamedeals.domain.models.Store
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@ProvidedTypeConverter
internal class StoreImagesConverter @Inject constructor(
    private val serializer: Serializer
) {
    @TypeConverter
    fun convertToJsonString(priceDetails: Store.StoreImages): String = serializer.serialize(priceDetails)

    @TypeConverter
    fun convertToObject(json: String): Store.StoreImages = serializer.deserialize(json)
}

@ProvidedTypeConverter
internal class LocalDatetimeConverter @Inject constructor() {
    @TypeConverter
    fun convertToJsonString(localDateTime: LocalDateTime): Long = localDateTime.toEpochSecond(ZoneOffset.UTC)

    @TypeConverter
    fun convertToObject(json: Long): LocalDateTime = LocalDateTime.ofEpochSecond(json, 0, ZoneOffset.UTC)
}

@ProvidedTypeConverter
internal class GiveawayPlatformsConverter @Inject constructor() {
    @TypeConverter
    fun convertToJsonString(platforms: List<GiveawayPlatform>): String = platforms.joinToString(separator = ", ")

    @TypeConverter
    fun convertToObject(json: String): List<GiveawayPlatform> = json.split(", ").map { GiveawayPlatform.valueOf(it) }
}