package ru.neelvis.librarian.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.neelvis.librarian.feature.add_books.navigation.addBookSection
import ru.neelvis.librarian.feature.dashboard.navigation.DashboardBaseRoute
import ru.neelvis.librarian.feature.dashboard.navigation.dashboardSection
import ru.neelvis.librarian.feature.notification.navigation.notificationSection
import ru.neelvis.librarian.feature.profile.navigation.profileSection
import ru.neelvis.librarian.feature.search.navigation.searchSection
import ru.neelvis.librarian.ui.LibrarianAppState


@Composable
fun LibrarianAppNavHost(bookAppState: LibrarianAppState, modifier: Modifier = Modifier) {
    val navController = bookAppState.navController
    NavHost(
        navController = navController,
        startDestination = DashboardBaseRoute,
        modifier = modifier
    ) {
        dashboardSection(navController = navController)
        profileSection()
        addBookSection()
        searchSection()
        notificationSection()
    }
}