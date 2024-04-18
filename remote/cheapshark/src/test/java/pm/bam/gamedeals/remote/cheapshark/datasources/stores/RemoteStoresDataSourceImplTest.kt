package pm.bam.gamedeals.remote.cheapshark.datasources.stores

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
import pm.bam.gamedeals.remote.cheapshark.api.StoresApi
import pm.bam.gamedeals.remote.cheapshark.models.RemoteStore
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import retrofit2.HttpException

class RemoteStoresDataSourceImplTest {

    private val storesApi: StoresApi = mockk()

    private val logger: Logger = mockk {
        every { log(any(), any(), any(), any()) } just runs
    }

    private val exceptionTransformer: RemoteExceptionTransformer = mockk()

    private val impl = RemoteStoresDataSourceImpl(logger, storesApi, exceptionTransformer)

    @Test
    fun `search results success`() = runTest {
        val results: RemoteStore = mockk()

        coEvery { storesApi.getStores() } returns ApiResponse.of { listOf(results) }

        val result = impl.getStores().first()
        assertEquals(results, result)

        coVerify(exactly = 1) { storesApi.getStores() }
    }

    @Test
    fun `search results failure - HttpException`() = runTest {
        val remoteException: HttpException = mockk()

        coEvery { storesApi.getStores() } throws remoteException

        try {
            impl.getStores()
        } catch (exception: Exception) {
            assertEquals(remoteException, exception)
        }

        coVerify(exactly = 1) { storesApi.getStores() }
    }
}