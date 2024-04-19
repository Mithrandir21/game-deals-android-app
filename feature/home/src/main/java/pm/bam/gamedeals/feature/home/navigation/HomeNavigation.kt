package pm.bam.gamedeals.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.home.ui.HomeScreen

fun NavGraphBuilder.homeScreen(
    route: String,
    goToSearch: () -> Unit,
    goToGame: (gameId: Int) -> Unit,
    goToStore: (storeId: Int) -> Unit,
    goToGiveaway: () -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    composable(route) {
        HomeScreen(
            onSearch = goToSearch,
            goToGame = goToGame,
            onViewStoreDeals = { store: Store -> goToStore(store.storeID) },
            onViewGiveaways = goToGiveaway,
            goToWeb = { url: String, gameTitle: String -> goToWeb(url, gameTitle) }
        )
    }
}