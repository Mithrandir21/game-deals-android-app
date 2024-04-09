package pm.bam.gamedeals.feature.game.ui

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.domain.models.GameDetails
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.repositories.games.GamesRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.testing.MainCoroutineRule
import pm.bam.gamedeals.testing.TestingLoggingListener
import pm.bam.gamedeals.testing.utils.observeEmissions
import pm.bam.gamedeals.testing.utils.second

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val gamesRepository: GamesRepository = mockk()
    private val storesRepository: StoresRepository = mockk()

    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        viewModel = GameViewModel(TestingLoggingListener(), gamesRepository, storesRepository)
    }


    @Test
    fun `initially loading state`() = runTest {
        val emissions = viewModel.uiState.observeEmissions(this.backgroundScope, mainDispatcherRule.testDispatcher)

        assertEquals(1, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())
    }


    @Test
    fun `error state`() = runTest {val gameId = 1
        coEvery { gamesRepository.getGameDetails(gameId) } throws Exception()

        val emissions = viewModel.uiState.observeEmissions(this.backgroundScope, mainDispatcherRule.testDispatcher)

        assertEquals(1, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())

        viewModel.loadGameDetails(gameId)

        assertEquals(1, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())

        delay(1200) // Delay because Flow 'delayOnStart'

        assertEquals(2, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())
        assertEquals(GameViewModel.GameScreenData.Error, emissions.second())
    }


    @Test
    fun `game load`() = runTest {
        val gameId = 1
        val storeId = 2
        val store: Store = mockk()
        val gameDeals: GameDetails.GameDeal = mockk { every { storeID } returns storeId }
        val dealsList = listOf(gameDeals)
        val gameDetails: GameDetails = mockk { every { deals } returns dealsList }

        coEvery { gamesRepository.getGameDetails(gameId) } returns gameDetails
        coEvery { storesRepository.getStore(storeId) } returns store

        val emissions = viewModel.uiState.observeEmissions(this.backgroundScope, mainDispatcherRule.testDispatcher)

        assertEquals(1, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())

        viewModel.loadGameDetails(gameId)

        assertEquals(1, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())

        delay(1200) // Delay because Flow 'delayOnStart'

        assertEquals(2, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())
        assertEquals(GameViewModel.GameScreenData.Data(gameDetails, listOf(store to gameDeals)), emissions.second())
    }


    @Test
    fun `game reload`() = runTest {
        val gameId = 1
        val storeId = 2
        val store: Store = mockk()
        val gameDeals: GameDetails.GameDeal = mockk { every { storeID } returns storeId }
        val dealsList = listOf(gameDeals)
        val gameDetails: GameDetails = mockk { every { deals } returns dealsList }

        coEvery { gamesRepository.getGameDetails(gameId) } returns gameDetails
        coEvery { storesRepository.getStore(storeId) } returns store

        val emissions = viewModel.uiState.observeEmissions(this.backgroundScope, mainDispatcherRule.testDispatcher)

        assertEquals(1, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())

        viewModel.reloadGameDetails(gameId)

        assertEquals(2, emissions.size)
        assertEquals(GameViewModel.GameScreenData.Loading, emissions.first())
        assertEquals(GameViewModel.GameScreenData.Data(gameDetails, listOf(store to gameDeals)), emissions.second())
    }
}