package pm.bam.gamedeals.remote.cheapshark.datasources.releases

import com.skydoves.sandwich.ApiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.cheapshark.api.ReleaseApi
import pm.bam.gamedeals.remote.cheapshark.models.RemoteRelease
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import retrofit2.HttpException

class RemoteReleasesDataSourceImplTest {

    private val releaseApi: ReleaseApi = mockk()

    private val logger: Logger = mockk {
        every { log(any(), any(), any(), any()) } just runs
    }

    private val exceptionTransformer: RemoteExceptionTransformer = mockk()

    private val impl = RemoteReleasesDataSourceImpl(logger, releaseApi, exceptionTransformer)

    @Test
    fun `search results success`() = runTest {
        val results: RemoteRelease = mockk()

        coEvery { releaseApi.getReleases() } returns ApiResponse.of { listOf(results) }

        val result = impl.getReleases().first()
        Assert.assertEquals(results, result)

        coVerify(exactly = 1) { releaseApi.getReleases() }
    }

    @Test
    fun `search results failure - HttpException`() = runTest {
        val remoteException: HttpException = mockk()

        coEvery { releaseApi.getReleases() } throws remoteException

        try {
            impl.getReleases()
        } catch (exception: Exception) {
            Assert.assertEquals(remoteException, exception)
        }

        coVerify(exactly = 1) { releaseApi.getReleases() }
    }
}