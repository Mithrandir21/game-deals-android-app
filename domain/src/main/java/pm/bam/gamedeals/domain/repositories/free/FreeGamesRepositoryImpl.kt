package pm.bam.gamedeals.domain.repositories.free

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.common.onError
import pm.bam.gamedeals.domain.db.dao.FreeGamesDao
import pm.bam.gamedeals.domain.models.FreeGame
import pm.bam.gamedeals.domain.models.FreeGameSearchParameters
import pm.bam.gamedeals.domain.models.FreeGameSortBy
import pm.bam.gamedeals.domain.models.toFreeGame
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.logging.fatal
import pm.bam.gamedeals.remote.freetogame.datasources.RemoteFreeGamesDataSource
import javax.inject.Inject

internal class FreeGamesRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val freeGamesDao: FreeGamesDao,
    private val remoteFreeGamesDataSource: RemoteFreeGamesDataSource,
    private val datetimeParsing: DatetimeParsing
) : FreeGamesRepository {

    override fun observeFreeGames(): Flow<List<FreeGame>> =
        freeGamesDao.observeAllFreeGames()
            .map { items -> items.sortedByDescending { it.releaseDate } }
            .onError { fatal(logger, it) }

    override fun observeFreeGames(freeGameSearchParameters: FreeGameSearchParameters): Flow<List<FreeGame>> {
        val typeValues = freeGameSearchParameters.genres
            .filter { it.second }
            .map { it.first }

        val requestedPlatforms = freeGameSearchParameters.platforms
            .filter { it.second }
            .map { it.first }

        return freeGamesDao.observeAllFreeGames()
            .map { items ->
                if (requestedPlatforms.isEmpty()) return@map items
                else items.filter { requestedPlatforms.contains(it.platform) }
            }
            .map { items ->
                if (typeValues.isEmpty()) return@map items
                else items.filter { typeValues.contains(it.genre) }
            }
            .map { items ->
                when (freeGameSearchParameters.sortBy) {
                    FreeGameSortBy.RELEASE_DATE -> items.sortedByDescending { it.releaseDate }
                    FreeGameSortBy.ALPHABETICAL -> items.sortedByDescending { it.title }
                }
            }
            .onError { fatal(logger, it) }
    }

    override suspend fun refreshFreeGames() =
        remoteFreeGamesDataSource.getAllFreeGames()
            .mapNotNull { remoteFreeGame ->
                try {
                    remoteFreeGame.toFreeGame(datetimeParsing)
                } catch (e: Exception) {
                    logger.fatalThrowable(e)
                    null
                }
            }
            .let { freeGamesDao.addFreeGames(*it.toTypedArray()) }
}