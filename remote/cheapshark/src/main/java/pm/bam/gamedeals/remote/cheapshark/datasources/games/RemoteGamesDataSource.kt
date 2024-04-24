package pm.bam.gamedeals.remote.cheapshark.datasources.games

import pm.bam.gamedeals.remote.cheapshark.models.RemoteGame
import pm.bam.gamedeals.remote.cheapshark.models.RemoteGameDetails

interface RemoteGamesDataSource {

    suspend fun searchGames(
        title: String,
        steamAppID: Int? = null,
        limit: Int? = null,
        pageNumber: Int? = null
    ): List<RemoteGame>

    suspend fun getGameDetails(id: String): RemoteGameDetails

}