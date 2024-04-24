package pm.bam.gamedeals.remote.cheapshark.datasources.deals

import pm.bam.gamedeals.remote.cheapshark.api.models.deals.RemoteDealsQuery
import pm.bam.gamedeals.remote.cheapshark.models.RemoteDeal
import pm.bam.gamedeals.remote.cheapshark.models.RemoteDealDetails

interface RemoteDealsDataSource {

    suspend fun getDeals(remoteDealsQuery: RemoteDealsQuery? = null): List<RemoteDeal>

    suspend fun getDeals(id: String): RemoteDealDetails

}