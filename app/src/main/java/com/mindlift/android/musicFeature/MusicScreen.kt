package com.mindlift.android.musicFeature

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.music.model.MusicNavViewModel
import com.mindlift.android.R
import com.mindlift.android.UserViewModel

@Composable
fun MusicScreen(
    userViewModel: UserViewModel,
    musicNavViewModel: MusicNavViewModel,
    navigateToHome: () -> Unit,
    navigateToMusicList: () -> Unit,
    navigateToMusicPlayer: () -> Unit
) {
    val navController = rememberNavController()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.music_screen_microphone_background_image),
            contentDescription = "Entry Screen Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Adjust content scale as needed
        )
        MusicScreenComposable(
            navController, userViewModel, musicNavViewModel,
            navigateToMusicList, navigateToMusicPlayer
        )

        Button(
            onClick = {
                navigateToHome()
            }, modifier = Modifier
                .padding(bottom = 10.dp)
                .border(
                    BorderStroke(1.dp, Color.White), shape =
                    RoundedCornerShape(20.dp)
                )
                .background(Color.Transparent)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text("Home")
        }
    }
}
