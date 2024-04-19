package pm.bam.gamedeals.feature.giveaways.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import pm.bam.gamedeals.common.ui.PhonePortrait
import pm.bam.gamedeals.common.ui.PreviewGiveaway
import pm.bam.gamedeals.common.ui.theme.GameDealsCustomTheme
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.feature.giveaways.R

@Composable
internal fun GiveawaysScreen(
    onBack: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit,
    viewModel: GiveawaysViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ScreenScaffold(
        data = uiState.value,
        onBack = onBack,
        onReload = { viewModel.reloadGiveaways() },
        goToWeb = goToWeb
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenScaffold(
    data: GiveawaysViewModel.GiveawaysScreenData,
    onBack: () -> Unit,
    onReload: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    GameDealsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        modifier = Modifier.testTag(TopAppBarTag),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(text = "Giveaways", maxLines = 2, overflow = TextOverflow.Ellipsis) },
                        navigationIcon = {
                            IconButton(
                                modifier = Modifier.testTag(TopAppNavBarTag),
                                onClick = { onBack() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.giveaway_screen_navigation_back_button)
                                )
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding: PaddingValues ->
                when (data.status) {
                    GiveawaysViewModel.GiveawaysScreenStatus.LOADING -> CircularProgressIndicator(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .testTag(LoadingDataTag)
                    )

                    GiveawaysViewModel.GiveawaysScreenStatus.SUCCESS -> LazyColumn(
                        modifier = Modifier.padding(innerPadding),
                        content = {
                            items(
                                key = { index -> data.giveaways[index].id },
                                count = data.giveaways.size
                            ) {
                                GiveawayListItem(data.giveaways[it]) { goToWeb(data.giveaways[it].gamerpowerUrl, data.giveaways[it].title) }
                            }
                        }
                    )

                    GiveawaysViewModel.GiveawaysScreenStatus.ERROR -> LaunchedEffect(snackbarHostState) {
                        val results = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.giveaway_screen_data_loading_error_msg),
                            actionLabel = context.getString(R.string.giveaway_screen_data_loading_error_retry)
                        )
                        if (results == SnackbarResult.ActionPerformed) {
                            onReload()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GiveawayListItem(
    giveaway: Giveaway,
    onGiveaway: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .clickable { onGiveaway() }
            .fillMaxWidth()
            .padding(horizontal = GameDealsCustomTheme.spacing.large, vertical = GameDealsCustomTheme.spacing.small)
            .testTag(GiveawayListItemTag.plus(giveaway.id)),
        headlineContent = { Text(giveaway.title) },
        supportingContent = {
            giveaway.worth?.let {
                Text(text = buildAnnotatedString {
                    withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                        append(stringResource(id = R.string.giveaway_screen_list_item_free_label))
                    }
                    append(" ")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append(stringResource(id = R.string.giveaway_screen_list_item_worth_label, it))
                    }
                })
            } ?: Text(stringResource(id = R.string.giveaway_screen_list_item_free_label))
        },
        leadingContent = {
            AsyncImage(
                model = giveaway.thumbnail,
                contentDescription = stringResource(R.string.giveaway_screen_game_image, giveaway.title),
                error = painterResource(id = pm.bam.gamedeals.common.ui.R.drawable.videogame_thumb),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(60.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(GameDealsCustomTheme.spacing.extraSmall))
            )
        }
    )
    HorizontalDivider(color = Color.Black)
}


@PhonePortrait
@Composable
private fun PreviewLoading() {
    ScreenScaffold(
        data = GiveawaysViewModel.GiveawaysScreenData(status = GiveawaysViewModel.GiveawaysScreenStatus.LOADING),
        onBack = {},
        goToWeb = { _, _ -> },
        onReload = {}
    )
}

@PhonePortrait
@Composable
private fun PreviewData() {
    ScreenScaffold(
        data = GiveawaysViewModel.GiveawaysScreenData(
            status = GiveawaysViewModel.GiveawaysScreenStatus.SUCCESS,
            giveaways = listOf(
                PreviewGiveaway.copy(id = 1),
                PreviewGiveaway.copy(id = 2),
                PreviewGiveaway.copy(id = 3),
                PreviewGiveaway.copy(id = 4),
                PreviewGiveaway.copy(id = 5)
            )
        ),
        onBack = {},
        goToWeb = { _, _ -> },
        onReload = {}
    )
}


internal const val TopAppBarTag = "TopAppBarTag"
internal const val TopAppNavBarTag = "TopAppNavBarTag"
internal const val LoadingDataTag = "LoadingDataTag"

internal const val GiveawayListItemTag = "GiveawayListItemTag"