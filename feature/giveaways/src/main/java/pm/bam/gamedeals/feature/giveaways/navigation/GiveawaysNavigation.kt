package pm.bam.gamedeals.feature.giveaways.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import pm.bam.gamedeals.feature.giveaways.ui.GiveawaysScreen

fun NavGraphBuilder.giveawaysScreen(
    navController: NavController,
    route: String,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    composable(route = route) {
        GiveawaysScreen(
            onBack = { navController.popBackStack() },
            goToWeb = goToWeb
        )
    }
}