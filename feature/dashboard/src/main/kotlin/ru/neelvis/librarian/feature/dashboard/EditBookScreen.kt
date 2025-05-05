package ru.neelvis.librarian.feature.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.core.ui.BookInfoEditable
import ru.neelvis.librarian.feature.dashboard.viewmodels.DashboardViewModel

@Composable
internal fun EditBookScreen(viewmodel: DashboardViewModel = hiltViewModel<DashboardViewModel>(), onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.isBookUpdatedFlow.collect { event ->
            if (event is BookEvent.BOOK_UPDATED) {
                Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            } else {
                Toast.makeText(context, "Error, please retry. ${(event as BookEvent.ERROR).message}", Toast.LENGTH_SHORT).show()

            }
        }
    }

    EditBookScreen(viewmodel.currentBook, onSaveChanges = { book: Book ->
        viewmodel.updateBook(book)
    })
}

@Composable
internal fun EditBookScreen(book: Book, onSaveChanges: (Book) -> Unit) {
    Column {
        Text("Edit book")
        BookInfoEditable(
            book = book,
            onAcceptClick = onSaveChanges
        )
    }
}