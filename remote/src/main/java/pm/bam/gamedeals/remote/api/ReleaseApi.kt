package pm.bam.gamedeals.remote.api

import com.skydoves.sandwich.ApiResponse
import pm.bam.gamedeals.remote.models.RemoteRelease
import retrofit2.http.GET
import retrofit2.http.Headers

internal interface ReleaseApi {

    @Suppress("kotlin:S107")
    @GET("/api/other/releases")
    @Headers("Accept: application/json")
    suspend fun getReleases(): ApiResponse<List<RemoteRelease>>

}