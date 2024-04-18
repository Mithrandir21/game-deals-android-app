package pm.bam.gamedeals.remote.cheapshark.datasources.games

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
import pm.bam.gamedeals.remote.cheapshark.api.GamesApi
import pm.bam.gamedeals.remote.cheapshark.models.RemoteGame
import pm.bam.gamedeals.remote.exceptions.RemoteExceptionTransformer
import retrofit2.HttpException

class RemoteGamesDataSourceImplTest {

    private val gamesApi: GamesApi = mockk()

    private val logger: Logger = mockk {
        every { log(any(), any(), any(), any()) } just runs
    }

    private val exceptionTransformer: RemoteExceptionTransformer = mockk()

    private val impl = RemoteGamesDataSourceImpl(logger, gamesApi, exceptionTransformer)

    @Test
    fun `search results success`() = runTest {
        val gameTitle = "Game Title"
        val results: RemoteGame = mockk()

        coEvery { gamesApi.getGames(gameTitle) } returns ApiResponse.of { listOf(results) }

        val result = impl.searchGames(gameTitle).first()
        assertEquals(results, result)

        coVerify(exactly = 1) { gamesApi.getGames(gameTitle) }
    }

    @Test
    fun `search results failure - HttpException`() = runTest {
        val gameTitle = "Game Title"
        val remoteException: HttpException = mockk()

        coEvery { gamesApi.getGames(gameTitle) } throws remoteException

        try {
            impl.searchGames(gameTitle)
        } catch (exception: Exception) {
            assertEquals(remoteException, exception)
        }

        coVerify(exactly = 1) { gamesApi.getGames(gameTitle) }
    }
}