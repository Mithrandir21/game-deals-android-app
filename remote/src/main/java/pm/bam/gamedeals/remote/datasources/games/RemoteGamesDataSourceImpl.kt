package pm.bam.gamedeals.remote.datasources.games

import com.skydoves.sandwich.getOrThrow
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.api.GamesApi
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.logic.log
import pm.bam.gamedeals.remote.logic.mapAnyFailure
import pm.bam.gamedeals.remote.models.RemoteGame
import pm.bam.gamedeals.remote.models.RemoteGameDetails
import javax.inject.Inject

internal class RemoteGamesDataSourceImpl @Inject constructor(
    private val logger: Logger,
    private val gamesApi: GamesApi,
    private val remoteExceptionTransformer: RemoteExceptionTransformer
) : RemoteGamesDataSource {

    override suspend fun searchGames(title: String, steamAppID: Int?, limit: Int?, pageNumber: Int?): List<RemoteGame> =
        gamesApi.getGames(title, steamAppID, limit, pageNumber)
            .log(logger, tag = RemoteGamesDataSourceImpl::class.simpleName)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()

    override suspend fun getGameDetails(id: String): RemoteGameDetails =
        gamesApi.getGame(id)
            .log(logger, tag = RemoteGamesDataSourceImpl::class.simpleName)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()
}