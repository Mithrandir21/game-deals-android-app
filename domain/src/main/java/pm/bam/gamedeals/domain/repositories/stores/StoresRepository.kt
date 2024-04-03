package pm.bam.gamedeals.domain.repositories.stores

import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.Store

interface StoresRepository {

    fun observeStores(): Flow<List<Store>>

    suspend fun getStore(storeId: Int): Store

    suspend fun refreshStores(force: Boolean = false)

}