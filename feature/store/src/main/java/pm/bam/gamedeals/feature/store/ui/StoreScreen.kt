package pm.bam.gamedeals.feature.store.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.flowOf
import pm.bam.gamedeals.common.ui.FoldableLandscape
import pm.bam.gamedeals.common.ui.FoldablePortrait
import pm.bam.gamedeals.common.ui.PhoneLandscape
import pm.bam.gamedeals.common.ui.PhonePortrait
import pm.bam.gamedeals.common.ui.PreviewDeal
import pm.bam.gamedeals.common.ui.PreviewStore
import pm.bam.gamedeals.common.ui.TabletLandscape
import pm.bam.gamedeals.common.ui.TabletPortrait
import pm.bam.gamedeals.common.ui.theme.GameDealsCustomTheme
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheet
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheetData
import pm.bam.gamedeals.feature.deal.ui.DealDetailsViewModel
import pm.bam.gamedeals.feature.store.R

@Composable
internal fun StoreScreen(
    storeId: Int,
    onBack: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit,
    viewModel: StoreViewModel = hiltViewModel(),
    dealDealDetailsViewModel: DealDetailsViewModel = hiltViewModel()
) {
    viewModel.setStoreId(storeId)

    val deals: LazyPagingItems<Deal> = viewModel.deals.collectAsLazyPagingItems()
    val storeDetails = viewModel.storeDetails.collectAsStateWithLifecycle()

    val dealDetails = dealDealDetailsViewModel.dealDealDetails.collectAsStateWithLifecycle()

    StoreDeals(
        deals = deals,
        dealDetails = dealDetails.value,
        storeDetails = storeDetails.value,
        onBack = onBack,
        onLoadDealDetails = { dealId, dealStoreId, dealTitle, dealPriceDenominated ->
            dealDealDetailsViewModel.loadDealDetails(
                dealId = dealId,
                dealStoreId = dealStoreId,
                dealTitle = dealTitle,
                dealPriceDenominated = dealPriceDenominated,
            )
        },
        onDismissDealDetails = { dealDealDetailsViewModel.dismissDealDetails() },
        goToWeb = goToWeb
    )
}

@Composable
private fun DealRow(
    deal: Deal,
    onViewDealDetails: ((deal: Deal) -> Unit)
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewDealDetails(deal) }
                .padding(
                    start = GameDealsCustomTheme.spacing.small,
                    top = GameDealsCustomTheme.spacing.extraSmall,
                    end = GameDealsCustomTheme.spacing.medium,
                    bottom = GameDealsCustomTheme.spacing.extraSmall
                )
                .testTag(DealRowTag.plus(deal.dealID)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = deal.thumb,
                contentDescription = stringResource(R.string.store_screen_game_image, deal.title),
                error = painterResource(id = pm.bam.gamedeals.common.ui.R.drawable.videogame_thumb),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(60.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(GameDealsCustomTheme.spacing.extraSmall))
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = GameDealsCustomTheme.spacing.medium),
                textAlign = TextAlign.Start,
                text = deal.title
            )
            Text(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(GameDealsCustomTheme.spacing.extraSmall))
                    .padding(GameDealsCustomTheme.spacing.medium),
                text = deal.salePriceDenominated,
            )
        }
    }
}

@Composable
private fun StoreDeals(
    deals: LazyPagingItems<Deal>,
    dealDetails: DealBottomSheetData? = null,
    storeDetails: Store? = null,
    onBack: () -> Unit,
    onLoadDealDetails: (dealId: String, dealStoreId: Int, dealTitle: String, dealPriceDenominated: String) -> Unit,
    onDismissDealDetails: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = { StoreToolbar(onBack, storeDetails) }
    ) { innerPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(GameDealsCustomTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.medium),
        ) {
            items(
                count = deals.itemCount,
                key = deals.itemKey { it.dealID }
            ) { index: Int ->
                deals[index]?.let { deal ->
                    DealRow(deal) { onLoadDealDetails(deal.dealID, deal.storeID, deal.title, deal.salePriceDenominated) }
                }
            }
            item {
                if (deals.loadState.append is LoadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(GameDealsCustomTheme.spacing.large)
                            .testTag(LoadingRowTag)
                    )
                }
            }
        }

        DealBottomSheet(
            data = dealDetails,
            goToWeb = goToWeb,
            onDismiss = { onDismissDealDetails() },
            onRetryDealDetails = { dealDetails?.let { onLoadDealDetails(it.dealId, it.store.storeID, it.gameName, it.gameSalesPriceDenominated) } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreToolbar(
    onBack: () -> Unit,
    storeDetails: Store? = null
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(StoreTopBarTag),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = storeDetails?.images?.banner,
                    contentDescription = stringResource(R.string.store_screen_store_banner, storeDetails?.storeName ?: ""),
                    error = painterResource(id = pm.bam.gamedeals.common.ui.R.drawable.videogame_thumb),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(60.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(GameDealsCustomTheme.spacing.extraSmall))
                )
                Text(
                    modifier = Modifier.padding(horizontal = GameDealsCustomTheme.spacing.medium),
                    text = storeDetails?.storeName ?: "",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier.testTag(TopBarNavTag),
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.store_screen_navigation_back_icon)
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}


@PhonePortrait
@PhoneLandscape
@FoldablePortrait
@FoldableLandscape
@TabletPortrait
@TabletLandscape
@Composable
private fun Preview() {
    val items = List(6) { PreviewDeal.copy(dealID = PreviewDeal.dealID.plus("-$it")) }
    val previewData = flowOf(PagingData.from(items)).collectAsLazyPagingItems()

    StoreDeals(
        deals = previewData,
        dealDetails = null,
        storeDetails = PreviewStore,
        onBack = {},
        onLoadDealDetails = { _, _, _, _ -> },
        onDismissDealDetails = {},
        goToWeb = { _, _ -> }
    )
}


internal const val StoreTopBarTag = "StoreTopBar"
internal const val TopBarNavTag = "TopBarNav"
internal const val LoadingRowTag = "LoadingRow"
internal const val DealRowTag = "DealRow"