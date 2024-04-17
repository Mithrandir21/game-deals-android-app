package pm.bam.gamedeals.remote.datasources.releases

import pm.bam.gamedeals.remote.models.RemoteRelease

interface RemoteReleasesDataSource {

    suspend fun getReleases(): List<RemoteRelease>

}