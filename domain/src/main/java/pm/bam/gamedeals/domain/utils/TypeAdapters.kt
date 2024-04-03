package pm.bam.gamedeals.domain.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import pm.bam.gamedeals.common.serializer.Serializer
import pm.bam.gamedeals.common.serializer.deserialize
import pm.bam.gamedeals.common.serializer.serialize
import pm.bam.gamedeals.domain.models.Store
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