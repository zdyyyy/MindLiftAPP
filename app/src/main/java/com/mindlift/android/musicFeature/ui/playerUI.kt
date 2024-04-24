package com.mindlift.android.musicFeature.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.music.model.MusicNavViewModel
import com.example.music.util.MyExoplayer
import com.example.music.model.SongModel
import com.example.music.util.GlideImage
import com.mindlift.android.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(
    musicNavViewModel: MusicNavViewModel,
    navToMusicList: () -> Unit,
    navToGenreScreen: () -> Unit,
    navToHome: () -> Unit
) {
    // Getting values from the Song View Model
    val songUrl = musicNavViewModel.songUrl
    val songTitle = musicNavViewModel.songTitle
    val songArtist = musicNavViewModel.songArtist
    val coverUrl = musicNavViewModel.songCoverUrl

    val context = LocalContext.current

    val exoPlayer = MyExoplayer.getInstance(LocalContext.current)

    // Handling "noArtist" placeholder or null artist names
    val displayArtist =
        if (songArtist.isNullOrEmpty() || songArtist == "noArtist") "Unknown Artist" else songArtist

    // Trigger playback as soon as PlayerScreen is displayed or songUrl changes
    LaunchedEffect(songUrl) {
        MyExoplayer.playSong(
            context, SongModel(
                title = songTitle ?: "Unknown Title",
                id = "",
                artist = displayArtist,
                genre = "",
                url = songUrl,
                coverUrl = coverUrl
            )
        )
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.play_music_background),
            contentDescription = "Music Play Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        // UI Display
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Display the song title
            Text(
                modifier = Modifier.padding(10.dp),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 25.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                ),
                text = songTitle ?: "Unknown Title"
            )

            // Display the artist name
            Text(
                modifier = Modifier.padding(10.dp),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                ),
                text = displayArtist
            )

            // Display the cover image using the coverUrl
            coverUrl.let {
                GlideImage(
                    imageUrl = it,
                    contentDescription = "Song Cover",
                    modifier = Modifier
                        .size(150.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
            PlayerControls(exoPlayer)

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom)
            {
                Button(
                    onClick = {
                        navToMusicList()
                    }, modifier = Modifier
                        .padding(start=20.dp)
                        .border(
                            BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text("Songs")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navToGenreScreen()
                    }, modifier = Modifier
                        .padding(start=20.dp)
                        .border(
                            BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text("Genres")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navToHome()
                    }, modifier = Modifier
                        .padding(start=20.dp)
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
fun PlayerControls(exoPlayer: ExoPlayer) {
    val coroutineScope = rememberCoroutineScope()
    val playbackState = remember { mutableStateOf(ExoPlayer.STATE_IDLE) }
    val currentPlaybackPosition = remember { mutableStateOf(0L) }
    val duration = remember { mutableStateOf(0L) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                playbackState.value = state
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    coroutineScope.launch {
                        while (playbackState.value == ExoPlayer.STATE_READY && exoPlayer.isPlaying) {
                            currentPlaybackPosition.value = exoPlayer.currentPosition
                            duration.value = exoPlayer.duration
                            delay(1000)
                        }
                    }
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }
    val sliderPosition =
        if (duration.value > 0) currentPlaybackPosition.value.toFloat() / duration.value.toFloat() else 0f

    Column {
        Slider(
            value = sliderPosition,
            colors = SliderColors(
                activeTickColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTickColor = Color.White,
                inactiveTrackColor = Color.White,
                thumbColor = Color.White,
                disabledActiveTickColor = Color.White,
                disabledActiveTrackColor = Color.White,
                disabledInactiveTickColor = Color.White,
                disabledInactiveTrackColor = Color.White,
                disabledThumbColor = Color.White
            ),
            onValueChange = { newPosition ->
                val newPlaybackPosition = (newPosition * duration.value).toLong()
                exoPlayer.seekTo(newPlaybackPosition)
            },
            modifier = Modifier.padding(horizontal = 16.dp),
            onValueChangeFinished = {
            }
        )

        // Playback Controls
        PlaybackButtons(exoPlayer = exoPlayer, playbackState = playbackState.value)

        // Playback Speed Adjustment
        PlaybackSpeedAdjustment(exoPlayer = exoPlayer)
    }
}

@Composable
fun PlaybackSpeedAdjustment(exoPlayer: ExoPlayer) {
    Text(
        modifier = Modifier.padding(10.dp),
        style = TextStyle(
            color = Color.White,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
        ),
        text = "Playback Speed"
    )
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        listOf(0.5f, 1f, 2f).forEach { speed ->
            Button(
                onClick = { exoPlayer.setPlaybackParameters(PlaybackParameters(speed)) },
                modifier = Modifier
                    .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)

            )
            {
                Text("${speed}x")
            }
        }
    }
}

@Composable
fun PlaybackButtons(exoPlayer: ExoPlayer, playbackState: Int) {
    val isPlaying by rememberUpdatedState(newValue = exoPlayer.isPlaying)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {

        // Back Track Button
        IconButton(onClick = { exoPlayer.seekTo(exoPlayer.currentPosition - 10_000) }) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Back Track", tint = Color.White)
        }

        // Play/Pause Button
        IconButton(onClick = {
            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
        }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play", tint = Color.White
            )
        }

        // Fast Track Button
        IconButton(onClick = { exoPlayer.seekTo(exoPlayer.currentPosition + 10_000) }) {
            Icon(Icons.Default.SkipNext, contentDescription = "Fast Track", tint = Color.White)
        }
    }
}
