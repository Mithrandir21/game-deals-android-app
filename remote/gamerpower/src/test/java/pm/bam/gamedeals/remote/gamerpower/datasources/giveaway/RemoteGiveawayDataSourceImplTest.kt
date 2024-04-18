package pm.bam.gamedeals.remote.gamerpower.datasources.giveaway

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
import pm.bam.gamedeals.remote.gamerpower.api.GamesApi
import pm.bam.gamedeals.remote.gamerpower.models.RemoteGiveaway
import retrofit2.HttpException

class RemoteGiveawayDataSourceImplTest{

    private val gamesApi: GamesApi = mockk()

    private val logger: Logger = mockk {
        every { log(any(), any(), any(), any()) } just runs
    }

    private val exceptionTransformer: RemoteExceptionTransformer = mockk()

    private val impl = RemoteGiveawayDataSourceImpl(logger, gamesApi, exceptionTransformer)

    @Test
    fun `search results success`() = runTest {
        val results: RemoteGiveaway = mockk()

        coEvery { gamesApi.getAllGames() } returns ApiResponse.of { listOf(results) }

        val result = impl.getGiveaways().first()
        Assert.assertEquals(results, result)

        coVerify(exactly = 1) { gamesApi.getAllGames() }
    }

    @Test
    fun `search results failure - HttpException`() = runTest {
        val remoteException: HttpException = mockk()

        coEvery { gamesApi.getAllGames() } throws remoteException

        try {
            impl.getGiveaways()
        } catch (exception: Exception) {
            Assert.assertEquals(remoteException, exception)
        }

        coVerify(exactly = 1) { gamesApi.getAllGames() }
    }
}