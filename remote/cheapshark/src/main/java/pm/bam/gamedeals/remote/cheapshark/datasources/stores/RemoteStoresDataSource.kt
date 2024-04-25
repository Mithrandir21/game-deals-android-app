package pm.bam.gamedeals.remote.cheapshark.datasources.stores

import pm.bam.gamedeals.remote.cheapshark.models.RemoteStore

interface RemoteStoresDataSource {

    suspend fun getStores(): List<RemoteStore>

}