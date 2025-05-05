package ru.neelvis.librarian.feature.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.core.ui.BookCard
import ru.neelvis.librarian.feature.dashboard.viewmodels.DashboardViewModel


@Composable
internal fun BookInfoScreen(viewModel: DashboardViewModel = hiltViewModel(), onNavigateBack: () -> Unit, onNavigateToEdit: () -> Unit) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.isBookRemovedFlow.collect { event ->
            if (event is BookEvent.BOOK_REMOVED) {
                onNavigateBack()
            } else {
                Toast.makeText(context, "Error while deleting books", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BookInfoCard(
        viewModel.currentBook,
        onRemove = {
            viewModel.removeCurrentBook()
        },
        onEdit =
        onNavigateToEdit
    )
}

@Composable
fun BookInfoCard(
    selectedBook: Book,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val cardSize = Modifier
                .width(150.dp)
                .height(200.dp)

            BookCard(selectedBook, modifier = cardSize)

            Column(
                modifier = cardSize,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Button(
                    onClick = onRemove,
                    modifier = Modifier
                        .width(150.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(corner = CornerSize(10.dp))
                ) {
                    Text("Remove")
                }
                Button(
                    enabled = false,
                    onClick = { },
                    modifier = Modifier
                        .width(150.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(corner = CornerSize(10.dp))
                ) {
                    Text("Find similar")
                }
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .width(150.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(corner = CornerSize(10.dp))
                ) {
                    Text("Edit")
                }
            }

        }
        Text(selectedBook.description ?: "")
    }
}

@Preview
@Composable
fun BookMenuPreview() {
    BookInfoCard(selectedBook = Book(
        id = "0",
        title = "Preview Title", authors = listOf("Any Nastya"),
        cover = null,
        isbn = "123213123",
        publishedDate = "17.12.1994",
        description = "Some book"
    ),
        onRemove = {},
        onEdit = {}
    )
}