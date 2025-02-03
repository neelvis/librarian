package ru.neelvis.librarian.feature.add_books.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.neelvis.librarian.feature.add_books.AddBookScreen

@Serializable
data object AddBookRoute

fun NavController.navigateToAddBook(navOptions: NavOptions) = navigate(AddBookRoute, navOptions)

fun NavGraphBuilder.addBookSection() {
    composable<AddBookRoute> {
        AddBookScreen()
    }
}