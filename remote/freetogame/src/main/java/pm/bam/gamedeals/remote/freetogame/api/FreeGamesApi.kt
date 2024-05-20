package pm.bam.gamedeals.remote.freetogame.api

import com.skydoves.sandwich.ApiResponse
import pm.bam.gamedeals.remote.freetogame.models.RemoteFreeGame
import retrofit2.http.GET

internal interface FreeGamesApi {

    @GET("/api/games")
    suspend fun getAllFreeGames(): ApiResponse<List<RemoteFreeGame>>
}