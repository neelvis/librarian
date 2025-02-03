package ru.neelvis.librarian.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ru.neelvis.librarian.common.R

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    ) {
        Text(
            "Profile Settings",
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = TextUnit(24f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(shape = RoundedCornerShape(size = 50.dp))
                .border(
                    BorderStroke(width = 2.dp, color = Color.LightGray),
                    shape = RoundedCornerShape(size = 50.dp)
                )
        ) {
            Image(
                painter = painterResource(R.drawable.profile_mock),
                contentDescription = "profile logo",
                contentScale = ContentScale.FillBounds
            )
        }
        Text(
            "Change password",
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = TextUnit(16f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "Log out",
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = TextUnit(16f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}