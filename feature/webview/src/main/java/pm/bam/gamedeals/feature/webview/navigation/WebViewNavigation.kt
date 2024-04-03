package pm.bam.gamedeals.feature.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pm.bam.gamedeals.feature.webview.ui.WebView

fun NavGraphBuilder.webViewScreen(
    route: String,
    urlArg: String,
    gameTitleArg: String,
    onBack: () -> Unit
) {
    composable(
        route = route,
        arguments = listOf(navArgument(urlArg) { type = NavType.StringType }, navArgument(gameTitleArg) { type = NavType.StringType })
    ) { entry ->
        WebView(
            url = entry.arguments?.getString(urlArg)!!,
            gameTitle = entry.arguments?.getString(gameTitleArg)!!,
            onBack = onBack
        )
    }
}