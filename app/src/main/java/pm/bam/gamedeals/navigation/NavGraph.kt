package pm.bam.gamedeals.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pm.bam.gamedeals.feature.game.navigation.gameScreen
import pm.bam.gamedeals.feature.giveaways.navigation.giveawaysScreen
import pm.bam.gamedeals.feature.home.navigation.homeScreen
import pm.bam.gamedeals.feature.search.navigation.searchScreen
import pm.bam.gamedeals.feature.store.navigation.storeScreen
import pm.bam.gamedeals.feature.webview.navigation.webViewScreen

@Composable
internal fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestinations.HOME_SCREEN_ROUTE,
    navActions: NavigationActions = remember(navController) { NavigationActions(navController) }
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeScreen(
            route = NavigationDestinations.HOME_SCREEN_ROUTE,
            goToSearch = { navActions.navigateToSearch() },
            goToGame = { gameId -> navActions.navigateToGame(gameId) },
            goToStore = { storeId -> navActions.navigateToStore(storeId) },
            goToGiveaway = { navActions.navigateToGiveaways() },
            goToWeb = { url: String, gameTitle: String -> navActions.navigateToWeb(url, gameTitle) }
        )

        storeScreen(
            navController = navController,
            route = NavigationDestinations.STORE_ROUTE,
            storeIdArg = NavigationDestinationsArgs.STORE_ID_ARG,
            goToWeb = { url: String, gameTitle: String -> navActions.navigateToWeb(url, gameTitle) }
        )

        gameScreen(
            navController = navController,
            route = NavigationDestinations.GAME_ROUTE,
            gameIdArg = NavigationDestinationsArgs.GAME_ID_ARG,
            goToWeb = { url: String, gameTitle: String -> navActions.navigateToWeb(url, gameTitle) }
        )

        searchScreen(
            route = NavigationDestinations.SEARCH_ROUTE,
            goToGame = { gameId -> navActions.navigateToGame(gameId) }
        )

        webViewScreen(
            route = NavigationDestinations.WEBVIEW_ROUTE,
            urlArg = NavigationDestinationsArgs.WEB_URL_ARG,
            gameTitleArg = NavigationDestinationsArgs.WEB_GAME_TITLE_ARG,
            onBack = { navController.popBackStack() }
        )

        giveawaysScreen(
            navController = navController,
            route = NavigationDestinations.GIVEAWAYS_ROUTE,
            goToWeb = { url: String, gameTitle: String -> navActions.navigateToWeb(url, gameTitle) }
        )
    }
}