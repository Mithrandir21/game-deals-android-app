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
import pm.bam.gamedeals.domain.models.DealDetails
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.repositories.deals.DealsRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheetData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenListData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenStatus
import pm.bam.gamedeals.testing.MainCoroutineRule
import pm.bam.gamedeals.testing.TestingLoggingListener
import pm.bam.gamedeals.testing.utils.observeEmissions
import pm.bam.gamedeals.testing.utils.second
import pm.bam.gamedeals.testing.utils.third

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
        assertEquals(1, emissions.size)
        assertNotNull(emissions.first())
        assertEquals(HomeScreenData(state = HomeScreenStatus.LOADING), emissions.first())

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
        assertEquals(HomeScreenData(items = data), emissions.second())


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

    @Test
    fun `load deal details`() = runTest {
        val storeId = topStores.first()
        val store: Store = mockk {
            every { storeID } returns storeId
        }

        val dealGameInfo: DealDetails.GameInfo = mockk()
        val cheapestDeal: DealDetails.CheapestPrice = mockk()
        val cheapestDealStore: DealDetails.CheaperStore = mockk {
            every { storeID } returns storeId
        }

        val dealDetails: DealDetails = mockk {
            every { gameInfo } returns dealGameInfo
            every { cheapestPrice } returns cheapestDeal
            every { cheaperStores } returns listOf(cheapestDealStore)
        }

        val dealId = "DealId"
        val dealTitle = "Title"
        val dealPriceDenominated = "SalePriceDenominated"
        val deal: Deal = mockk {
            every { storeID } returns storeId
            every { dealID } returns dealId
            every { title } returns dealTitle
            every { salePriceDenominated } returns dealPriceDenominated
        }


        coEvery { storesRepository.getStore(storeId) } returns store
        coEvery { storesRepository.observeStores() } returns flowOf(listOf())
        coEvery { dealsRepository.getDeal(dealId) } returns dealDetails

        viewModel = HomeViewModel(storesRepository, dealsRepository, logger)
        val emissions = observeStates()

        viewModel.loadDealDetails(deal)


        assertEquals(2, emissions.size)
        assertNotNull(emissions.second())
        assertEquals(HomeScreenData(dealDetailsData = DealBottomSheetData.DealDetailsLoading(
            store = store,
            gameName = dealTitle,
            dealId = dealId,
            gameSalesPriceDenominated = dealPriceDenominated
        )), emissions.second())


        // Advance time to trigger the next emission as the delay is 750ms
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(1000)

        assertEquals(3, emissions.size)
        assertNotNull(emissions.third())
        assertEquals(HomeScreenData(dealDetailsData = DealBottomSheetData.DealDetailsData(
            store = store,
            gameName = dealTitle,
            dealId = dealId,
            gameSalesPriceDenominated = dealPriceDenominated,
            gameInfo = dealGameInfo,
            cheapestPrice = cheapestDeal,
            cheaperStores = listOf(cheapestDealStore).map { store to it }
        )), emissions.third())


        coVerify(exactly = 1) { storesRepository.observeStores() }
        coVerify(exactly = 0) { dealsRepository.getStoreDeals(any(), any()) }

        coVerify(exactly = 1) { dealsRepository.getDeal(dealId) }
        coVerify(exactly = 3) { storesRepository.getStore(storeId) }
    }

    @Test
    fun `load deal details failure`() = runTest {
        val storeId = topStores.first()
        val deal: Deal = mockk {
            every { storeID } returns storeId
        }

        coEvery { storesRepository.observeStores() } returns flowOf(listOf())
        coEvery { storesRepository.getStore(storeId) } throws Exception()

        viewModel = HomeViewModel(storesRepository, dealsRepository, logger)
        val emissions = observeStates()

        viewModel.loadDealDetails(deal)


        assertEquals(2, emissions.size)
        assertNotNull(emissions.second())
        assertEquals(HomeScreenData(state = HomeScreenStatus.ERROR), emissions.second())


        coVerify(exactly = 1) { storesRepository.observeStores() }
        coVerify(exactly = 0) { dealsRepository.getStoreDeals(any(), any()) }

        coVerify(exactly = 0) { dealsRepository.getDeal(any()) }
        coVerify(exactly = 1) { storesRepository.getStore(storeId) }
    }

    @Test
    fun `dismiss deal details`() = runTest {
        coEvery { storesRepository.observeStores() } returns flowOf(listOf())

        viewModel = HomeViewModel(storesRepository, dealsRepository, logger)
        val emissions = observeStates()

        viewModel.dismissDealDetails()


        assertEquals(1, emissions.size)
        assertNotNull(emissions.first())
        assertEquals(HomeScreenData(), emissions.first())
    }

    private fun TestScope.observeStates() = viewModel.uiState.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)

}