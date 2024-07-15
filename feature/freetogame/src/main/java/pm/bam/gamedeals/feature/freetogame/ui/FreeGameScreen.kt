package pm.bam.gamedeals.feature.freetogame.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import pm.bam.gamedeals.common.ui.PhonePortrait
import pm.bam.gamedeals.common.ui.PreviewFreeGame
import pm.bam.gamedeals.common.ui.theme.GameDealsCustomTheme
import pm.bam.gamedeals.common.ui.theme.GameDealsTheme
import pm.bam.gamedeals.domain.models.FreeGame
import pm.bam.gamedeals.domain.models.FreeGameSearchParameters
import pm.bam.gamedeals.domain.models.FreeGameSortBy
import pm.bam.gamedeals.feature.freetogame.R

@Composable
internal fun FreeGameScreen(
    onBack: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit,
    viewModel: FreeGameViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    var showFilters by rememberSaveable { mutableStateOf(false) }
    var existingParameters by rememberSaveable(stateSaver = parametersSaver) { mutableStateOf(FreeGameSearchParameters()) }


    ScreenScaffold(
        data = uiState.value,
        onBack = onBack,
        onReload = { viewModel.reloadFreeGames() },
        goToWeb = goToWeb,
        existingParameters = existingParameters,
        showFilters = showFilters,
        onShowFiltersChanged = { newShowFilters ->
            showFilters = newShowFilters
            if (!newShowFilters) {
                viewModel.loadFreeGames(existingParameters)
            }
        },
        onPlatformSelection = { platform, selection ->
            existingParameters = existingParameters.copy(
                platforms = existingParameters.platforms.toMutableList().map { if (it.first == platform) platform to selection else it })
        },
        onGenreSelection = { genre, selection ->
            existingParameters = existingParameters.copy(
                genres = existingParameters.genres.toMutableList().map { if (it.first == genre) genre to selection else it })
        },
        onSortBySelection = { sortBy ->
            existingParameters = existingParameters.copy(sortBy = sortBy)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenScaffold(
    data: FreeGameViewModel.FreeGameScreenData,
    onBack: () -> Unit,
    onReload: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit,
    existingParameters: FreeGameSearchParameters,
    showFilters: Boolean,
    onShowFiltersChanged: (showFilters: Boolean) -> Unit,
    onPlatformSelection: (platform: String, selection: Boolean) -> Unit,
    onGenreSelection: (genre: String, selection: Boolean) -> Unit,
    onSortBySelection: (sortBy: FreeGameSortBy) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
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
                        title = { Text(text = "FreeGames", maxLines = 2, overflow = TextOverflow.Ellipsis) },
                        navigationIcon = {
                            IconButton(
                                modifier = Modifier.testTag(TopAppNavBarTag),
                                onClick = { onBack() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.free_to_game_screen_navigation_back_button)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { onShowFiltersChanged(!showFilters) }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    stringResource(R.string.free_to_game_screen_filters_icon),
                                    modifier = Modifier.testTag(FreeGameFiltersIconTag)
                                )
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding: PaddingValues ->
                when (data.status) {
                    FreeGameViewModel.FreeGameScreenStatus.LOADING -> CircularProgressIndicator(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .testTag(LoadingDataTag)
                    )

                    FreeGameViewModel.FreeGameScreenStatus.SUCCESS -> LazyColumn(
                        state = scrollState,
                        modifier = Modifier.padding(innerPadding),
                        content = {
                            items(count = data.freeGames.size) {
                                FreeGameListItem(data.freeGames[it]) { goToWeb(data.freeGames[it].gameUrl, data.freeGames[it].title) }
                            }
                        }
                    )

                    FreeGameViewModel.FreeGameScreenStatus.ERROR -> LaunchedEffect(snackbarHostState) {
                        val results = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.free_to_game_screen_data_loading_error_msg),
                            actionLabel = context.getString(R.string.free_to_game_screen_data_loading_error_retry)
                        )
                        if (results == SnackbarResult.ActionPerformed) {
                            onReload()
                        }
                    }
                }

                FreeGameFilters(
                    existingParameters = existingParameters,
                    showFilters = showFilters,
                    onDismiss = { onShowFiltersChanged(false) },
                    onPlatformSelection = onPlatformSelection,
                    onGenreSelection = onGenreSelection,
                    onSortBySelection = onSortBySelection
                )
            }
        }
    }
}

