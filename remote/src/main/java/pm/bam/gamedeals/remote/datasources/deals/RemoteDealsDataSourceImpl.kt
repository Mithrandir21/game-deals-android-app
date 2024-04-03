package pm.bam.gamedeals.remote.datasources.deals

import com.skydoves.sandwich.getOrThrow
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.api.DealsApi
import pm.bam.gamedeals.remote.api.models.deals.RemoteDealsQuery
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.logic.log
import pm.bam.gamedeals.remote.logic.mapAnyFailure
import pm.bam.gamedeals.remote.models.RemoteDeal
import pm.bam.gamedeals.remote.models.RemoteDealDetails
import javax.inject.Inject

internal class RemoteDealsDataSourceImpl @Inject constructor(
    private val logger: Logger,
    private val dealsApi: DealsApi,
    private val remoteExceptionTransformer: RemoteExceptionTransformer
) : RemoteDealsDataSource {

    override suspend fun getDeals(remoteDealsQuery: RemoteDealsQuery?): List<RemoteDeal> =
        dealsApi.getDeals(
            storeID = remoteDealsQuery?.storeID,
            pageNumber = remoteDealsQuery?.pageNumber,
            pageSize = remoteDealsQuery?.pageSize,
            sortBy = remoteDealsQuery?.sortBy,
            desc = remoteDealsQuery?.desc,
            lowerPrice = remoteDealsQuery?.lowerPrice,
            upperPrice = remoteDealsQuery?.upperPrice,
            metacritic = remoteDealsQuery?.metacritic,
            steamRating = remoteDealsQuery?.steamRating,
            maxAge = remoteDealsQuery?.maxAge,
            steamAppID = remoteDealsQuery?.steamAppID,
            title = remoteDealsQuery?.title,
            exact = remoteDealsQuery?.exact,
            aaa = remoteDealsQuery?.aaa,
            steamworks = remoteDealsQuery?.steamworks,
            onSale = remoteDealsQuery?.onSale
        )
            .log(logger)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()

    override suspend fun getDeals(id: String): RemoteDealDetails = dealsApi.getDeal(id)
        .log(logger)
        .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
        .getOrThrow()
}
