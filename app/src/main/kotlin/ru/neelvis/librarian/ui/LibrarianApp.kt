package ru.neelvis.librarian.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.neelvis.librarian.R
import ru.neelvis.librarian.nav.LibrarianAppNavHost
import ru.neelvis.librarian.nav.TopLevelDestination

@Composable
fun LibrarianApp(
    bookAppState: LibrarianAppState,
    modifer: Modifier = Modifier,
) {
    // here some global initialization may be added
    LibrarianApp(bookAppState)
}


@Composable
internal fun LibrarianApp(
    bookAppState: LibrarianAppState,
) {
    Scaffold(
        bottomBar = {
            LibrarianBottomAppBar(
                listOf(
                    BottomButton(BottomButtonID.main, R.drawable.bookshelf,
                        { bookAppState.navigateToTopLevelDestination(TopLevelDestination.DASHBOARD) }),
                    BottomButton(BottomButtonID.global, R.drawable.global,
                        { bookAppState.navigateToTopLevelDestination(TopLevelDestination.SEARCH) }),
                    BottomButton(BottomButtonID.addBook, R.drawable.plus,
                        { bookAppState.navigateToTopLevelDestination(TopLevelDestination.ADDBOOK) }),
                    BottomButton(BottomButtonID.notification, R.drawable.notification,
                        { bookAppState.navigateToTopLevelDestination(TopLevelDestination.NOTIFICATION) }),
                    BottomButton(BottomButtonID.profile, R.drawable.profile,
                        { bookAppState.navigateToTopLevelDestination(TopLevelDestination.PROFILE) })
                )
            )
        },
        contentWindowInsets = WindowInsets(40.dp, 5.dp, 40.dp, 0.dp)
    ) { padding ->
        LibrarianAppNavHost(
            bookAppState,
            modifier = Modifier
                .statusBarsPadding()
                .padding(padding)
        )
    }
}