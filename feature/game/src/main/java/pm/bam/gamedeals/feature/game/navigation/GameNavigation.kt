package pm.bam.gamedeals.feature.game.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pm.bam.gamedeals.feature.game.ui.GameScreen

fun NavGraphBuilder.gameScreen(
    navController: NavController,
    route: String,
    gameIdArg: String,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    composable(
        route = route,
        arguments = listOf(navArgument(gameIdArg) { type = NavType.IntType })
    ) { entry ->
        GameScreen(
            gameId = entry.arguments?.getInt(gameIdArg)!!,
            onBack = { navController.popBackStack() },
            goToWeb = goToWeb
        )
    }
}