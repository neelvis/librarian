package ru.neelvis.librarian.common.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.neelvis.librarian.common.R
import ru.neelvis.librarian.core.model.Book

sealed interface BooksListUiState {

    data object IsLoading : BooksListUiState

    data class Success(
        val books: List<Book>,
    ) : BooksListUiState

    data object Error : BooksListUiState
}

@Composable
fun BookCard(book: Book, modifier: Modifier) {
    Box(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.BottomCenter) { //book cover with label at the bottom
                if (book.cover != null && BitmapFactory.decodeByteArray(book.cover, 0, book.cover!!.size) != null) {
                    Image(
                        bitmap = BitmapFactory.decodeByteArray(book.cover, 0, book.cover!!.size).asImageBitmap(),
                        contentDescription = "Book cover",
                        modifier = Modifier.fillMaxSize(),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painterResource(id = R.drawable.mock_cover),
                        contentDescription = "Book cover",
                        modifier = Modifier.fillMaxSize(),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                }
                Box( // label at the bottom, circle-shaped border
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .border(
                                border = BorderStroke(
                                    width = 1.dp, color = Color.Black
                                ), shape = RoundedCornerShape(corner = CornerSize(10.dp))
                            )
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color.White.copy(alpha = 0.8f))
                    ) {
                        Text(
                            book.title,
                            modifier = Modifier
                                .padding(all = 2.dp)
                                .basicMarquee(animationMode = MarqueeAnimationMode.WhileFocused),
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            book.authors.joinToString(", "),
                            modifier = Modifier
                                .padding(all = 2.dp)
                                .basicMarquee(animationMode = MarqueeAnimationMode.WhileFocused),
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
