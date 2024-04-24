package pm.bam.gamedeals.domain.models


import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pm.bam.gamedeals.domain.utils.IMAGE_BASE
import pm.bam.gamedeals.domain.utils.millisInHour
import pm.bam.gamedeals.remote.cheapshark.models.RemoteStore

@Immutable
@Entity(tableName = "Store")
@Serializable
data class Store(
    @PrimaryKey
    @SerialName("storeID")
    val storeID: Int,
    @SerialName("storeName")
    val storeName: String,
    @SerialName("isActive")
    val isActive: Boolean,
    @SerialName("images")
    val images: StoreImages,

    /**
     * An expiration date has been artificially to determine when
     * the Store should be considered as expired, set as now + (something time).
     *
     * @see millisInHour
     */
    @SerialName("expires")
    val expires: Long = System.currentTimeMillis().plus(millisInHour * 8)
) {
    @Immutable
    @Serializable
    data class StoreImages(
        @SerialName("banner")
        val banner: String,
        @SerialName("logo")
        val logo: String,
        @SerialName("icon")
        val icon: String
    )
}

internal fun RemoteStore.RemoteStoreImages.toStoreImages(): Store.StoreImages =
    Store.StoreImages(
        banner = IMAGE_BASE.plus(banner),
        logo = IMAGE_BASE.plus(logo),
        icon = IMAGE_BASE.plus(icon)
    )

internal fun RemoteStore.toStore(): Store =
    Store(
        storeID = storeID,
        storeName = storeName,
        isActive = isActive.toBooleanStrict(),
        images = images.toStoreImages()
    )

internal fun Int.toBooleanStrict() =
    when (this) {
        0 -> false
        1 -> true
        else -> throw Exception("Unknown value for int to boolean conversion: $this")
    }