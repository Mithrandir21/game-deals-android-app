package pm.bam.gamedeals.remote.cheapshark.datasources.releases

import pm.bam.gamedeals.remote.cheapshark.models.RemoteRelease

interface RemoteReleasesDataSource {

    suspend fun getReleases(): List<RemoteRelease>

}