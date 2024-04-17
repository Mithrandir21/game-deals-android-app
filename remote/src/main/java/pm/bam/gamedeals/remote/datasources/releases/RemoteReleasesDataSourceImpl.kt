package pm.bam.gamedeals.remote.datasources.releases

import com.skydoves.sandwich.getOrThrow
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.api.ReleaseApi
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.logic.log
import pm.bam.gamedeals.remote.logic.mapAnyFailure
import pm.bam.gamedeals.remote.models.RemoteRelease
import javax.inject.Inject

internal class RemoteReleasesDataSourceImpl @Inject constructor(
    private val logger: Logger,
    private val releaseApi: ReleaseApi,
    private val remoteExceptionTransformer: RemoteExceptionTransformer
) : RemoteReleasesDataSource {

    override suspend fun getReleases(): List<RemoteRelease> =
        releaseApi.getReleases()
            .log(logger, tag = RemoteReleasesDataSourceImpl::class.simpleName)
            .mapAnyFailure { remoteExceptionTransformer.transformApiException(this) }
            .getOrThrow()
}