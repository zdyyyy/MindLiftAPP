package com.mindlift.android.diaryFeature

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mindlift.android.R
import com.mindlift.android.UserViewModel
import com.mindlift.android.gptAPIService.GPTRequestParameters
import com.mindlift.android.gptAPIService.GPTRequestBody
import com.mindlift.android.gptAPIService.OpenAIApiService
import com.mindlift.android.gptAPIService.GPTResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiaryScreen(navigateToHome: ()->Unit, navigateToViewDiary: ()-> Unit, userViewModel: UserViewModel){
    val audioPermission: PermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val launchPermission = audioPermission::launchPermissionRequest
    if (audioPermission.status.isGranted) {
        val username = userViewModel.username.collectAsState()
        Log.i("DiaryScreen", "User Name Value Is: "+username.value)
        GetUserDiaryNotes(navigateToHome, navigateToViewDiary, userViewModel)
    }
    else{
        GetUserMicrophonePermission(launchPermission)
    }
}

@Composable
fun GetUserMicrophonePermission(onRequest: ()->Unit){
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text(text = "Please grant permissions for Microphone access")
        Button(onClick = {
            onRequest()
        }){
            Icon(imageVector = Icons.Default.Mic
                , contentDescription = "Microphone")
            Text(text = "Grant Permission")
        }
    }
}

@Composable
fun GetUserDiaryNotes(navigateToHome: ()->Unit, navigateToViewDiary: ()-> Unit, userViewModel: UserViewModel) {
    val context = LocalContext.current
    var userDiaryText by remember { mutableStateOf("") }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.diary_screen_microphone_background_image),
            contentDescription = "Entry Screen Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Adjust content scale as needed
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text ="Speak Out Your Thoughts In The Mic",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                speechRecognizer.startListening(recognizerIntent)
            }, modifier = Modifier.border(
                BorderStroke(1.dp, Color.White), shape =
                RoundedCornerShape(20.dp)
            )
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Icon(imageVector = Icons.Default.Mic
                    , contentDescription = "Microphone")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text="View My Diary",
                style = TextStyle(color = Color.White))

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navigateToViewDiary()
            },modifier = Modifier.border(
                BorderStroke(1.dp, Color.White), shape =
                RoundedCornerShape(20.dp)
            )
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text("My Diary")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text="Explore Other Features",
                style = TextStyle(color = Color.White))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navigateToHome()
            },modifier = Modifier.border(
                BorderStroke(1.dp, Color.White), shape =
                RoundedCornerShape(20.dp)
            )
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text("Home")
            }

            val listener = object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {}
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResults(results: Bundle?) {
                    val userDiaryInput = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (userDiaryInput != null) {
                        userDiaryText = userDiaryInput[0]
                        insertOrUpdateDiaryInputToDatabase(userViewModel.username.value, userDiaryText)
                        predictMode(userViewModel.username.value, userDiaryText)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            }
            speechRecognizer.setRecognitionListener(listener)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun insertOrUpdateDiaryInputToDatabase(userName: String, diaryNotes:String){
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val diaryNotesKeyDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val userDocumentReference = firestore.collection("Users").document(userName)

    firestore.runTransaction { transaction ->
        val snapshot = transaction.get(userDocumentReference)
        val diaryEntries = snapshot.get("DiaryNotes") as? HashMap<String, String> ?: hashMapOf()
        val diaryEntryNotesToday = diaryEntries[diaryNotesKeyDate] ?: ""
        diaryEntries[diaryNotesKeyDate] = if (diaryEntryNotesToday.isEmpty()) diaryNotes else "$diaryEntryNotesToday\n$diaryNotes"
        transaction.set(userDocumentReference, hashMapOf("DiaryNotes" to diaryEntries), SetOptions.merge())
    }.addOnSuccessListener {
        Log.i("DiaryScreen", "Diary entry added or updated successfully")
    }.addOnFailureListener { exception ->
        Log.e("DiaryScreen", "Error updating diary entry", exception)
    }
}

object RetrofitClient {
    val webservice: OpenAIApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/") // Base URL for the OpenAI API
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(OpenAIApiService::class.java)
    }
}

fun predictMode(userName: String, diaryNotes:String)
{
    val prompt ="I am going to share my thoughts, I want you to predict the mood based on the text. The response you give should be one of the four words 'happy', 'sad', 'fear', 'angry'. Make sure the response is only one of the four words. Basically you are predicting how I am feeling based on my diary notes. My notes are $diaryNotes"
    val messages = listOf(
        GPTRequestBody(role = "system", content = "You should act like mood predictor, return only only word based on thee users prompt"),
        GPTRequestBody(role = "user", content = prompt)
    )
    val gptRequestParameters = GPTRequestParameters(messages = messages)
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    RetrofitClient.webservice.getGPTResponse(gptRequestParameters).enqueue(object : Callback<GPTResponse> {
        override fun onResponse(call: Call<GPTResponse>, response: Response<GPTResponse>) {
            if (response.isSuccessful) {

                var mood = response.body()?.choices?.firstOrNull()?.message?.content
                if(mood !in setOf("happy", "sad", "angry", "fear")) {
                    mood = "happy"
                }
                Log.i("DiaryScreen", "$mood")

                firestore.collection("Users")
                    .document(userName)
                    .update(mapOf("Mood" to mood))
                    .addOnSuccessListener { Log.i("DiaryScreen", "Diary entry added or updated successfully") }
                    .addOnFailureListener { e ->
                        Log.e("DiaryScreen", e.message.toString())
                    }

            } else {
                Log.e("DiaryScreen", "API Call Failed! ${response.errorBody()?.string()}")
            }
        }
        
        override fun onFailure(call: Call<GPTResponse>, t: Throwable) {
            println("API call failed: ${t.message}")
        }
    })
}
