package ru.neelvis.librarian.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.neelvis.librarian.feature.profile.ProfileScreen

@Serializable
object ProfileRoute

fun NavController.navigateToProfile(navOptions: NavOptions) =
    navigate(route = ProfileRoute, navOptions)

fun NavGraphBuilder.profileSection() {
    composable<ProfileRoute> {
        ProfileScreen()
    }
}