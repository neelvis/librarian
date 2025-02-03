package ru.neelvis.librarian.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.neelvis.librarian.R

data class BottomButton(val buttonID: BottomButtonID, val resID: Int, val onClick: () -> Unit)
enum class BottomButtonID(val labelResID: Int) {
    main(R.string.main_nav_button_label),
    global(R.string.global_nav_button_label),
    addBook(R.string.addBook_nav_button_label),
    notification(R.string.notification_nav_button_label),
    profile(R.string.profile_nav_button_label)
}

@Composable
fun LibrarianBottomAppBar(buttons: List<BottomButton>) {
    BottomAppBar(
        containerColor = Color.Transparent,
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            buttons.map { RegularBottomIcon(buttonID = it.buttonID, resID = it.resID, onClick = it.onClick) }
        }
    }
}

@Composable
fun RegularBottomIcon(buttonID: BottomButtonID, resID: Int, onClick: (() -> Unit)) {
    val iconSize = 32
    val labelWidth = 48
    val textSize = 12
    Column(
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .width(labelWidth.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .width(iconSize.dp)
                .height(iconSize.dp),
            painter = painterResource(resID),
            contentDescription = stringResource(buttonID.labelResID),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
        )
        Text(
            stringResource(buttonID.labelResID),
            modifier = Modifier
                .padding(top = 2.dp),
            fontSize = textSize.sp,
            fontWeight = FontWeight.W300,
            softWrap = false,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun BottomAppBarPreview() {
    LibrarianBottomAppBar(
        listOf(
            BottomButton(
                BottomButtonID.main,
                R.drawable.bookshelf,
                { }
            ),
            BottomButton(BottomButtonID.global, R.drawable.global, { }),
            BottomButton(
                BottomButtonID.addBook,
                R.drawable.plus,
                { }
            ),
            BottomButton(
                BottomButtonID.notification,
                R.drawable.notification,
                { }
            ),
            BottomButton(
                BottomButtonID.profile,
                R.drawable.profile,
                { }
            )
        )
    )
}