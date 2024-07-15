package pm.bam.gamedeals.domain.repositories.free

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.common.datetime.parsing.DatetimeParsing
import pm.bam.gamedeals.domain.db.dao.FreeGamesDao
import pm.bam.gamedeals.domain.models.FreeGame
import pm.bam.gamedeals.domain.models.FreeGameSearchParameters
import pm.bam.gamedeals.domain.models.FreeGameSortBy
import pm.bam.gamedeals.domain.models.toFreeGame
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.remote.freetogame.datasources.RemoteFreeGamesDataSource
import pm.bam.gamedeals.remote.freetogame.models.RemoteFreeGame
import pm.bam.gamedeals.testing.TestingLoggingListener
import java.time.LocalDate

class FreeGamesRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val logger: Logger = TestingLoggingListener()

    private val freeGamesDao: FreeGamesDao = mockk()

    private val remoteFreeGamesDataSource: RemoteFreeGamesDataSource = mockk()

    private val datetimeParsing: DatetimeParsing = mockk()

    private val impl = FreeGamesRepositoryImpl(logger, freeGamesDao, remoteFreeGamesDataSource, datetimeParsing)

    @Test
    fun `observe free game with descending releaseDate order`() = runTest {
        val resultOne = mockk<FreeGame> {
            every { releaseDate } returns LocalDate.MIN
        }
        val resultTwo = mockk<FreeGame> {
            every { releaseDate } returns LocalDate.MAX
        }

        every { freeGamesDao.observeAllFreeGames() } returns flowOf(listOf(resultOne, resultTwo))

        val result = impl.observeFreeGames().first()
        Assert.assertTrue(result.isNotEmpty())

        Assert.assertEquals(result[0], resultTwo)
        Assert.assertEquals(result[1], resultOne)

        verify(exactly = 1) { freeGamesDao.observeAllFreeGames() }
    }

    @Test
    fun `refresh giveaways`() = runTest {
        val remoteFreeGame = mockk<RemoteFreeGame>()
        val giveaway = mockk<FreeGame>()

        mockkStatic(RemoteFreeGame::toFreeGame)
        every { remoteFreeGame.toFreeGame(datetimeParsing) } returns giveaway

        coEvery { remoteFreeGamesDataSource.getAllFreeGames() } returns listOf(remoteFreeGame)
        coEvery { freeGamesDao.addFreeGames(any()) } just Runs

        impl.refreshFreeGames()

        coVerify(exactly = 1) { remoteFreeGamesDataSource.getAllFreeGames() }
        coVerify(exactly = 1) { freeGamesDao.addFreeGames(any()) }
    }

    @Test
    fun `observe giveaways with search parameters`() = runTest {
        val mmorpg = "MMORPG"
        val fantasy = "Fantasy"

        val pc = "PC"
        val web = "Web"


        val resultOne = mockk<FreeGame> {
            every { title } returns "C"
            every { genre } returns mmorpg
            every { platform } returns pc
            every { releaseDate } returns LocalDate.MIN
        }
        val resultTwo = mockk<FreeGame> {
            every { title } returns "B"
            every { genre } returns fantasy
            every { platform } returns web
            every { releaseDate } returns LocalDate.MAX
        }
        val resultThree = mockk<FreeGame> {
            every { title } returns "A"
            every { genre } returns "Other"
            every { platform } returns "Other"
            every { releaseDate } returns LocalDate.MAX
        }

        every { freeGamesDao.observeAllFreeGames() } returns flowOf(listOf(resultOne, resultTwo, resultThree))

        val para = FreeGameSearchParameters(
            platforms = listOf(pc to true, web to true),
            genres = listOf(mmorpg to true, fantasy to true),
            sortBy = FreeGameSortBy.RELEASE_DATE
        )

        val resultDescendingDate = impl.observeFreeGames(para).first()
        Assert.assertTrue(resultDescendingDate.isNotEmpty())
        Assert.assertEquals(2, resultDescendingDate.size)

        Assert.assertEquals(resultDescendingDate[0], resultTwo)
        Assert.assertEquals(resultDescendingDate[1], resultOne)


        val resultDescendingPopularity = impl.observeFreeGames(para.copy(sortBy = FreeGameSortBy.RELEASE_DATE)).first()
        Assert.assertTrue(resultDescendingPopularity.isNotEmpty())
        Assert.assertEquals(2, resultDescendingPopularity.size)

        Assert.assertEquals(resultDescendingPopularity[0], resultTwo)
        Assert.assertEquals(resultDescendingPopularity[1], resultOne)


        val resultDescendingWorth = impl.observeFreeGames(para.copy(sortBy = FreeGameSortBy.ALPHABETICAL)).first()
        Assert.assertTrue(resultDescendingWorth.isNotEmpty())
        Assert.assertEquals(2, resultDescendingWorth.size)

        Assert.assertEquals(resultDescendingWorth[0], resultOne)
        Assert.assertEquals(resultDescendingWorth[1], resultTwo)

        verify(exactly = 3) { freeGamesDao.observeAllFreeGames() }
    }
}