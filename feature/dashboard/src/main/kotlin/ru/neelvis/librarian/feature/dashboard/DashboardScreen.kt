import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.StateFlow
import ru.neelvis.librarian.common.R
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.core.model.User
import ru.neelvis.librarian.core.ui.BookCard
import ru.neelvis.librarian.core.ui.BooksListUiState
import ru.neelvis.librarian.feature.dashboard.viewmodels.DashboardViewModel
import ru.neelvis.librarian.feature.dashboard.viewmodels.SelectionMode

data class SelectableBook(val book: Book, var selected: Boolean? = null)

@Composable
internal fun DashboardScreen(
    : DashboardViewModel = hiltViewModel(),
    userFlow: StateFlow<User?>,
    onSignInClick: () -> Unit,
    navigateOnBookSelected: () -> Unit,
) {
    val booksUiState by viewModel.booksUiState.collectAsStateWithLifecycle(minActiveState= Lifecycle.State.RESUMED)
    val selectionMode: SelectionMode by viewModel.selectionMode.collectAsStateWithLifecycle(initialValue = SelectionMode.VIEW)
    val selectedBooks: Set<Book> by viewModel.selectedBooks.collectAsStateWithLifecycle(initialValue = setOf()) // for SelectionMode.SELECT
    val user: User? by userFlow.collectAsStateWithLifecycle(initialValue = null)

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.isBookRemovedFlow.collect { event ->
            if (event is BookEvent.BOOK_REMOVED) {
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error while deleting", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val books: List<Book> = when (booksUiState) {
        BooksListUiState.IsLoading, BooksListUiState.Error -> {
            emptyList()
        }
        is BooksListUiState.Success -> {
            (booksUiState as BooksListUiState.Success).books
        }
    }

    DashboardScreen(
        user = user,
        onSignInClick = onSignInClick,
        books = books,
        onBookClicked = { book ->
            if (selectionMode == SelectionMode.VIEW) {
                viewModel.setCurrentBook(book)
                navigateOnBookSelected()
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
    books: List<Book>,
    onBookClicked: (Book) -> Unit, onBookLongClicked: (Book) -> Unit,
    onDeleteSelectedClick: () -> Unit, onClearSelectionClick: () -> Unit,
    selectionMode: SelectionMode, selectedBooks: Set<Book>,
    user: User? = null, // Pass user info if available
    onSignInClick: (() -> Unit)? = null, // For sign in prompt
) {
    val screenConfiguration = LocalConfiguration.current

    val gridState = rememberLazyGridState()
    var scrolledY = 0f

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            state = gridState,
            contentPadding = PaddingValues(all = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            if (screenConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    DashboardHeader(
                        scrollOffset = scrolledY,
                        booksCount = books.size,
                        user = user,
                        onSignInClick = onSignInClick
                    )
                }
            }
            items(books, key = { it.id }) { book ->
                BookCardWrapper(
                    selectableBook = SelectableBook(
                        book = book,
                        selected = if (selectionMode == SelectionMode.VIEW) null else selectedBooks.contains(book)
                    ),
                    onBookClicked = onBookClicked,
                    onBookLongClicked = onBookLongClicked,
                )
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
                        enabled = selectedBooks.isNotEmpty()
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
fun BookCardWrapper(
    selectableBook: SelectableBook,
    onBookClicked: (Book) -> Unit,
    onBookLongClicked: (Book) -> Unit,
) {
    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = { onBookClicked(selectableBook.book) },
                onLongClick = { onBookLongClicked(selectableBook.book) }
            )
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
fun DashboardHeader(scrollOffset: Float, booksCount: Int, user: User?, onSignInClick: (() -> Unit)? = null, friends: Int = 0) {
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
                if (user?.photoUrl.isNullOrBlank()) {
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
                } else {
                    Image(
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(corner = CornerSize(80.dp)))
                            .border(width = 2.dp, brush = SolidColor(Color.White), shape = CircleShape),
                        painter = rememberAsyncImagePainter(user.photoUrl),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // User name and nickname or sign in prompt
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(PaddingValues(start = 85.dp))
            ) {
                if (user != null) {
                    Text(
                        user.name.ifBlank { user.email },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Not signed in",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = Color.White
                    )
                    if (onSignInClick != null) {
                        Button(
                            onClick = onSignInClick,
                            modifier = Modifier.padding(top = 4.dp)
                        ) { Text("Sign In / Sign Up") }
                    }
                }
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
                UserInfoCounter(friends, "Friends")
            }
        }
    }
}

@Preview
@Composable
fun DashboardScreenPreviewNoSelection() {
    DashboardScreen(books =
    listOf(
        Book(title = "1"),
        Book(title = "2"),
        Book(title = "3"),
        Book(title = "4")

    ),
        onBookClicked = {}, onBookLongClicked = {},
        onDeleteSelectedClick = {}, onClearSelectionClick = {},
        selectionMode = SelectionMode.VIEW, selectedBooks = setOf<Book>()
    )
}

@Preview
@Composable
fun DashboardScreenPreviewSelection() {
    val book1 = Book(title = "1")
    DashboardScreen(books = listOf(
        book1,
        Book(title = "2"),
        Book(title = "3"),
        Book(title = "4")
    ),
        onBookClicked = {}, onBookLongClicked = {},
        onDeleteSelectedClick = {}, onClearSelectionClick = {},
        selectionMode = SelectionMode.SELECT, selectedBooks = setOf(book1)
    )
}