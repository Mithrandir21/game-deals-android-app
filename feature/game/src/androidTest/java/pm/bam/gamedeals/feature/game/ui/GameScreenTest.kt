package pm.bam.gamedeals.feature.game.ui

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.domain.models.GameDetails
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.game.R

class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val screenOrientationRule: ScreenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)


    private val viewModel: GameViewModel = mockk()


    private val gameTitle = "Title"

    private val mockCheapestPriceEver: GameDetails.GameCheapestPriceEver = mockk {
        every { priceDenominated } returns "PriceDenominated"
        every { date } returns "Date"
    }
    private val gameInfo: GameDetails.GameInfo = mockk {
        every { thumb } returns "Thumb"
        every { title } returns gameTitle
    }

    private val dealId = "ID"
    private val dealSavings = 10
    private val price = 1.1
    private val priceDenominatedText = "$price"
    private val gameDeal: GameDetails.GameDeal = mockk {
        every { dealID } returns dealId
        every { savings } returns dealSavings
        every { priceValue } returns price
        every { priceDenominated } returns priceDenominatedText
    }
    private val gameDetails: GameDetails = mockk {
        every { info } returns gameInfo
        every { deals } returns listOf(gameDeal)
        every { cheapestPriceEver } returns mockCheapestPriceEver
    }

    private val mockStoreName = "Store Name"
    private val storeImages: Store.StoreImages = mockk {
        every { icon } returns "Icon"
    }
    private val store: Store = mockk {
        every { storeName } returns mockStoreName
        every { images } returns storeImages
    }


    @Before
    fun setup() {
        every { viewModel.loadGameDetails(any()) } just runs
        every { viewModel.reloadGameDetails(any()) } just runs
    }

    @Test
    fun initialLoading() {
        val gameId = 1

        every { viewModel.uiState } returns MutableStateFlow(GameViewModel.GameScreenData.Loading)

        var expectedTitle = ""

        composeTestRule.setContent {
            expectedTitle = stringResource(id = R.string.game_screen_toolbar_title_loading)

            GameDealsTheme {
                GameScreen(
                    gameId = gameId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                )
            }
        }

        composeTestRule.onNodeWithTag(TopAppBarTag)
            .assertIsDisplayed()
            .onChildren()
            .filterToOne(hasTextExactly(expectedTitle))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(LoadingDataTag)
            .assertIsDisplayed()

        verify(exactly = 1) { viewModel.loadGameDetails(gameId) }
        verify(exactly = 1) { viewModel.uiState }
    }

    @Test
    fun errorState() {
        val gameId = 1

        every { viewModel.uiState } returns MutableStateFlow(GameViewModel.GameScreenData.Error)

        var snackText = ""
        var snackRetry = ""

        composeTestRule.setContent {
            snackText = stringResource(id = R.string.game_screen_data_loading_error_msg)
            snackRetry = stringResource(id = R.string.game_screen_data_loading_error_retry)

            GameDealsTheme {
                GameScreen(
                    gameId = gameId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                )
            }
        }

        composeTestRule.onNodeWithText(snackText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(snackRetry)
            .assertIsDisplayed()

        verify(exactly = 1) { viewModel.loadGameDetails(gameId) }
        verify(exactly = 0) { viewModel.reloadGameDetails(gameId) }
        verify(exactly = 1) { viewModel.uiState }

        // Retry button clicked
        composeTestRule.onNodeWithText(snackRetry)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { viewModel.reloadGameDetails(gameId) }
    }


    @Test
    fun gameDetailsLoaded() {
        val gameId = 1

        every { viewModel.uiState } returns MutableStateFlow(
            GameViewModel.GameScreenData.Data(
                gameDetails = gameDetails,
                dealDetails = listOf(store to gameDeal)
            )
        )

        composeTestRule.setContent {
            GameDealsTheme {
                GameScreen(
                    gameId = gameId,
                    onBack = {},
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                )
            }
        }

        composeTestRule.onNodeWithTag(TopAppBarTag)
            .assertIsDisplayed()
            .onChildren()
            .filterToOne(hasTextExactly(gameTitle))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(GameDetailsTag)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(GameDetailsTitleTag)
            .assertIsDisplayed()
            .assertTextEquals(gameTitle)

        composeTestRule.onNodeWithTag(GameDealItemTag)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(GameDealItemStoreTitleLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(mockStoreName)

        verify(exactly = 1) { viewModel.loadGameDetails(gameId) }
        verify(exactly = 1) { viewModel.uiState }
    }

    @Test
    fun gameDetailsLoadedWide() {
        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)

        gameDetailsLoaded()
    }


    @Test
    fun onBackActioned() {
        val gameId = 1
        val onBack: () -> Unit = mockk()

        every { onBack.invoke() } just runs
        every { viewModel.uiState } returns MutableStateFlow(
            GameViewModel.GameScreenData.Data(
                gameDetails = gameDetails,
                dealDetails = listOf(store to gameDeal)
            )
        )

        composeTestRule.setContent {
            GameDealsTheme {
                GameScreen(
                    gameId = gameId,
                    onBack = onBack,
                    goToWeb = { _, _ -> },
                    viewModel = viewModel,
                )
            }
        }

        composeTestRule.onNodeWithTag(TopAppNavBarTag)
            .performClick()

        verify(exactly = 1) { viewModel.loadGameDetails(gameId) }
        verify(exactly = 1) { viewModel.uiState }
        verify(exactly = 1) { onBack.invoke() }
    }
}