package pm.bam.gamedeals.remote.api

import com.skydoves.sandwich.ApiResponse
import pm.bam.gamedeals.remote.models.RemoteStore
import retrofit2.http.GET
import retrofit2.http.Headers

internal interface StoresApi {

    @GET("/api/1.0/stores")
    @Headers("Accept: application/json")
    suspend fun getStores(): ApiResponse<List<RemoteStore>>

}