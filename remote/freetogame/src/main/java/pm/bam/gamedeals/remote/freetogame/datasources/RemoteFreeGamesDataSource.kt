package pm.bam.gamedeals.remote.freetogame.datasources

import pm.bam.gamedeals.remote.freetogame.models.RemoteFreeGame

interface RemoteFreeGamesDataSource {

    suspend fun getAllFreeGames(): List<RemoteFreeGame>

}