package pm.bam.gamedeals.domain.repositories.releases

import kotlinx.coroutines.flow.Flow
import pm.bam.gamedeals.domain.models.Release

interface ReleasesRepository {

    fun observeReleases(): Flow<List<Release>>

    suspend fun refreshReleases()

}