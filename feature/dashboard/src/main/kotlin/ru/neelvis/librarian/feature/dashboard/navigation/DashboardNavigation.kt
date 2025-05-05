package ru.neelvis.librarian.feature.dashboard.navigation

import DashboardScreen
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import ru.neelvis.librarian.core.model.User
import ru.neelvis.librarian.feature.dashboard.BookInfoScreen
import ru.neelvis.librarian.feature.dashboard.EditBookScreen
import ru.neelvis.librarian.feature.dashboard.viewmodels.DashboardViewModel

@Serializable
object DashboardRoute

@Serializable
object DashboardBaseRoute

@Serializable
object BookInfoRoute

@Serializable
object EditBookRoute

fun NavController.navigateToDashboard(navOptions: NavOptions) =
    navigate(route = DashboardRoute, navOptions)

fun NavController.navigateToBookInfo() =
    navigate(route = BookInfoRoute)

fun NavController.navigateToEditBook() =
    navigate(route = EditBookRoute)

fun NavGraphBuilder.dashboardSection(
    navController: NavController,
    userFlow: StateFlow<User?>,
    onSignInClick: () -> Unit,
) {
    navigation<DashboardBaseRoute>(startDestination = DashboardRoute) {
        composable<DashboardRoute> {
            DashboardScreen(
                userFlow = userFlow,
                onSignInClick = onSignInClick,
                navigateOnBookSelected = navController::navigateToBookInfo,
            )
        }
        composable<BookInfoRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(DashboardRoute)
            }
            BookInfoScreen(
                hiltViewModel<DashboardViewModel>(parentEntry),
                onNavigateBack = navController::popBackStack,
                onNavigateToEdit = navController::navigateToEditBook
            )
        }
        composable<EditBookRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(DashboardRoute)
            }
            EditBookScreen(
                hiltViewModel<DashboardViewModel>(parentEntry),
                onNavigateBack = navController::popBackStack
            )
        }
    }
}
