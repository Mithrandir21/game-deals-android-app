package pm.bam.gamedeals.feature.store.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheetData
import pm.bam.gamedeals.feature.deal.ui.DealDetailsViewModel

class StoreScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val deal: Deal = mockk()

    private val viewModel: StoreViewModel = mockk()

    private val dealDealDetailsViewModel: DealDetailsViewModel = mockk()

    private val storeId = 1

    @Before
    fun setup() {
        val dealId = "DealId"
        val dealTitle = "Title"
        val dealPrice = "Price"
        val dealThumb = "DealThumbnail"
        every { deal.dealID } returns dealId
        every { deal.storeID } returns storeId
        every { deal.title } returns dealTitle
        every { deal.salePriceDenominated } returns dealPrice
        every { deal.thumb } returns dealThumb
        val dealPaging: Flow<PagingData<Deal>> = flowOf(PagingData.from(listOf(deal)))
        val dealDetails: StateFlow<DealBottomSheetData?> = MutableStateFlow(null)
        val storeDetails: StateFlow<Store?> = MutableStateFlow(null)

        every { viewModel.deals } returns dealPaging
        every { dealDealDetailsViewModel.dealDealDetails } returns dealDetails
        every { viewModel.storeDetails } returns storeDetails
    }


    @Test
    fun loadingScreen() {
        val pagingData = PagingData.from<Deal>(
            data = listOf(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(true),
                prepend = LoadState.NotLoading(true),
                append = LoadState.Loading
            )
        )

        every { viewModel.deals } returns flowOf(pagingData)
        every { viewModel.setStoreId(storeId) } just runs

        composeTestRule.setContent {
            GameDealsTheme {
                StoreScreen(
                    storeId = storeId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                    dealDealDetailsViewModel = dealDealDetailsViewModel
                )
            }
        }

        composeTestRule.onNodeWithTag(LoadingRowTag)
            .assertIsDisplayed()

        verify(exactly = 1) { viewModel.deals }
        verify(exactly = 1) { dealDealDetailsViewModel.dealDealDetails }
        verify(exactly = 1) { viewModel.storeDetails }
        verify(exactly = 1) { viewModel.setStoreId(storeId) }
    }

    @Test
    fun loadSingleDeal() {
        every { viewModel.setStoreId(storeId) } just runs

        composeTestRule.setContent {
            GameDealsTheme {
                StoreScreen(
                    storeId = storeId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                    dealDealDetailsViewModel = dealDealDetailsViewModel
                )
            }
        }

        verify(exactly = 1) { viewModel.deals }
        verify(exactly = 1) { dealDealDetailsViewModel.dealDealDetails }
        verify(exactly = 1) { viewModel.storeDetails }
        verify(exactly = 1) { viewModel.setStoreId(storeId) }
    }

    @Test
    fun loadDealDetails() {
        every { viewModel.setStoreId(any()) } just runs
        every { dealDealDetailsViewModel.loadDealDetails(any(), any(), any(), any()) } just runs

        composeTestRule.setContent {
            GameDealsTheme {
                StoreScreen(
                    storeId = storeId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                    dealDealDetailsViewModel = dealDealDetailsViewModel
                )
            }
        }

        composeTestRule.onNodeWithTag(DealRowTag.plus(deal.dealID))
            .performClick()

        verify(exactly = 1) { viewModel.deals }
        verify(exactly = 1) { dealDealDetailsViewModel.dealDealDetails }
        verify(exactly = 1) { viewModel.storeDetails }
        verify(exactly = 1) { viewModel.setStoreId(storeId) }
        verify(exactly = 1) { dealDealDetailsViewModel.loadDealDetails(any(), any(), any(), any()) }
    }

    @Test
    fun loadStoreDetails() {
        val name = "StoreName"
        val store: Store = mockk {
            every { images } returns mockk {
                every { banner } returns "Store Banner"
            }
            every { storeName } returns name
        }

        val storeDetails: StateFlow<Store?> = MutableStateFlow(store)


        every { viewModel.storeDetails } returns storeDetails
        every { viewModel.setStoreId(storeId) } just runs

        composeTestRule.setContent {
            GameDealsTheme {
                StoreScreen(
                    storeId = storeId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                    dealDealDetailsViewModel = dealDealDetailsViewModel
                )
            }
        }

        composeTestRule.onNodeWithTag(StoreTopBarTag)
            .onChildren()
            .filterToOne(hasText(name))
            .assertIsDisplayed()


        verify(exactly = 1) { viewModel.deals }
        verify(exactly = 1) { dealDealDetailsViewModel.dealDealDetails }
        verify(exactly = 1) { viewModel.storeDetails }
        verify(exactly = 1) { viewModel.setStoreId(storeId) }
    }

    @Test
    fun onBackActioned() {
        val onBack: () -> Unit = mockk()

        every { onBack.invoke() } just runs
        every { viewModel.setStoreId(storeId) } just runs

        composeTestRule.setContent {
            GameDealsTheme {
                StoreScreen(
                    storeId = storeId,
                    onBack = onBack,
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                    dealDealDetailsViewModel = dealDealDetailsViewModel
                )
            }
        }

        composeTestRule.onNodeWithTag(TopBarNavTag)
            .performClick()

        verify(exactly = 1) { viewModel.deals }
        verify(exactly = 1) { dealDealDetailsViewModel.dealDealDetails }
        verify(exactly = 1) { viewModel.storeDetails }
        verify(exactly = 1) { viewModel.setStoreId(storeId) }
        verify(exactly = 1) { onBack.invoke() }
    }
}