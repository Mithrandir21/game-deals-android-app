package pm.bam.gamedeals.domain.repositories.releases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import pm.bam.gamedeals.common.onError
import pm.bam.gamedeals.domain.db.dao.ReleasesDao
import pm.bam.gamedeals.domain.models.Release
import pm.bam.gamedeals.domain.models.toRelease
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.logging.fatal
import pm.bam.gamedeals.remote.datasources.releases.RemoteReleasesDataSource
import javax.inject.Inject

internal class ReleasesRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val releasesDao: ReleasesDao,
    private val remoteReleasesDataSource: RemoteReleasesDataSource
) : ReleasesRepository {

    override fun observeReleases(): Flow<List<Release>> =
        releasesDao.observeAllReleases()
            .onStart { refreshReleases() }
            .onError { fatal(logger, it) }

    override suspend fun refreshReleases() =
        remoteReleasesDataSource.getReleases()
            .map { remoteRelease -> remoteRelease.toRelease() }
            .let { releasesDao.addReleases(*it.toTypedArray()) }
}