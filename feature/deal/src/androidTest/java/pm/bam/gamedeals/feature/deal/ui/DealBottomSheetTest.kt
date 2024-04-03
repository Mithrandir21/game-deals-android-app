package pm.bam.gamedeals.feature.deal.ui

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.domain.models.DealDetails
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.deal.R

class DealBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun loadingScreen() {
        val storeName = "Store Name"
        val store: Store = mockk {
            every { this@mockk.images.logo } returns "Logo"
            every { this@mockk.storeName } returns storeName
        }
        val dealId = "Deal ID"
        val gameName = "Game Name"
        val gamePrice = "Game Price"

        val loadingData = DealBottomSheetData.DealDetailsLoading(
            store = store,
            gameName = gameName,
            dealId = dealId,
            gameSalesPriceDenominated = gamePrice
        )

        var expectedGameData = ""

        composeTestRule.setContent {
            expectedGameData = stringResource(id = R.string.deal_details_title_label, storeName, gamePrice)

            GameDealsTheme {
                DealBottomSheet(
                    data = loadingData,
                    onDismiss = {},
                    goToWeb = { _, _ -> }
                )
            }
        }


        composeTestRule.onNodeWithTag(StoreDataGameDataTag)
            .assert(hasTextExactly(expectedGameData))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(StoreDataGameNameTag)
            .assert(hasTextExactly(gameName))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(DataLoadingTag)
            .assertIsDisplayed()
    }


    @Test
    fun dataScreen() {
        val storeId = 1
        val storeName = "Store Name"
        val storeLogo = "Logo"
        val store: Store = mockk {
            every { this@mockk.storeID } returns storeId
            every { this@mockk.images.logo } returns storeLogo
            every { this@mockk.storeName } returns storeName
        }
        val dealId = "Deal ID"
        val gameName = "Game Name"
        val gamePrice = "Game Price"

        val gameInfoThumb = "Thumb"
        val gameInfoName = "Name"
        val gameInfoScore = 1
        val gameInfoPercentage = 2
        val gameInfoReleaseDate = "Thumb"
        val gameInfoSteamId = 3
        val gameInfoSteamworks = true
        val gameInfo: DealDetails.GameInfo = mockk {
            every { this@mockk.thumb } returns gameInfoThumb
            every { this@mockk.name } returns gameInfoName
            every { this@mockk.metacriticScore } returns gameInfoScore
            every { this@mockk.steamRatingPercent } returns gameInfoPercentage
            every { this@mockk.releaseDate } returns gameInfoReleaseDate
            every { this@mockk.steamAppID } returns gameInfoSteamId
            every { this@mockk.steamworks } returns gameInfoSteamworks
        }

        val salePriceDenominated = "Cheaper"
        val cheaperStoreDetails: DealDetails.CheaperStore = mockk {
            every { this@mockk.salePriceDenominated } returns salePriceDenominated
        }

        val cheapestPriceDenominated = "Cheapest Price"
        val cheapestPriceDate = "Cheapest Date"
        val cheapestPrice: DealDetails.CheapestPrice = mockk {
            every { this@mockk.priceDenominated } returns cheapestPriceDenominated
            every { this@mockk.date } returns cheapestPriceDate
        }


        val dealDetailsData = DealBottomSheetData.DealDetailsData(
            store = store,
            gameName = gameName,
            dealId = dealId,
            gameSalesPriceDenominated = gamePrice,
            gameInfo = gameInfo,
            cheaperStores = listOf(store to cheaperStoreDetails),
            cheapestPrice = cheapestPrice,
        )

        var expectedGameData = ""
        var expectedCheapestStore = ""
        var expectedCheapestPrice = ""

        composeTestRule.setContent {
            expectedGameData = stringResource(id = R.string.deal_details_title_label, storeName, gamePrice)
            expectedCheapestStore = stringResource(id = R.string.deal_details_cheapest_store_label)
                .plus(stringResource(id = R.string.deal_details_cheapest_no))
            expectedCheapestPrice = stringResource(id = R.string.deal_details_cheapest_on_label, cheapestPriceDenominated, cheapestPriceDate)

            GameDealsTheme {
                DealBottomSheet(
                    data = dealDetailsData,
                    onDismiss = {},
                    goToWeb = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithTag(StoreDataGameDataTag)
            .assert(hasTextExactly(expectedGameData))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(StoreDataGameNameTag)
            .assert(hasTextExactly(gameName))
            .assertIsDisplayed()

        // Loading not being shown
        composeTestRule.onNodeWithTag(DataLoadingTag)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag(DealCheapestTag)
            .assert(hasTextExactly(expectedCheapestStore))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(CheapestPriceTag)
            .assert(hasTextExactly(expectedCheapestPrice))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(DealCheaperStoreRowTag.plus(store.storeID))
            .onChildren()
            .filterToOne(hasTextExactly(salePriceDenominated))
            .assertIsDisplayed()
    }
}