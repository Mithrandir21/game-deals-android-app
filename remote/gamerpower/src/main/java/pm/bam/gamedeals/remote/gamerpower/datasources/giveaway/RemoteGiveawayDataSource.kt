package pm.bam.gamedeals.remote.gamerpower.datasources.giveaway

import pm.bam.gamedeals.remote.gamerpower.models.RemoteGiveaway

interface RemoteGiveawayDataSource {

    suspend fun getGiveaways(): List<RemoteGiveaway>

}