package ru.neelvis.librarian.feature.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.neelvis.librarian.feature.notification.NotificationScreen

@Serializable
data object NotificationRoute

fun NavController.navigateToNotification(navOptions: NavOptions) =
    navigate(route = NotificationRoute, navOptions)

fun NavGraphBuilder.notificationSection() {
    composable<NotificationRoute> {
        NotificationScreen()
    }
}
