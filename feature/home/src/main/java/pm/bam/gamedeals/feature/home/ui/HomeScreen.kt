package pm.bam.gamedeals.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import pm.bam.gamedeals.common.ui.FoldableLandscape
import pm.bam.gamedeals.common.ui.FoldablePortrait
import pm.bam.gamedeals.common.ui.PhoneLandscape
import pm.bam.gamedeals.common.ui.PhonePortrait
import pm.bam.gamedeals.common.ui.PreviewDeal
import pm.bam.gamedeals.common.ui.PreviewStore
import pm.bam.gamedeals.common.ui.TabletLandscape
import pm.bam.gamedeals.common.ui.TabletPortrait
import pm.bam.gamedeals.common.ui.theme.GameDealsCustomTheme
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheet
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheetData
import pm.bam.gamedeals.feature.deal.ui.DealDetailsViewModel
import pm.bam.gamedeals.feature.home.R
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenListData.DealData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenListData.StoreData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenListData.ViewAllData
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenStatus.ERROR
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenStatus.LOADING
import pm.bam.gamedeals.feature.home.ui.HomeViewModel.HomeScreenStatus.SUCCESS

@Composable
internal fun HomeScreen(
    onSearch: () -> Unit,
    onViewStoreDeals: ((store: Store) -> Unit) = {},
    goToWeb: (url: String, gameTitle: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    dealDealDetailsViewModel: DealDetailsViewModel = hiltViewModel()
) {
    val data = viewModel.uiState.collectAsStateWithLifecycle()
    val dealDetails = dealDealDetailsViewModel.dealDealDetails.collectAsStateWithLifecycle()

    Screen(
        onSearch = onSearch,
        data = data.value,
        dealDetails = dealDetails.value,
        onViewDealDetails = { dealId, dealStoreId, dealTitle, dealPriceDenominated ->
            dealDealDetailsViewModel.loadDealDetails(
                dealId = dealId,
                dealStoreId = dealStoreId,
                dealTitle = dealTitle,
                dealPriceDenominated = dealPriceDenominated,
            )
        },
        onViewStoreDeals = onViewStoreDeals,
        onDismissDealDetails = { dealDealDetailsViewModel.dismissDealDetails() },
        goToWeb = goToWeb,
        onRetry = { viewModel.loadTopStoresDeals() }
    )
}

@Composable
private fun StoreDealRow(
    deal: Deal,
    onViewDealDetails: ((dealId: String, dealStoreId: Int, dealTitle: String, dealPriceDenominated: String) -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDealDetails(deal.dealID, deal.storeID, deal.title, deal.salePriceDenominated) }
            .padding(bottom = GameDealsCustomTheme.spacing.small)
            .testTag(HomeScreenDealRowTag.plus(deal.dealID)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = deal.thumb,
            contentDescription = stringResource(R.string.home_screen_game_image, deal.title),
            contentScale = ContentScale.Fit,
            error = painterResource(id = pm.bam.gamedeals.common.ui.R.drawable.videogame_thumb),
            modifier = Modifier
                .padding(horizontal = GameDealsCustomTheme.spacing.medium)
                .height(60.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(GameDealsCustomTheme.spacing.extraSmall))
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = GameDealsCustomTheme.spacing.small),
            textAlign = TextAlign.Start,
            text = deal.title
        )
        Text(
            modifier = Modifier.padding(horizontal = GameDealsCustomTheme.spacing.medium),
            style = TextStyle(textDecoration = TextDecoration.LineThrough),
            text = deal.normalPriceDenominated
        )
        Text(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.extraSmall)
                .padding(GameDealsCustomTheme.spacing.medium),
            text = deal.salePriceDenominated,
        )
        Spacer(Modifier.padding(GameDealsCustomTheme.spacing.small))
    }
}

