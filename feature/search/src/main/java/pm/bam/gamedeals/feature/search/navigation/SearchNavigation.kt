package pm.bam.gamedeals.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import pm.bam.gamedeals.feature.search.ui.SearchScreen

fun NavGraphBuilder.searchScreen(
    route: String,
    goToGame: (gameId: Int) -> Unit
) {
    composable(route) {
        SearchScreen(
            onSearchedGame = { gameId: Int -> goToGame(gameId) }
        )
    }
}