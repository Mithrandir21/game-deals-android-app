package pm.bam.gamedeals.feature.freetogame.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import pm.bam.gamedeals.feature.freetogame.ui.FreeGameScreen

fun NavGraphBuilder.freeGamesScreen(
    navController: NavController,
    route: String,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    composable(route = route) {
        FreeGameScreen(
            onBack = { navController.popBackStack() },
            goToWeb = goToWeb
        )
    }
}