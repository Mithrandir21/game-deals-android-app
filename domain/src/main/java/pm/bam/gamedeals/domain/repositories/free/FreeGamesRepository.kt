package pm.bam.gamedeals.domain.repositories.free

import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.FreeGame
import pm.bam.gamedeals.domain.models.FreeGameSearchParameters

interface FreeGamesRepository {

    fun observeFreeGames(): Flow<List<FreeGame>>

    fun observeFreeGames(freeGameSearchParameters: FreeGameSearchParameters): Flow<List<FreeGame>>

    suspend fun refreshFreeGames()

}