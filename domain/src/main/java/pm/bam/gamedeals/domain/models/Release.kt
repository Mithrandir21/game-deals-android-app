package pm.bam.gamedeals.domain.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pm.bam.gamedeals.remote.cheapshark.models.RemoteRelease

@Immutable
@Entity(tableName = "Release")
@Serializable
data class Release(
    @PrimaryKey
    @SerialName("title")
    val title: String,
    @SerialName("date")
    val date: Int,
    @SerialName("image")
    val image: String,
)

internal fun RemoteRelease.toRelease(): Release =
    Release(
        title = title,
        date = date,
        image = image,
    )