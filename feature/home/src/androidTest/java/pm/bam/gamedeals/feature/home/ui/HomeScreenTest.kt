package pm.bam.gamedeals.feature.home.ui

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.deal.ui.DealDetailsViewModel
import pm.bam.gamedeals.feature.home.R
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenListData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenStatus

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val screenOrientationRule: ScreenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)

    private val viewModel: HomeViewModel = mockk()

    private val dealDealDetailsViewModel: DealDetailsViewModel = mockk()

    private val storeId = 1
    private val storeTitle = "StoreTitle"

    private val storeBanner = "Banner"
    private val storeLogo = "Logo"

    private val dealId = "DealId"
    private val dealTitle = "Title"
    private val normalPrice = "NormalPrice"
    private val dealPrice = "Price"
    private val dealThumb = "DealThumbnail"


    private val mockImages: Store.StoreImages = mockk {
        every { banner } returns storeBanner
        every { logo } returns storeLogo
    }
    private val mockStore: Store = mockk {
        every { storeID } returns storeId
        every { storeName } returns storeTitle
        every { images } returns mockImages
    }
    private val mockDeal: Deal = mockk {
        every { dealID } returns dealId
        every { title } returns dealTitle
        every { normalPriceDenominated } returns normalPrice
        every { salePriceDenominated } returns dealPrice
        every { thumb } returns dealThumb
    }
    private val mockStoreData = HomeScreenListData.StoreData(mockStore)
    private val mockDealData = HomeScreenListData.DealData(mockDeal)
    private val mockViewAllData = HomeScreenListData.ViewAllData(mockStore)

    @Before
    fun setup() {
        every { dealDealDetailsViewModel.dealDealDetails } returns MutableStateFlow(null)
    }

    @Test
    fun loadingState() {
        val mockData = HomeScreenData(state = HomeScreenStatus.LOADING)

        every { viewModel.uiState } returns MutableStateFlow(mockData)

        composeTestRule.setContent {
            HomeScreen(
                onSearch = {},
                onViewStoreDeals = {},
                goToWeb = { _, _ -> },
                viewModel = viewModel,
                dealDealDetailsViewModel = dealDealDetailsViewModel
            )
        }

        composeTestRule.onNodeWithTag(HomeScreenStoreBannerTag.plus(storeId))
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenDealRowTag.plus(dealId))
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenViewAllButtonTag.plus(storeId))
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenLoadingTag)
            .assertIsDisplayed()
    }

    @Test
    fun errorState() {
        val mockData = HomeScreenData(state = HomeScreenStatus.ERROR)

        every { viewModel.uiState } returns MutableStateFlow(mockData)

        var snackText = ""
        var snackRetry = ""

        composeTestRule.setContent {
            snackText = stringResource(id = R.string.home_screen_data_loading_error_msg)
            snackRetry = stringResource(id = R.string.home_screen_data_loading_error_retry)

            HomeScreen(
                onSearch = {},
                onViewStoreDeals = {},
                goToWeb = { _, _ -> },
                viewModel = viewModel,
                dealDealDetailsViewModel = dealDealDetailsViewModel
            )
        }

        composeTestRule.onNodeWithTag(HomeScreenStoreBannerTag.plus(storeId))
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenDealRowTag.plus(dealId))
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenViewAllButtonTag.plus(storeId))
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenLoadingTag)
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithText(snackText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(snackRetry)
            .assertIsDisplayed()
    }

    @Test
    fun storeDataLoaded() {
        val mockData = HomeScreenData(state = HomeScreenStatus.SUCCESS, items = listOf(mockStoreData, mockDealData, mockViewAllData))

        every { viewModel.uiState } returns MutableStateFlow(mockData)

        composeTestRule.setContent {
            HomeScreen(
                onSearch = {},
                onViewStoreDeals = {},
                goToWeb = { _, _ -> },
                viewModel = viewModel,
                dealDealDetailsViewModel = dealDealDetailsViewModel
            )
        }

        composeTestRule.onNodeWithTag(HomeScreenStoreBannerTag.plus(storeId))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenDealRowTag.plus(dealId))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenViewAllButtonTag.plus(storeId))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(HomeScreenLoadingTag)
            .assertIsNotDisplayed()
    }

    @Test
    fun storeDataLoadedWide() {
        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)

        storeDataLoaded()
    }

}