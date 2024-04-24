package com.mindlift.android.yogaFeature

import android.os.Build
import android.util.Half.toFloat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.mindlift.android.R
import com.mindlift.android.UserViewModel
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollablePoses(
                    userViewModel: UserViewModel,
                    yogaPoseViewModel: YogaPoseViewModel,
                    navigateToPose: ()->Unit) {

    // create observables for the fetched percentages

    var plankPoseProg by remember{mutableStateOf(0.0f)}
    var prayerPoseProg by remember{mutableStateOf(0.0f)}
    var raisedArmPose by remember{ mutableStateOf(0.0f) }
    var cobraPose by remember{ mutableStateOf(0.0f) }


    val keyDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    firestore.collection("Users")
        .whereEqualTo("UserName", userViewModel.username.value)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents){
                if (document!=null){
                    val yogaStats = document["YogaStats"] as HashMap<String, Any>?
                    if (yogaStats!=null){
                        val yogaAccuracies = yogaStats[keyDate] as HashMap<String, Double>?
                        if (yogaAccuracies!=null){
                            // set the yoga accuracies
                            poses.forEach{ poseName ->
                                Log.e("ForDebug", "${yogaAccuracies[poseName]}")
                                val floatAcc = yogaAccuracies[poseName]?.toFloat()
                                when(poseName){
                                    "Dhadhasana (Plank Pose)" -> plankPoseProg = floatAcc ?: 0.0f
                                    "Pranamasana (Prayer Pose)" -> prayerPoseProg = floatAcc ?: 0.0f
                                    "Hasta Uttanasana (Raised Arms Pose)" -> raisedArmPose = floatAcc ?: 0.0f
                                    "Bhujangasana (Cobra Pose)" -> cobraPose = floatAcc ?: 0.0f
                                }
                            }
                        }
                    }
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("YogaEntryScreen", "Error! Firestore Error Fetching YogaStats", exception)
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.yogabackground),
            contentDescription = "yoga background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Adjust content scale as needed
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val cardItems = poses.size
            items(cardItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(395.dp)
                        .padding(vertical = 8.dp)
                        .clickable{
                            // the change the view Model value based on the clicked string
                            yogaPoseViewModel.yogaPose = poses[item]
                            // then navigate to the corresponding pose
                            navigateToPose()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0.5f, 0.5f, 0.5f, 0.5f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.DarkGray
                    )

                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Pose Heading
                        Text(
                            text = poses[item],
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                )
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(
                                modifier = Modifier
                                    .weight(2f) // Takes half of the available space
                                    .padding(horizontal = 8.dp)
                            ){
                                Image(
                                    painter = painterResource(id = pose2image[poses[item]] ?: R.drawable.dhadhasana),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Takes half of the available space
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ){
                                // get this from DB..
                                val progress = when(poses[item]){
                                    "Dhadhasana (Plank Pose)" -> plankPoseProg
                                    "Pranamasana (Prayer Pose)" -> prayerPoseProg
                                    "Hasta Uttanasana (Raised Arms Pose)" -> raisedArmPose
                                    "Bhujangasana (Cobra Pose)" -> cobraPose
                                    else -> {plankPoseProg}
                                }
                                CircularProgressbar(
                                    name = "Avg. Accuracy",
                                    dataUsage = progress*100
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${yogaPoses[poses[item]]}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

