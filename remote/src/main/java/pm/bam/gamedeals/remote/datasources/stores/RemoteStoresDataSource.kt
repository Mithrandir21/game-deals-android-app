package pm.bam.gamedeals.remote.datasources.stores

import pm.bam.gamedeals.remote.models.RemoteStore

interface RemoteStoresDataSource {

    suspend fun getStores(): List<RemoteStore>

}