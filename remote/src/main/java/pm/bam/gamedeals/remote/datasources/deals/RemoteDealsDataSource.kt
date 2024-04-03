package pm.bam.gamedeals.remote.datasources.deals

import pm.bam.gamedeals.remote.api.models.deals.RemoteDealsQuery
import pm.bam.gamedeals.remote.models.RemoteDeal
import pm.bam.gamedeals.remote.models.RemoteDealDetails

interface RemoteDealsDataSource {

    suspend fun getDeals(remoteDealsQuery: RemoteDealsQuery? = null): List<RemoteDeal>

    suspend fun getDeals(id: String): RemoteDealDetails

}