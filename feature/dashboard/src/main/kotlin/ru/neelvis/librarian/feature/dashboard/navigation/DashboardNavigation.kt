package ru.neelvis.librarian.feature.dashboard.navigation

import DashboardScreen
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import ru.neelvis.librarian.feature.dashboard.BookInfoScreen
import ru.neelvis.librarian.feature.dashboard.viewmodels.DashboardViewModel

@Serializable
object DashboardRoute

@Serializable
object DashboardBaseRoute

@Serializable
object BookInfoRoute

fun NavController.navigateToDashboard(navOptions: NavOptions) =
    navigate(route = DashboardRoute, navOptions)

fun NavController.navigateToBookInfo() =
    navigate(route = BookInfoRoute)

fun NavGraphBuilder.dashboardSection(navController: NavController) {

    navigation<DashboardBaseRoute>(startDestination = DashboardRoute) {
        composable<DashboardRoute> {
            DashboardScreen(navigateOnBookSelected = navController::navigateToBookInfo)
        }
        composable<BookInfoRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(DashboardRoute)
            }
            BookInfoScreen(
                hiltViewModel<DashboardViewModel>(parentEntry),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
