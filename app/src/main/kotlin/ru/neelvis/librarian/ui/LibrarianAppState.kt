package ru.neelvis.librarian.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import ru.neelvis.librarian.feature.add_books.navigation.navigateToAddBook
import ru.neelvis.librarian.feature.dashboard.navigation.navigateToDashboard
import ru.neelvis.librarian.feature.notification.navigation.navigateToNotification
import ru.neelvis.librarian.feature.profile.navigation.navigateToProfile
import ru.neelvis.librarian.feature.search.navigation.navigateToSearch
import ru.neelvis.librarian.nav.TopLevelDestination

@Composable
fun rememberLibrarianAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): LibrarianAppState {
    return remember(navController, coroutineScope) {
        LibrarianAppState(navController, coroutineScope)
    }
}

@Stable
class LibrarianAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = false
        }
        when (topLevelDestination) {
            TopLevelDestination.DASHBOARD -> navController.navigateToDashboard(navOptions = topLevelNavOptions)
            TopLevelDestination.PROFILE -> navController.navigateToProfile(navOptions = topLevelNavOptions)
            TopLevelDestination.ADDBOOK -> navController.navigateToAddBook(navOptions = topLevelNavOptions)
            TopLevelDestination.SEARCH -> navController.navigateToSearch(navOptions = topLevelNavOptions)
            TopLevelDestination.NOTIFICATION -> navController.navigateToNotification(navOptions = topLevelNavOptions)
        }
    }
}