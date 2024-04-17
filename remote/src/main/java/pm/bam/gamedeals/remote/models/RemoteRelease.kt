package pm.bam.gamedeals.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//class RemoteReleases : ArrayList<RemoteRelease>()

@Serializable
data class RemoteRelease(
    @SerialName("date")
    val date: Int,
    @SerialName("title")
    val title: String,
    @SerialName("image")
    val image: String
)