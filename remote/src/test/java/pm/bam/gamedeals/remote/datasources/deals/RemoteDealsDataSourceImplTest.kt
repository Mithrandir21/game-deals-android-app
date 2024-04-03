package pm.bam.gamedeals.remote.datasources.deals

import com.skydoves.sandwich.ApiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.api.DealsApi
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.models.RemoteDeal
import retrofit2.HttpException

class RemoteDealsDataSourceImplTest {

    private val dealsApi: DealsApi = mockk()

    private val logger: Logger = mockk {
        every { log(any(), any(), any(), any()) } just runs
    }

    private val exceptionTransformer: RemoteExceptionTransformer = mockk()

    private val impl = RemoteDealsDataSourceImpl(logger, dealsApi, exceptionTransformer)

    @Test
    fun `search results success`() = runTest {
        val results: RemoteDeal = mockk()

        coEvery { dealsApi.getDeals() } returns ApiResponse.of { listOf(results) }

        val result = impl.getDeals()
        assertEquals(results, result.first())

        coVerify(exactly = 1) { dealsApi.getDeals() }
    }

    @Test
    fun `search results failure - HttpException`() = runTest {
        val remoteException: HttpException = mockk()

        coEvery { dealsApi.getDeals() } throws remoteException

        try {
            impl.getDeals()
        } catch (exception: Exception) {
            assertEquals(remoteException, exception)
        }

        coVerify(exactly = 1) { dealsApi.getDeals() }
    }
}