@Composable
private fun Screen(
    onSearch: () -> Unit,
    data: HomeViewModel.HomeScreenData,
    dealDetails: DealBottomSheetData?,
    onViewDealDetails: (dealId: String, dealStoreId: Int, dealTitle: String, dealPriceDenominated: String) -> Unit,
    onViewStoreDeals: (store: Store) -> Unit,
    onDismissDealDetails: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    GameDealsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {}) {
                            when (data.state) {
                                LOADING -> CircularProgressIndicator(Modifier.testTag(HomeScreenLoadingTag))
                                ERROR -> Icon(Icons.Default.Warning, contentDescription = stringResource(R.string.home_screen_floating_search_icon))
                                SUCCESS -> Icon(
                                    modifier = Modifier.clickable { onSearch() },
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.home_screen_floating_search_icon)
                                )
                            }
                        }
                    }) { innerPadding: PaddingValues ->

                    LazyColumn(
                        modifier = Modifier.padding(innerPadding),
                        content = {
                            items(data.items.size) { index ->
                                when (val itemData = data.items[index]) {
                                    is StoreData -> StoreHeader(itemData.store)
                                    is DealData -> StoreDealRow(itemData.deal, onViewDealDetails)
                                    is ViewAllData -> Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth()
                                            .padding(top = GameDealsCustomTheme.spacing.medium, bottom = GameDealsCustomTheme.spacing.large)
                                            .testTag(HomeScreenViewAllButtonTag.plus(itemData.store.storeID)),
                                        onClick = { onViewStoreDeals(itemData.store) }) {
                                        Text(text = stringResource(R.string.home_screen_all_store_deals_label, itemData.store.storeName))
                                    }
                                }
                            }
                        }
                    )
                    DealBottomSheet(
                        data = dealDetails,
                        onDismiss = { onDismissDealDetails() },
                        goToWeb = goToWeb,
                        onRetryDealDetails = {
                            dealDetails?.let {
                                onViewDealDetails(
                                    it.dealId,
                                    it.store.storeID,
                                    it.gameName,
                                    it.gameSalesPriceDenominated
                                )
                            }
                        }
                    )
                }
            }

            if (data.state == ERROR) {
                LaunchedEffect(snackbarHostState) {
                    val results = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.home_screen_data_loading_error_msg),
                        actionLabel = context.getString(R.string.home_screen_data_loading_error_retry)
                    )
                    if (results == SnackbarResult.ActionPerformed) {
                        onRetry()
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreHeader(store: Store) {
    AsyncImage(
        model = store.images.banner,
        contentDescription = stringResource(R.string.home_screen_store_banner, store.storeName),
        contentScale = ContentScale.Fit,
        error = painterResource(id = pm.bam.gamedeals.common.ui.R.drawable.videogame_thumb),
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(GameDealsCustomTheme.spacing.extraSmall))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(GameDealsCustomTheme.spacing.medium)
            .testTag(HomeScreenStoreBannerTag.plus(store.storeID))
    )

    HorizontalDivider()
}


@PhonePortrait
@PhoneLandscape
@FoldablePortrait
@FoldableLandscape
@TabletPortrait
@TabletLandscape
@Composable
private fun ScreenPreview() {
    Screen(
        onSearch = {},
        data = HomeViewModel.HomeScreenData(
            state = SUCCESS,
            items = listOf(
                StoreData(store = PreviewStore.copy(storeID = 1)),
                DealData(deal = PreviewDeal),
                ViewAllData(store = PreviewStore.copy(storeID = 1)),
                StoreData(store = PreviewStore.copy(storeID = 1)),
                DealData(deal = PreviewDeal),
                DealData(deal = PreviewDeal),
                DealData(deal = PreviewDeal),
                ViewAllData(store = PreviewStore.copy(storeID = 1)),
                StoreData(store = PreviewStore.copy(storeID = 1)),
                DealData(deal = PreviewDeal),
                DealData(deal = PreviewDeal),
                ViewAllData(store = PreviewStore.copy(storeID = 1))
            )
        ),
        dealDetails = null,
        onViewDealDetails = { _, _, _, _ -> },
        onViewStoreDeals = {},
        onDismissDealDetails = {},
        goToWeb = { _, _ -> },
        onRetry = {}
    )
}


internal const val HomeScreenStoreBannerTag = "HomeScreenStoreBannerTag"
internal const val HomeScreenDealRowTag = "HomeScreenDealRowTag"
internal const val HomeScreenViewAllButtonTag = "HomeScreenViewAllButtonTag"
internal const val HomeScreenLoadingTag = "HomeScreenLoadingTag"