package pm.bam.gamedeals.domain.repositories.giveaway

import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.common.onError
import pm.bam.gamedeals.domain.db.dao.GiveawaysDao
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.domain.models.GiveawaySearchParameters
import pm.bam.gamedeals.domain.models.GiveawaySortBy
import pm.bam.gamedeals.domain.models.PUBLISHED_FIELD_NAME
import pm.bam.gamedeals.domain.models.USER_FIELD_NAME
import pm.bam.gamedeals.domain.models.WORTH_FIELD_NAME
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
            .onError { fatal(logger, it) }

    override fun observeGiveaways(giveawaySearchParameters: GiveawaySearchParameters): Flow<List<Giveaway>> {
        val sortBy = giveawaySearchParameters.sortBy.first.toSortBy()
        val typeValues = giveawaySearchParameters.types
            .filter { it.second }
            .takeIf { it.isNotEmpty() }
            ?.map { it.first.name }

        return when (giveawaySearchParameters.isAscending()) {
            true -> giveawaysDao.observeAllGiveawaysAscending(
                sortBy = sortBy,
                typeValues = typeValues
            )

            false -> giveawaysDao.observeAllGiveawaysDescending(
                sortBy = sortBy,
                typeValues = typeValues
            )
        }
            .onError { fatal(logger, it) }
    }

    override suspend fun refreshGiveaways() =
        remoteGiveawayDataSource.getGiveaways()
            .map { remoteRelease -> remoteRelease.toGiveaway(datetimeParsing) }
            .let { giveawaysDao.addGiveaways(*it.toTypedArray()) }


    private fun GiveawaySortBy.toSortBy(): String = when (this) {
        GiveawaySortBy.DATE -> PUBLISHED_FIELD_NAME
        GiveawaySortBy.VALUE -> WORTH_FIELD_NAME
        GiveawaySortBy.POPULARITY -> USER_FIELD_NAME
    }
}