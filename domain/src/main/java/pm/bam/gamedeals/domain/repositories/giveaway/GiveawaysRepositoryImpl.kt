package pm.bam.gamedeals.domain.repositories.giveaway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.common.onError
import pm.bam.gamedeals.domain.db.dao.GiveawaysDao
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.domain.models.toGiveaway
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.logging.fatal
import pm.bam.gamedeals.remote.gamerpower.datasources.giveaway.RemoteGiveawayDataSource
import javax.inject.Inject

internal class GiveawaysRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val giveawaysDao: GiveawaysDao,
    private val remoteGiveawayDataSource: RemoteGiveawayDataSource,
    private val datetimeParsing: DatetimeParsing
) : GiveawaysRepository {

    override fun observeGiveaways(): Flow<List<Giveaway>> =
        giveawaysDao.observeAllGiveaways()
            .onStart { refreshGiveaways() }
            .onError { fatal(logger, it) }

    override suspend fun refreshGiveaways() =
        remoteGiveawayDataSource.getGiveaways()
            .map { remoteRelease -> remoteRelease.toGiveaway(datetimeParsing) }
            .let { giveawaysDao.addGiveaways(*it.toTypedArray()) }
}