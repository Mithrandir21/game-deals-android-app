package pm.bam.gamedeals.feature.home.ui


import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.repositories.deals.DealsRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenListData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenStatus
import pm.bam.gamedeals.testing.MainCoroutineRule
import pm.bam.gamedeals.testing.TestingLoggingListener
import pm.bam.gamedeals.testing.utils.observeEmissions
import pm.bam.gamedeals.testing.utils.second

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: HomeViewModel

    private val storesRepository: StoresRepository = mockk()

    private val dealsRepository: DealsRepository = mockk()

    private val logger: TestingLoggingListener = TestingLoggingListener()


    @Test
    fun `initially loading state`() = runTest {
        coEvery { storesRepository.observeStores() } returns flowOf(listOf())

        viewModel = HomeViewModel(storesRepository, dealsRepository, logger)

        val emissions = observeStates()
        assertEquals(2, emissions.size)
        assertNotNull(emissions.first())
        assertEquals(HomeScreenData(state = HomeScreenStatus.LOADING), emissions.first())
        assertNotNull(emissions.second())
        assertEquals(HomeScreenData(state = HomeScreenStatus.SUCCESS), emissions.second())

        coVerify(exactly = 1) { storesRepository.observeStores() }
        coVerify(exactly = 0) { dealsRepository.getStoreDeals(any(), any()) }
    }

    @Test
    fun `load store deals from source`() = runTest {
        val store: Store = mockk {
            every { storeID } returns topStores.first()
        }

        val deal: Deal = mockk()

        coEvery { storesRepository.observeStores() } returns flowOf(listOf(store))
        coEvery { dealsRepository.getStoreDeals(topStores.first(), LIMIT_DEALS) } returns listOf(deal)

        viewModel = HomeViewModel(storesRepository, dealsRepository, logger)
        val emissions = observeStates()

        val data = mutableListOf<HomeScreenListData>()
            .apply {
                add(HomeScreenListData.StoreData(store))
                add(HomeScreenListData.DealData(deal))
                add(HomeScreenListData.ViewAllData(store))
            }


        assertEquals(2, emissions.size)
        assertNotNull(emissions.second())
        assertEquals(HomeScreenData(state = HomeScreenStatus.SUCCESS, items = data), emissions.second())


        coVerify(exactly = 1) { storesRepository.observeStores() }
        coVerify(exactly = 1) { dealsRepository.getStoreDeals(topStores.first(), LIMIT_DEALS) }
    }

    @Test
    fun `load store deals from source failure`() = runTest {
        coEvery { storesRepository.observeStores() } throws Exception()

        viewModel = HomeViewModel(storesRepository, dealsRepository, logger)
        val emissions = observeStates()

        assertEquals(2, emissions.size)
        assertNotNull(emissions.second())
        assertEquals(HomeScreenData(state = HomeScreenStatus.ERROR), emissions.second())


        coVerify(exactly = 1) { storesRepository.observeStores() }
        coVerify(exactly = 0) { dealsRepository.getStoreDeals(any(), any()) }
    }

    private fun TestScope.observeStates() = viewModel.uiState.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)

}