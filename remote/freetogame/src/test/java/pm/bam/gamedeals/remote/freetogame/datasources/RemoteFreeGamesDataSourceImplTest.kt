package pm.bam.gamedeals.remote.freetogame.datasources

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
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import pm.bam.gamedeals.remote.freetogame.api.FreeGamesApi
import pm.bam.gamedeals.remote.freetogame.models.RemoteFreeGame
import retrofit2.HttpException

class RemoteFreeGamesDataSourceImplTest {

    private val freeGamesApi: FreeGamesApi = mockk()

    private val logger: Logger = mockk {
        every { log(any(), any(), any(), any()) } just runs
    }

    private val exceptionTransformer: RemoteExceptionTransformer = mockk()

    private val impl = RemoteFreeGamesDataSourceImpl(logger, freeGamesApi, exceptionTransformer)

    @Test
    fun `search results success`() = runTest {
        val results: RemoteFreeGame = mockk()

        coEvery { freeGamesApi.getAllFreeGames() } returns ApiResponse.of { listOf(results) }

        val result = impl.getAllFreeGames().first()
        Assert.assertEquals(results, result)

        coVerify(exactly = 1) { freeGamesApi.getAllFreeGames() }
    }

    @Test
    fun `search results failure - HttpException`() = runTest {
        val remoteException: HttpException = mockk()

        coEvery { freeGamesApi.getAllFreeGames() } throws remoteException

        try {
            impl.getAllFreeGames()
        } catch (exception: Exception) {
            Assert.assertEquals(remoteException, exception)
        }

        coVerify(exactly = 1) { freeGamesApi.getAllFreeGames() }
    }
}