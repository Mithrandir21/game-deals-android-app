package pm.bam.gamedeals.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.feature.home.ui.HomeScreen

fun NavGraphBuilder.homeScreen(
    route: String,
    goToSearch: () -> Unit,
    goToStore: (storeId: Int) -> Unit,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    composable(route) {
        HomeScreen(
            onSearch = { goToSearch() },
            onViewStoreDeals = { store: Store -> goToStore(store.storeID) },
            goToWeb = { url: String, gameTitle: String -> goToWeb(url, gameTitle) }
        )
    }
}