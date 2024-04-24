package com.mindlift.android.musicFeature.ui

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.model.GenreModel
import com.example.music.util.GlideImage

@Composable
fun GenreItem(genre: GenreModel, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0.5f, 0.5f, 0.5f, 0.5f))
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            imageUrl = genre.coverUrl,
            contentDescription = "${genre.name} cover",
            modifier = Modifier
                .size(100.dp)
                .padding(start=10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = genre.name,
            fontSize = 25.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun GenreList(genres: List<GenreModel>, onGenreClick: (GenreModel) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.65f),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn (modifier = Modifier.fillMaxSize()) { // Changed from LazyColumn to LazyRow
            items(genres) { genre ->
                GenreItem(genre = genre, onClick = { onGenreClick(genre) })
            }
        }
    }
}
