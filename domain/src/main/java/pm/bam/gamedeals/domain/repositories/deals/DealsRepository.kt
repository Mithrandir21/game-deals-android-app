package pm.bam.gamedeals.domain.repositories.deals

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.DealDetails

interface DealsRepository {

    fun observeAllDeals(): Flow<List<Deal>>

    fun getPagingStoreDeals(storeId: Int): Flow<PagingData<Deal>>

    suspend fun getStoreDeals(storeId: Int): List<Deal>

    suspend fun getStoreDeals(storeId: Int, limit: Int): List<Deal>

    suspend fun getDeal(dealId: String): DealDetails

    suspend fun refreshDeals(storeId: Int, force: Boolean = false)
}