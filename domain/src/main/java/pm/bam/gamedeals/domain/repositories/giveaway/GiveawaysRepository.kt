package pm.bam.gamedeals.domain.repositories.giveaway

import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.Giveaway

interface GiveawaysRepository {

    fun observeGiveaways(): Flow<List<Giveaway>>

    suspend fun refreshGiveaways()

}