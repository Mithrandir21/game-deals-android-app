package pm.bam.gamedeals.remote.freetogame.datasources

import com.skydoves.sandwich.getOrThrow
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.freetogame.api.FreeGamesApi
import pm.bam.gamedeals.remote.freetogame.models.RemoteFreeGame
import pm.bam.gamedeals.remote.logic.log
import pm.bam.gamedeals.remote.logic.mapAnyFailure
import javax.inject.Inject

internal class RemoteFreeGamesDataSourceImpl @Inject constructor(
    private val logger: Logger,
    private val freeGamesApi: FreeGamesApi,
    private val remoteExceptionTransformer: RemoteExceptionTransformer
) : RemoteFreeGamesDataSource {

    override suspend fun getAllFreeGames(): List<RemoteFreeGame> =
        freeGamesApi.getAllFreeGames()
            .log(logger, tag = RemoteFreeGamesDataSourceImpl::class.simpleName)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()
}