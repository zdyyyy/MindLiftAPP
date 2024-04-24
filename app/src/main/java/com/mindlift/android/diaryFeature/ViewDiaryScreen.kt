package com.mindlift.android.diaryFeature

import android.annotation.SuppressLint
import com.mindlift.android.UserViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.mindlift.android.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
class DateViewModel : ViewModel() {
    var selectedDate: String by mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        private set

    fun updateDate(year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month + 1, day)
        selectedDate = date.format(DateTimeFormatter.ISO_DATE)
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewDiaryScreen(navigateToHome: ()->Unit, navigateToDiaryScreen: ()-> Unit, userViewModel: UserViewModel, viewModel: DateViewModel = viewModel()) {
    val context = LocalContext.current
    val username = userViewModel.username.collectAsState().value
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }
    val (notes, setNotes) = remember { mutableStateOf("Select A Date To View Diary Notes") }
    val scrollState = rememberScrollState()

    if (showDatePicker) {
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue - 1
        val currentDay = LocalDate.now().dayOfMonth

        DisposableEffect(Unit) {
            val datePickerDialog = DatePickerDialog(

                context,
                { _, year, monthOfYear, dayOfMonth ->
                    viewModel.updateDate(year, monthOfYear, dayOfMonth)
                    setShowDatePicker(false)
                },
                currentYear,
                currentMonth,
                currentDay
            )
            datePickerDialog.show()

            onDispose {
                datePickerDialog.dismiss()
            }
        }
    }

    LaunchedEffect(viewModel.selectedDate) {
        setNotes("Fetching Diary Notes ...")
        setNotes(fetchDiaryNotes(username, viewModel.selectedDate))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.view_diary_screen_background_image),
            contentDescription = "View Diary Screen Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Adjust content scale as needed
        )
        Column(modifier = Modifier.padding(55.dp) .verticalScroll(scrollState)) {
            Button(onClick = { setShowDatePicker(true) }, modifier = Modifier.border(
                BorderStroke(1.dp, Color.White), shape =
                RoundedCornerShape(20.dp)
            )
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text("Diary Notes On ...")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Date ${viewModel.selectedDate}", color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                notes,
                color = Color.Black,
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp))

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text="Make A Diary Entry",
                style = TextStyle(color = Color.White))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navigateToDiaryScreen()
            },modifier = Modifier.border(
                BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(20.dp)
            )
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text("Speak Out My Thoughts")
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
        }
    }
}


suspend fun fetchDiaryNotes(userName: String, date: String):String {
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    try {
        val documents = firestore.collection("Users")
            .whereEqualTo("UserName", userName)
            .get()
            .await()
        for (document in documents) {
            val diary = document.get("DiaryNotes")as? HashMap<String, String> ?: hashMapOf()
            Log.i("ViewDiaryScreen", "Fetched Diary Notes")
            return diary[date] ?: "No Diary Notes on $date"
        }
    }  catch (exception: Exception) {
        Log.e("ViewDiaryScreen", exception.message.toString())
        return "No Diary Notes on $date"
    }
    return "No Diary Notes on $date"
}
