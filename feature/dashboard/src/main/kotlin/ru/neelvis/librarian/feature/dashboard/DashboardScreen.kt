import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.neelvis.librarian.common.R
import ru.neelvis.librarian.common.ui.BookCard
import ru.neelvis.librarian.common.ui.BooksListUiState
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.feature.dashboard.viewmodels.DashboardViewModel
import ru.neelvis.librarian.feature.dashboard.viewmodels.SelectionMode

data class SelectableBook(val book: Book, var selected: Boolean? = null)

@Composable
internal fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel(), navigateOnBookSelected: () -> Unit) {
    val booksUiState by viewModel.booksUiState.collectAsStateWithLifecycle()
    val selectionMode: SelectionMode by viewModel.selectionMode.collectAsStateWithLifecycle(initialValue = SelectionMode.VIEW)
    val selectedBooks: Set<Book> by viewModel.selectedBooks.collectAsStateWithLifecycle(initialValue = setOf()) // for SelectionMode.SELECT

    // group books list by 3 for grid representation
    val booksGrouped: List<List<Book>> = when (booksUiState) {
        BooksListUiState.IsLoading, BooksListUiState.Error -> {
            emptyList()
        }

        is BooksListUiState.Success -> {
            (booksUiState as BooksListUiState.Success).books.chunked(3)
        }
    }
    val booksCount = when (booksUiState) {
        BooksListUiState.IsLoading, BooksListUiState.Error -> {
            0
        }

        is BooksListUiState.Success -> {
            (booksUiState as BooksListUiState.Success).books.size
        }
    }

    val context = LocalContext.current
    viewModel.isBookRemoved.observe(LocalLifecycleOwner.current) { removed ->
        Log.d(null, "books were removed: $removed")
        if (removed == true) {
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            viewModel.confirmRemoving()
        }
    }

    DashboardScreen(
        booksGrouped = booksGrouped,
        booksCount = booksCount,
        onBookClicked = { book ->
            if (selectionMode == SelectionMode.VIEW) {
                viewModel.setCurrentBook(book)
                navigateOnBookSelected.invoke()
            } else {
                if (viewModel.isSelected(book)) {
                    viewModel.deselectBook(book)
                } else {
                    viewModel.selectBook(book)
                }
            }
        },
        onBookLongClicked = { book ->
            if (selectionMode == SelectionMode.VIEW) {
                viewModel.setSelectionMode(SelectionMode.SELECT)
                viewModel.selectBook(book)
            } else {
                viewModel.setSelectionMode(SelectionMode.VIEW)
                viewModel.clearSelection()
            }
        },
        onDeleteSelectedClick = {
            viewModel.deleteSelected()
        },
        onClearSelectionClick = {
            viewModel.clearSelection()
        },
        selectionMode = selectionMode,
        selectedBooks = selectedBooks
    )
}

