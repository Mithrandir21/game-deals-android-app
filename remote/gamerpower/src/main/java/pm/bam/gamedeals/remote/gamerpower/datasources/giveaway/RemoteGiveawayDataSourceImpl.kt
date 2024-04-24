package pm.bam.gamedeals.remote.gamerpower.datasources.giveaway

import com.skydoves.sandwich.getOrThrow
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.gamerpower.api.GamesApi
import pm.bam.gamedeals.remote.gamerpower.models.RemoteGiveaway
import pm.bam.gamedeals.remote.logic.log
import pm.bam.gamedeals.remote.logic.mapAnyFailure
import javax.inject.Inject

internal class RemoteGiveawayDataSourceImpl @Inject constructor(
    private val logger: Logger,
    private val gamesApi: GamesApi,
    private val remoteExceptionTransformer: RemoteExceptionTransformer
) : RemoteGiveawayDataSource {

    override suspend fun getGiveaways(): List<RemoteGiveaway> =
        gamesApi.getAllGames()
            .log(logger, tag = RemoteGiveawayDataSourceImpl::class.simpleName)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()

}