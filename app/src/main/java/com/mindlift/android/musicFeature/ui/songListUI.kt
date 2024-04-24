package com.mindlift.android.musicFeature.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.music.model.MusicNavViewModel
import com.example.music.model.SongModel
import com.example.music.model.SongsViewModel
import com.example.music.util.GlideImage
import com.mindlift.android.R

@Composable
fun SongsListScreen(
    navController: NavController,
    viewModel: SongsViewModel,
    viewModelNav: MusicNavViewModel,
    navToMusicPlayer: () -> Unit,
    navToHome: () -> Unit,
    navToGenre: () -> Unit
) {

    val genreId = viewModelNav.genreId
    val genreName = viewModelNav.genreName
    val coverUrl = viewModelNav.genreCoverUrl

    // Fetch songs for the genre when genreId changes
    LaunchedEffect(genreId) {
        viewModel.fetchSongsForGenre(genreId)
    }

    val songs = viewModel.songs.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        when (genreName) {
            "Classical" -> {
                Image(

                    painter = painterResource(id = R.drawable.classical_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            "Hip Hop" -> {
                Image(

                    painter = painterResource(id = R.drawable.hip_hop_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            "Country" -> {
                Image(

                    painter = painterResource(id = R.drawable.country_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            "Electronic" -> {
                Image(

                    painter = painterResource(id = R.drawable.electronic_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            "Blues" -> {
                Image(

                    painter = painterResource(id = R.drawable.blues_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            "Rock" -> {
                Image(

                    painter = painterResource(id = R.drawable.rock_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            else -> {
                Image(

                    painter = painterResource(id = R.drawable.music_screen_microphone_background_image),
                    contentDescription = "Music Play Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }


        Column {
            // Display the genre cover image
            GenreCoverImage(coverUrl = coverUrl)

            // Display the genre name at the top
            GenreNameHeader(genreName = genreName)

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f),
                contentAlignment = Alignment.Center
            ) {

                // Display songs in a list
                LazyColumn {
                    items(songs) { song ->
                        SongItem(song = song) {
                            // Update the current song state
                            viewModel.setCurrentSong(song)

                            // define navigation parameters
                            viewModelNav.songUrl = song.url
                            viewModelNav.songTitle = song.title
                            viewModelNav.songArtist = song.artist ?: "noArtist"
                            viewModelNav.songCoverUrl = song.coverUrl
                            // navigate to music player screen
                            navToMusicPlayer()
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom)
            {
                Button(
                    onClick = {
                        navToGenre()
                    }, modifier = Modifier
                        .padding(start=10.dp)
                        .border(
                            BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text("Genres")
                }

                Button(
                    onClick = {
                        navToHome()
                    }, modifier = Modifier
                        .padding(start=10.dp)
                        .border(
                            BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text("Home")
                }
            }
        }
    }
}

@Composable
fun GenreCoverImage(coverUrl: String) {
    GlideImage(
        imageUrl = coverUrl,
        contentDescription = "Genre Cover",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun SongItem(song: SongModel, onClick: () -> Unit) {
    val displayArtist =
        if (song.artist.isNullOrEmpty() || song.artist == "noArtist") "Unknown Artist" else song.artist
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
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            imageUrl = song.coverUrl,
            contentDescription = "Cover for ${song.title}",
            modifier = Modifier
                .size(100.dp)
                .padding(start=10.dp)
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                modifier = Modifier.padding(10.dp),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                ),
                text = song.title
            )
            Text(
                modifier = Modifier.padding(15.dp),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                ),
                text = "Artist: $displayArtist"
            )
        }
    }
}

@Composable
fun GenreNameHeader(genreName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Genre: $genreName",
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 3.dp,
            color = Color.White
        )
    }
}