@Composable
fun DashboardScreen(
    booksGrouped: List<List<Book>>, booksCount: Int,
    onBookClicked: (Book) -> Unit, onBookLongClicked: (Book) -> Unit,
    onDeleteSelectedClick: () -> Unit, onClearSelectionClick: () -> Unit,
    selectionMode: SelectionMode, selectedBooks: Set<Book>,
) {
    val lazyScrollState = rememberLazyListState()
    var scrolledY = 0f
    var previousOffset = 0

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, state = lazyScrollState) {
            scrolledY += lazyScrollState.firstVisibleItemScrollOffset - previousOffset
            previousOffset = lazyScrollState.firstVisibleItemScrollOffset

            item {
                Header(
                    scrollOffset = scrolledY,
                    booksCount = booksCount
                )
            }
            // Books list
            items(booksGrouped) { booksGroup ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Color(0xEEFFFFFF)),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    booksGroup.map { book ->
                        BookCardWrapper(
                            selectableBook = SelectableBook(
                                book = book,
                                selected = if (selectionMode == SelectionMode.VIEW) null else selectedBooks.contains(book)
                            ),
                            onBookClicked = onBookClicked,
                            onBookLongClicked = onBookLongClicked
                        )
                    }
                }
            }
        }
        if (selectionMode == SelectionMode.SELECT) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { onDeleteSelectedClick() },
                        enabled = selectedBooks.size > 0
                    ) {
                        Text("Delete selected (${selectedBooks.size})")
                    }
                    Button(onClick = { onClearSelectionClick() }) {
                        Text("Clear Selection")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCardWrapper(selectableBook: SelectableBook, onBookClicked: (Book) -> Unit, onBookLongClicked: (Book) -> Unit) {
    Box(
        modifier = Modifier.combinedClickable(
            onClick = { onBookClicked(selectableBook.book) },
            onLongClick = { onBookLongClicked(selectableBook.book) })
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
        ) {
            BookCard(
                selectableBook.book,
                Modifier
                    .width(100.dp)
                    .height(150.dp)
            )
            if (selectableBook.selected == true) {
                Text("Selected", color = Color.Green)
            } else if (selectableBook.selected == false) {
                Text("Unselected", color = Color.Red)
            } else {
                Text("View mode", color = Color.Blue)
            }
        }
    }
}

@Composable
fun UserInfoCounter(counter: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            if (counter < 1000) counter.toString() else "1000+",
            softWrap = false,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = TextUnit(16f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            label,
            fontSize = TextUnit(12f, TextUnitType.Sp),
            color = Color.White
        )
    }
}

@Composable
fun Header(scrollOffset: Float, booksCount: Int, freinds: Int = 0) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
    ) {
        //header
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(corner = CornerSize(10.dp)))
                .blur(radius = 1.dp)
                .graphicsLayer {
                    translationY = scrollOffset
                    alpha = 1.0f - scrollOffset / 500f
                },
            painter = painterResource(R.drawable.header),
            contentDescription = "Main header pic",
            contentScale = ContentScale.FillWidth
        )

        //header content with paddings
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 10.dp)
                .padding(horizontal = 5.dp)
                .graphicsLayer {
                    rotationX = scrollOffset * 0.1f
                    translationY = -scrollOffset * 0.25f
                }
        ) {
            //profile pic
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Image(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(80.dp)))
                        .border(width = 2.dp, brush = SolidColor(Color.White), shape = CircleShape),
                    painter = painterResource(R.drawable.profile_mock),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop
                )
            }
            // User name and nickname
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(PaddingValues(start = 85.dp))
            ) {
                Text(
                    "@neelvis_reader",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = Color.White
                )
            }
            // Counters
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End
            ) {
                UserInfoCounter(booksCount, "Books")
                UserInfoCounter(freinds, "Friends")
            }
        }
    }
}

@Preview
@Composable
fun HeaderPreview() {
    Column {
        Header(0f, 100000)
        Header(50f, 100000)
        Header(100f, 100000)
    }
}

@Preview
@Composable
fun DashboardScreenPreviewNoSelection() {
    DashboardScreen(booksGrouped = listOf(
        listOf(
            Book(title = "1"),
            Book(title = "2"),
            Book(title = "3")
        ),
        listOf(
            Book(title = "4")
        )
    ), booksCount = 4,
        onBookClicked = {}, onBookLongClicked = {},
        onDeleteSelectedClick = {}, onClearSelectionClick = {},
        selectionMode = SelectionMode.VIEW, selectedBooks = setOf<Book>()
    )
}

@Preview
@Composable
fun DashboardScreenPreviewSelection() {
    val book1 = Book(title = "1")
    DashboardScreen(booksGrouped = listOf(
        listOf(
            book1,
            Book(title = "2"),
            Book(title = "3")
        ),
        listOf(
            Book(title = "4")
        )
    ),
        booksCount = 4,
        onBookClicked = {}, onBookLongClicked = {},
        onDeleteSelectedClick = {}, onClearSelectionClick = {},
        selectionMode = SelectionMode.SELECT, selectedBooks = setOf(book1)
    )
}