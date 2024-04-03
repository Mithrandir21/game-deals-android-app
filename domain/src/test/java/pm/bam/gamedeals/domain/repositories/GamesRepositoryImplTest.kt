package pm.bam.gamedeals.domain.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.common.datetime.formatting.DateTimeFormatter
import pm.bam.gamedeals.domain.db.dao.GamesDao
import pm.bam.gamedeals.domain.models.Game
import pm.bam.gamedeals.domain.models.GameDetails
import pm.bam.gamedeals.domain.models.toGame
import pm.bam.gamedeals.domain.models.toGameDetails
import pm.bam.gamedeals.domain.repositories.games.GamesRepositoryImpl
import pm.bam.gamedeals.domain.transformations.CurrencyTransformation
import pm.bam.gamedeals.remote.datasources.deals.RemoteDealsDataSource
import pm.bam.gamedeals.remote.datasources.games.RemoteGamesDataSource
import pm.bam.gamedeals.remote.models.RemoteGame
import pm.bam.gamedeals.remote.models.RemoteGameDetails

class GamesRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val gamesDao: GamesDao = mockk()

    private val remoteGamesDataSource: RemoteGamesDataSource = mockk()

    private val remoteDealsDataSource: RemoteDealsDataSource = mockk()

    private val currencyTransformation: CurrencyTransformation = mockk()

    private val dateTimeFormatter: DateTimeFormatter = mockk()

    private val impl = GamesRepositoryImpl(gamesDao, remoteGamesDataSource, remoteDealsDataSource, currencyTransformation, dateTimeFormatter)

    @Test
    fun `observe games with refresh called`() = runTest {
        val remoteResults: RemoteGame = mockk()
        val results: Game = mockk()

        mockkStatic(RemoteGame::toGame)
        every { remoteResults.toGame(currencyTransformation) } returns results

        coEvery { gamesDao.observeAllGames() } returns flowOf(emptyList())
        coEvery { remoteGamesDataSource.searchGames("") } returns listOf(remoteResults)
        coEvery { gamesDao.addGames(results) } just runs


        val result = impl.observeGames().first()
        Assert.assertTrue(result.isEmpty())

        coVerify(exactly = 1) { gamesDao.observeAllGames() }
        coVerify(exactly = 1) { remoteGamesDataSource.searchGames("") }
        coVerify(exactly = 1) { gamesDao.addGames(results) }
    }


    @Test
    fun `get game`() = runTest {
        val id = 1
        val idString = id.toString()
        val remoteResults: RemoteGameDetails = mockk()
        val results: GameDetails = mockk()

        mockkStatic(RemoteGameDetails::toGameDetails)
        every { remoteResults.toGameDetails(currencyTransformation, dateTimeFormatter) } returns results

        coEvery { remoteGamesDataSource.getGameDetails(idString) } returns remoteResults


        val result = impl.getGameDetails(id)
        Assert.assertEquals(results, result)

        coVerify(exactly = 1) { remoteGamesDataSource.getGameDetails(idString) }
    }


    @Test
    fun `refresh games`() = runTest {
        val remoteResults: RemoteGame = mockk()
        val results: Game = mockk()

        mockkStatic(RemoteGame::toGame)
        every { remoteResults.toGame(currencyTransformation) } returns results

        coEvery { remoteGamesDataSource.searchGames("") } returns listOf(remoteResults)
        coEvery { gamesDao.addGames(results) } just runs


        impl.refreshGames()

        coVerify(exactly = 1) { remoteGamesDataSource.searchGames("") }
        coVerify(exactly = 1) { gamesDao.addGames(results) }
    }

}