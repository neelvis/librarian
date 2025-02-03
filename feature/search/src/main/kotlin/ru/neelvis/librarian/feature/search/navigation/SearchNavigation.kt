package ru.neelvis.librarian.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.neelvis.librarian.feature.search.SearchScreen

@Serializable
data object SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptions) =
    navigate(route = SearchRoute, navOptions)

fun NavGraphBuilder.searchSection() {
    composable<SearchRoute> {
        SearchScreen()
    }
}