package ru.neelvis.librarian.nav

import ru.neelvis.librarian.feature.add_books.navigation.AddBookRoute
import ru.neelvis.librarian.feature.dashboard.navigation.DashboardBaseRoute
import ru.neelvis.librarian.feature.dashboard.navigation.DashboardRoute
import ru.neelvis.librarian.feature.notification.navigation.NotificationRoute
import ru.neelvis.librarian.feature.profile.navigation.ProfileRoute
import ru.neelvis.librarian.feature.search.navigation.SearchRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    DASHBOARD(route = DashboardRoute::class, baseRoute = DashboardBaseRoute::class),
    PROFILE(route = ProfileRoute::class),
    ADDBOOK(route = AddBookRoute::class),
    SEARCH(route = SearchRoute::class),
    NOTIFICATION(route = NotificationRoute::class)
}