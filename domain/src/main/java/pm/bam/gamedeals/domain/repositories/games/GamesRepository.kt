package pm.bam.gamedeals.domain.repositories.games

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.SearchParameters
import pm.bam.gamedeals.domain.models.Game
import pm.bam.gamedeals.domain.models.GameDetails

interface GamesRepository {

    fun observeGames(): Flow<List<Game>>

    suspend fun searchGames(query: String): List<Game>

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun searchGames(searchParameters: SearchParameters): List<Deal>

    suspend fun getGameDetails(dealId: Int): GameDetails

    suspend fun refreshGames()

}