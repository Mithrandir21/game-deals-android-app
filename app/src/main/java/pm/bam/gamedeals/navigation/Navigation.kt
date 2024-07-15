package pm.bam.gamedeals.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import pm.bam.gamedeals.navigation.NavigationDestinations.FREE_GAMES_ROUTE
import pm.bam.gamedeals.navigation.NavigationDestinations.GIVEAWAYS_ROUTE
import pm.bam.gamedeals.navigation.NavigationDestinations.HOME_SCREEN_ROUTE
import pm.bam.gamedeals.navigation.NavigationDestinations.SEARCH_ROUTE
import pm.bam.gamedeals.navigation.NavigationDestinationsArgs.GAME_ID_ARG
import pm.bam.gamedeals.navigation.NavigationDestinationsArgs.STORE_ID_ARG
import pm.bam.gamedeals.navigation.NavigationDestinationsArgs.WEB_GAME_TITLE_ARG
import pm.bam.gamedeals.navigation.NavigationDestinationsArgs.WEB_URL_ARG
import pm.bam.gamedeals.navigation.NavigationScreens.FREE_GAMES_SCREEN
import pm.bam.gamedeals.navigation.NavigationScreens.GAME_SCREEN
import pm.bam.gamedeals.navigation.NavigationScreens.GIVEAWAYS_SCREEN
import pm.bam.gamedeals.navigation.NavigationScreens.HOME_SCREEN
import pm.bam.gamedeals.navigation.NavigationScreens.SEARCH_SCREEN
import pm.bam.gamedeals.navigation.NavigationScreens.STORE_SCREEN
import pm.bam.gamedeals.navigation.NavigationScreens.WEBVIEW_SCREEN


/** Screens used in [NavigationDestinations]. */
private object NavigationScreens {
    const val HOME_SCREEN = "home"
    const val STORE_SCREEN = "store"
    const val GAME_SCREEN = "game"
    const val SEARCH_SCREEN = "search"
    const val WEBVIEW_SCREEN = "webview"
    const val GIVEAWAYS_SCREEN = "giveaways"
    const val FREE_GAMES_SCREEN = "freegames"
}

/** Arguments used in [NavigationDestinations] routes. */
internal object NavigationDestinationsArgs {
    const val STORE_ID_ARG = "storeId"
    const val GAME_ID_ARG = "gameId"

    const val WEB_URL_ARG = "url"
    const val WEB_GAME_TITLE_ARG = "gameTitle"
}


/** Possible destinations used in this Navigation graph. */
internal object NavigationDestinations {
    const val HOME_SCREEN_ROUTE = HOME_SCREEN
    const val STORE_ROUTE = "$STORE_SCREEN?$STORE_ID_ARG={$STORE_ID_ARG}"
    const val GAME_ROUTE = "$GAME_SCREEN?$GAME_ID_ARG={$GAME_ID_ARG}"
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val WEBVIEW_ROUTE = "$WEBVIEW_SCREEN?$WEB_URL_ARG={$WEB_URL_ARG}&$WEB_GAME_TITLE_ARG={$WEB_GAME_TITLE_ARG}"
    const val GIVEAWAYS_ROUTE = GIVEAWAYS_SCREEN
    const val FREE_GAMES_ROUTE = FREE_GAMES_SCREEN
}


/**
 * Models the navigation actions in the app.
 */
internal class NavigationActions(private val navController: NavHostController) {

    fun navigateHome() {
        navController.navigate(HOME_SCREEN_ROUTE) {
            // Pop up to the start destination of the graph to avoid building up a large stack of destinations on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToStore(storeId: Int) {
        navController.navigate("$STORE_SCREEN?$STORE_ID_ARG=${storeId}") {
            restoreState = storeId == 0
        }
    }

    fun navigateToGame(gameId: Int) {
        navController.navigate("$GAME_SCREEN?$GAME_ID_ARG=${gameId}") {
            restoreState = gameId == 0
        }
    }

    fun navigateToSearch() {
        navController.navigate(SEARCH_ROUTE) {
            restoreState = true
        }
    }

    fun navigateToWeb(url: String, gameTitle: String) {
        navController.navigate("$WEBVIEW_SCREEN?$WEB_URL_ARG=${url}&$WEB_GAME_TITLE_ARG=${gameTitle}") {
            restoreState = true
        }
    }

    fun navigateToGiveaways() {
        navController.navigate(GIVEAWAYS_ROUTE) {
            restoreState = true
        }
    }

    fun navigateToFreeGames() {
        navController.navigate(FREE_GAMES_ROUTE) {
            restoreState = true
        }
    }

}
