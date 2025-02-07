package pm.bam.gamedeals.remote.cheapshark.datasources.stores

import com.skydoves.sandwich.getOrThrow
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.cheapshark.api.StoresApi
import pm.bam.gamedeals.remote.cheapshark.datasources.games.RemoteGamesDataSourceImpl
import pm.bam.gamedeals.remote.cheapshark.models.RemoteStore
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.logic.log
import pm.bam.gamedeals.remote.logic.mapAnyFailure
import javax.inject.Inject

internal class RemoteStoresDataSourceImpl @Inject constructor(
    private val logger: Logger,
    private val storesApi: StoresApi,
    private val remoteExceptionTransformer: RemoteExceptionTransformer
) : RemoteStoresDataSource {

    override suspend fun getStores(): List<RemoteStore> =
        storesApi.getStores()
            .log(logger, tag = RemoteGamesDataSourceImpl::class.simpleName)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()
}