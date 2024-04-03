package pm.bam.gamedeals.feature.store.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pm.bam.gamedeals.feature.store.ui.StoreScreen

fun NavGraphBuilder.storeScreen(
    navController: NavController,
    route: String,
    storeIdArg: String,
    goToWeb: (url: String, gameTitle: String) -> Unit
) {
    composable(
        route = route,
        arguments = listOf(navArgument(storeIdArg) { type = NavType.IntType })
    ) { entry ->
        StoreScreen(
            storeId = entry.arguments?.getInt(storeIdArg)!!,
            onBack = { navController.popBackStack() },
            goToWeb = goToWeb
        )
    }
}