@Composable
private fun FreeGameListItem(
    freeGame: FreeGame,
    onFreeGame: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .clickable { onFreeGame() }
            .fillMaxWidth()
            .padding(horizontal = GameDealsCustomTheme.spacing.large, vertical = GameDealsCustomTheme.spacing.small)
            .testTag(FreeGameListItemTag.plus(freeGame.id)),
        headlineContent = { Text(freeGame.title) },
        leadingContent = {
            AsyncImage(
                model = freeGame.thumbnail,
                contentDescription = stringResource(R.string.free_to_game_screen_game_image, freeGame.title),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FreeGameFilters(
    existingParameters: FreeGameSearchParameters,
    showFilters: Boolean,
    onDismiss: () -> Unit,
    onPlatformSelection: (platform: String, selection: Boolean) -> Unit,
    onGenreSelection: (genre: String, selection: Boolean) -> Unit,
    onSortBySelection: (sortBy: FreeGameSortBy) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showFilters) {
        ModalBottomSheet(
            modifier = Modifier.testTag(FreeGameFiltersTag),
            onDismissRequest = { onDismiss() },
            sheetState = modalBottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Filters(existingParameters, onPlatformSelection, onGenreSelection, onSortBySelection)
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Filters(
    existingParameters: FreeGameSearchParameters,
    onPlatformSelection: (platform: String, selection: Boolean) -> Unit,
    onGenreSelection: (genre: String, selection: Boolean) -> Unit,
    onSortBySelection: (sortBy: FreeGameSortBy) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(GameDealsCustomTheme.spacing.large)
            .navigationBarsPadding()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = GameDealsCustomTheme.spacing.medium),
            text = stringResource(R.string.free_to_game_screen_filters_platform_label)
        )
        FlowRow(
            modifier = Modifier.padding(GameDealsCustomTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.extraSmall)
        ) {
            existingParameters.platforms.forEach { (platform, selected) ->
                FilterChip(selected = selected, onClick = { onPlatformSelection(platform, !selected) }, label = {
                    Text(
                        text = platform,
                        modifier = Modifier.padding(GameDealsCustomTheme.spacing.extraSmall),
                        style = MaterialTheme.typography.bodyMedium
                    )
                })
            }
        }

        HorizontalDivider()

        Text(
            modifier = Modifier.padding(GameDealsCustomTheme.spacing.medium),
            text = stringResource(R.string.free_to_game_screen_filters_genre_label)
        )
        FlowRow(
            modifier = Modifier.padding(horizontal = GameDealsCustomTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.extraSmall)
        ) {
            existingParameters.genres.forEach { (genre, selected) ->
                FilterChip(selected = selected, onClick = { onGenreSelection(genre, !selected) }, label = {
                    Text(
                        text = genre,
                        modifier = Modifier.padding(GameDealsCustomTheme.spacing.extraSmall),
                        style = MaterialTheme.typography.bodyMedium
                    )
                })
            }
        }

        HorizontalDivider()

        Text(
            modifier = Modifier.padding(GameDealsCustomTheme.spacing.medium),
            text = stringResource(R.string.free_to_game_screen_filters_sort_by_label)
        )
        FreeGameSortByOptions(existingParameters, onSortBySelection)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FreeGameSortByOptions(
    existingParameters: FreeGameSearchParameters,
    onSortBySelection: (sortBy: FreeGameSortBy) -> Unit
) {
    FlowRow(
        modifier = Modifier.padding(horizontal = GameDealsCustomTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(GameDealsCustomTheme.spacing.extraSmall)
    ) {
        FreeGameSortBy.entries
            .map {
                when (it) {
                    existingParameters.sortBy -> it to true
                    else -> it to false
                }
            }
            .forEach { (sortBy, selected) ->
                FilterChip(
                    label = {
                        Text(
                            modifier = Modifier
                                .padding(GameDealsCustomTheme.spacing.extraSmall)
                                .testTag(FreeGameFiltersSortTag.plus(sortBy.name)),
                            text = sortBy.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    selected = selected,
                    onClick = { onSortBySelection(sortBy) })
            }
    }
}


/** Saving mechanism for [FreeGameSearchParameters] into [rememberSaveable]. */
private val parametersSaver = run {
    mapSaver(
        save = { it.asMap() },
        restore = { FreeGameSearchParameters.from(it) }
    )
}


@Preview
@Composable
private fun SortOptionsPreview() {
    GameDealsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FreeGameSortByOptions(
                existingParameters = FreeGameSearchParameters(),
                onSortBySelection = { }
            )
        }
    }
}


@Preview
@Composable
private fun FiltersPreview() {
    GameDealsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Filters(
                existingParameters = FreeGameSearchParameters(),
                onPlatformSelection = { _, _ -> },
                onGenreSelection = { _, _ -> },
                onSortBySelection = { }
            )
        }
    }
}

@PhonePortrait
@Composable
private fun PreviewLoading() {
    ScreenScaffold(
        data = FreeGameViewModel.FreeGameScreenData(status = FreeGameViewModel.FreeGameScreenStatus.LOADING),
        onBack = {},
        goToWeb = { _, _ -> },
        onReload = {},
        existingParameters = FreeGameSearchParameters(),
        showFilters = false,
        onShowFiltersChanged = {},
        onPlatformSelection = { _, _ -> },
        onGenreSelection = { _, _ -> },
        onSortBySelection = {}
    )
}

@PhonePortrait
@Composable
private fun PreviewData() {
    ScreenScaffold(
        data = FreeGameViewModel.FreeGameScreenData(
            status = FreeGameViewModel.FreeGameScreenStatus.SUCCESS,
            freeGames = listOf(
                PreviewFreeGame.copy(id = 1),
                PreviewFreeGame.copy(id = 2),
                PreviewFreeGame.copy(id = 3),
                PreviewFreeGame.copy(id = 4),
                PreviewFreeGame.copy(id = 5),
            )
        ),
        onBack = {},
        goToWeb = { _, _ -> },
        onReload = {},
        existingParameters = FreeGameSearchParameters(),
        showFilters = false,
        onShowFiltersChanged = {},
        onPlatformSelection = { _, _ -> },
        onGenreSelection = { _, _ -> },
        onSortBySelection = {}
    )
}


internal const val TopAppBarTag = "TopAppBarTag"
internal const val TopAppNavBarTag = "TopAppNavBarTag"
internal const val LoadingDataTag = "LoadingDataTag"

internal const val FreeGameListItemTag = "FreeGameListItemTag"

internal const val FreeGameFiltersTag = "FreeGameFiltersTag"
internal const val FreeGameFiltersIconTag = "FreeGameFiltersIconTag"
internal const val FreeGameFiltersSortTag = "FreeGameFiltersSortTag"