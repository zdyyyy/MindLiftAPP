package com.mindlift.android.musicFeature

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.util.GlideImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music.model.GenreModel
import com.example.music.model.SongsViewModel
import com.mindlift.android.musicFeature.ui.GenreList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.music.model.MusicNavViewModel
import com.mindlift.android.musicFeature.ui.GenreItem
import com.google.firebase.firestore.FirebaseFirestore
import com.mindlift.android.R
import com.mindlift.android.UserViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun MusicScreenComposable(
    navController: NavController,
    userViewModel: UserViewModel,
    musicNavViewModel: MusicNavViewModel,
    navToMusicList: () -> Unit,
    navToMusicPlayer: () -> Unit
) {
    val viewModel: SongsViewModel = viewModel()
    val genres = viewModel.genres.value
    val currentSong by viewModel.currentSong
    var latestMood by remember { mutableStateOf<String?>(null) }
    val username by userViewModel.username.collectAsState()

    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            latestMood = fetchMood(username)
        }
    }

    MainPage(
        navController = navController,
        genres = genres,
        onGenreClick = { genre ->
            val encodedGenreName = genre.name
            val encodedCoverUrl = genre.coverUrl
            musicNavViewModel.genreId = genre.id
            musicNavViewModel.genreName = encodedGenreName
            musicNavViewModel.genreCoverUrl = encodedCoverUrl
            navToMusicList()
        },
        songUrl = currentSong?.url,
        songTitle = currentSong?.title,
        songArtist = currentSong?.artist,
        coverUrl = currentSong?.coverUrl,
        latestMood = latestMood,
        musicNavViewModel = musicNavViewModel,
        navToMusicPlayer = navToMusicPlayer
    )
}

@Composable
fun MainPage(
    navController: NavController,
    genres: List<GenreModel>,
    onGenreClick: (GenreModel) -> Unit,
    songUrl: String? = null,
    songTitle: String? = null,
    songArtist: String? = null,
    coverUrl: String? = null,
    latestMood: String?,
    musicNavViewModel: MusicNavViewModel,
    navToMusicPlayer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        // Display mood-based recommendations if a mood is detected
        if (latestMood != null) {
            MoodBasedRecommendationsSection(
                mood = latestMood,
                genres = genres,
                onRecGenreClick = onGenreClick,
                navController = navController
            )
        } else {
            Text(text = "No mood detected", color = Color.White)
        }

        GenreSection(genres, onGenreClick)
        LastTimeSection()
        MusicBar(
            musicNavViewModel,
            navToMusicPlayer,
            navController = navController
        )
    }
}

@Composable
fun MoodBasedRecommendationsSection(
    mood: String?,
    genres: List<GenreModel>,
    onRecGenreClick: (GenreModel) -> Unit,
    navController: NavController
) {
    mood?.let { currentMood ->
        // Use getRecommendedGenreIdsForMood to determine the genres to recommend
        val recommendedGenreIds = getRecommendedGenreIdsForMood(currentMood, genres)

        // Filter genres based on the recommended IDs
        val recommendedGenres = genres.filter { it.id in recommendedGenreIds }

        if (recommendedGenres.isNotEmpty()) {
            Column {
                Text(
                    text = "Suggested Genre For You",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(10.dp)
                )
                recommendedGenres.forEach { genre ->

                    GenreItem(genre = genre) {
                        onRecGenreClick(genre)
                    }
                }
            }
        } else {
            Text("No Suggestions Based On Your Current Mood :(", color = Color.White)
        }
    } ?: run {
        Text("Fetching Suggestions...", color = Color.White)
    }
}

suspend fun fetchMood(username: String): String {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val documentSnapshot = firestore.collection("Users").document(username).get().await()
        documentSnapshot.getString("Mood") ?: "No mood detected"
    } catch (e: Exception) {
        "Error fetching mood"
    }
}

fun getRecommendedGenreIdsForMood(mood: String, genres: List<GenreModel>): List<String> {
    val moodToGenreNameMap = mapOf(
        "happy" to listOf("Classical"),
        "sad" to listOf("Hip Hop"),
        "fear" to listOf("Country"),
        "angry" to listOf("Electronic")
    )

    // Get the list of genre names recommended for the current mood
    val recommendedGenreNames = moodToGenreNameMap[mood] ?: listOf()

    // Find the genres that match the recommended names and return their IDs
    return genres.filter { it.name in recommendedGenreNames }.map { it.id }
}

@Composable
fun GenreSection(genres: List<GenreModel>, onGenreClick: (GenreModel) -> Unit) {
    Text(
        text = "Something Else On Your Mind ?",
        color = Color.White,
        fontSize = 25.sp,
        modifier = Modifier.padding(10.dp)
    )
    GenreList(genres = genres, onGenreClick = onGenreClick)
}

@Composable
fun LastTimeSection() {
    Text(
        text = "Pick Up From Last Time",
        color = Color.White,
        fontSize = 25.sp,
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
fun MusicBar(
    musicNavViewModel: MusicNavViewModel,
    navToMusicPlayer: () -> Unit,
    navController: NavController
) {
    // define the values
    val songUrl = musicNavViewModel.songUrl
    val songTitle = musicNavViewModel.songTitle
    val songArtist = musicNavViewModel.songArtist
    val coverUrl = musicNavViewModel.songCoverUrl
    if(songUrl.isNullOrEmpty() and songTitle.isNullOrEmpty() and songArtist.isNullOrEmpty() && coverUrl.isNullOrEmpty())
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    BorderStroke(1.dp, Color.White),
                )
                .background(Color(0.5f, 0.5f, 0.5f, 0.5f))
                .padding(8.dp)
                .clickable(enabled = songUrl.isNotEmpty()) {
                    if (musicNavViewModel.songUrl != null) {
                        navToMusicPlayer()
                    }
                },
            verticalAlignment = Alignment.Bottom
        ){
            Text(
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                ),
                text = "Start Listening Now ..."
            )
        }
    }
    else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    BorderStroke(1.dp, Color.White),
                )
                .background(Color(0.3f, 0.3f, 0.3f, 0.3f))
                .padding(8.dp)
                .clickable(enabled = songUrl.isNotEmpty()) {
                    if (musicNavViewModel.songUrl != null) {
                        navToMusicPlayer()
                    }
                },
            verticalAlignment = Alignment.Bottom
        ) {
            if (coverUrl.isNotEmpty()) {
                GlideImage(
                    imageUrl = coverUrl,
                    contentDescription = "Song Cover",
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                // Display the song title
                Text(
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                    ),
                    text = songTitle
                )
                // Display the artist name
                val displayArtist =
                    if (songArtist.isNullOrEmpty() || songArtist == "noArtist") "Unknown Artist" else songArtist
                displayArtist.let {
                    Text(
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                        ),
                        text = it
                    )
                }
            }
        }
    }
}
