package pm.bam.gamedeals.feature.store.ui

import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.DealDetails
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.repositories.deals.DealsRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheetData
import pm.bam.gamedeals.testing.MainCoroutineRule
import pm.bam.gamedeals.testing.TestingLoggingListener
import pm.bam.gamedeals.testing.utils.observeEmissions
import pm.bam.gamedeals.testing.utils.second
import pm.bam.gamedeals.testing.utils.third

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val storesRepository: StoresRepository = mockk()

    private val dealsRepository: DealsRepository = mockk()

    private lateinit var viewModel: StoreViewModel

    @Before
    fun setup() {
        viewModel = StoreViewModel(TestingLoggingListener(), dealsRepository, storesRepository)
    }

    @Test
    fun `initially store details is null`() = runTest {
        val emissions = viewModel.storeDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)
        assertEquals(1, emissions.size)
        assertNull(emissions.first())
    }

    @Test
    fun `setting storeId loads StoreDetails`() = runTest {
        val storeId = 1
        val store: Store = mockk()

        coEvery { storesRepository.getStore(storeId) } returns store

        val emissions = viewModel.storeDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)


        assertEquals(1, emissions.size)
        assertNull(emissions.first())

        viewModel.setStoreId(storeId)

        assertEquals(2, emissions.size)
        assertNull(emissions.first())
        assertEquals(store, emissions.second())
    }

    @Test
    fun `setting storeId failure - throws caught error`() = runTest {
        val storeId = 1
        val exception: Exception = mockk {
            every { printStackTrace() } just runs
        }

        coEvery { storesRepository.getStore(222) } throws exception
        coEvery { storesRepository.getStore(storeId) } throws exception

        val emissions = viewModel.storeDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)


        assertEquals(1, emissions.size)
        assertNull(emissions.first())

        viewModel.setStoreId(storeId)

        assertEquals(1, emissions.size)
        assertNull(emissions.first())
    }

    @Test
    fun `initially deal details is null`() = runTest {
        val emissions = viewModel.dealDealDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)
        assertEquals(1, emissions.size)
        assertNull(emissions.first())
    }

    @Test
    fun `loading Deal results in DealDetails`() = runTest {
        val dealId = "Deal ID"
        val storeId = 2
        val dealTitle = "Deal Title"
        val dealSalePriceDenominated = "$12"
        val deal: Deal = mockk {
            every { dealID } returns dealId
            every { storeID } returns storeId
            every { title } returns dealTitle
            every { salePriceDenominated } returns dealSalePriceDenominated
        }

        val mockGameInfo: DealDetails.GameInfo = mockk()
        val mockCheapestPrice: DealDetails.CheapestPrice = mockk()
        val mockCheaperStore: DealDetails.CheaperStore = mockk {
            every { storeID } returns storeId
        }
        val dealDetails: DealDetails = mockk {
            every { gameInfo } returns mockGameInfo
            every { cheapestPrice } returns mockCheapestPrice
            every { cheaperStores } returns listOf(mockCheaperStore)
        }

        val store: Store = mockk()

        coEvery { dealsRepository.getDeal(dealId) } returns dealDetails
        coEvery { storesRepository.getStore(storeId) } returns store

        val emissions = viewModel.dealDealDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)

        assertEquals(1, emissions.size)
        assertNull(emissions.first())

        viewModel.loadDealDetails(deal)


        assertEquals(2, emissions.size)
        assertNull(emissions.first())
        assertEquals(
            DealBottomSheetData.DealDetailsLoading(
                store = store,
                gameName = deal.title,
                dealId = dealId,
                gameSalesPriceDenominated = dealSalePriceDenominated
            ), emissions.second()
        )

        delay(1000) // Delay because Flow 'mapDelayAtLeast'


        assertEquals(3, emissions.size)
        assertEquals(
            DealBottomSheetData.DealDetailsData(
                store = store,
                gameName = deal.title,
                dealId = dealId,
                gameSalesPriceDenominated = deal.salePriceDenominated,
                gameInfo = dealDetails.gameInfo,
                cheapestPrice = dealDetails.cheapestPrice,
                cheaperStores = dealDetails.cheaperStores.map { store to it }
            ), emissions.third()
        )
    }

    @Test
    fun `dismissing deal details`() = runTest {
        val emissions = viewModel.dealDealDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)

        assertEquals(1, emissions.size)
        assertNull(emissions.first())

        viewModel.dismissDealDetails()

        assertEquals(1, emissions.size)
        assertNull(emissions.first())
    }
}