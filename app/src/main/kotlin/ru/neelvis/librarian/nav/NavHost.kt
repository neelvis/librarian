package ru.neelvis.librarian.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.flow.StateFlow
import ru.neelvis.librarian.core.model.User
import ru.neelvis.librarian.feature.add_books.navigation.addBookSection
import ru.neelvis.librarian.feature.dashboard.navigation.DashboardBaseRoute
import ru.neelvis.librarian.feature.dashboard.navigation.dashboardSection
import ru.neelvis.librarian.feature.notification.navigation.notificationSection
import ru.neelvis.librarian.feature.profile.navigation.profileSection
import ru.neelvis.librarian.feature.search.navigation.searchSection
import ru.neelvis.librarian.viewmodels.AppAuthViewModel
import ru.neelvis.librarian.ui.LibrarianAppState

@Composable
fun LibrarianAppNavHost(bookAppState: LibrarianAppState, modifier: Modifier = Modifier) {
    val navController = bookAppState.navController
    val appAuthViewModel: AppAuthViewModel = hiltViewModel()
    val userStateFlow: StateFlow<User?> = appAuthViewModel.user
    NavHost(
        navController = navController,
        startDestination = DashboardBaseRoute,
        modifier = modifier
    ) {
        dashboardSection(
            navController = navController,
            userFlow = userStateFlow,
            onSignInClick = { bookAppState.navigateToTopLevelDestination(TopLevelDestination.PROFILE) },
        )
        profileSection()
        addBookSection(navController = navController)
        searchSection()
        notificationSection(
            userFlow = userStateFlow,
            onSignInClick = { bookAppState.navigateToTopLevelDestination(TopLevelDestination.PROFILE) },
        )
    }
}