package pm.bam.gamedeals.domain.repositories.giveaway

import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.domain.models.GiveawaySearchParameters

interface GiveawaysRepository {

    fun observeGiveaways(): Flow<List<Giveaway>>

    fun observeGiveaways(giveawaySearchParameters: GiveawaySearchParameters): Flow<List<Giveaway>>

    suspend fun refreshGiveaways()

}