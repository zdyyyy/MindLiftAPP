package com.mindlift.android.introScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindlift.android.R
import com.mindlift.android.UserViewModel

@Composable
fun homeScreen(
    navigateToMusic: ()->Unit,
    navigateToDiary: ()->Unit,
    navigateToYoga: ()->Unit,
    navigateToMaps: ()->Unit,
    navigateToLogin: ()->Unit,
    userViewModel: UserViewModel){

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.home_back),
            contentDescription = "yoga background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Adjust content scale as needed
        )

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                HomeCards("Harmony Haven","Find Calmness with Music Therapy", navigateToMusic)
                HomeCards("Balance and Bliss","Connect Body and Soul with Yoga", navigateToYoga)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeCards("Reflections of Resilience","Embrace Healing Through Journaling", navigateToDiary)
                HomeCards("Pathways to Wellness","Explore Nearby Sanctuaries for Mindful Healing", navigateToMaps)
            }

            Button(onClick = {
                userViewModel.clearUsername()
                navigateToLogin()
            }) {
                Text("Sign Out")
            }
        }
    }

}

@Composable
fun HomeCards(heading:String, description:String, navCallback: ()-> Unit){
    // music
    Card(
        modifier = Modifier
            .size(200.dp)
            .padding(8.dp)
            .clickable(onClick = { navCallback() }),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0.5f, 0.5f, 0.5f, 0.5f),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = heading,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = description,
                style = TextStyle(
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
        }

}