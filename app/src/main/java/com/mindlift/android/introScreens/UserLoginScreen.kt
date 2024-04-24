package com.mindlift.android.introScreens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.mindlift.android.R
import com.mindlift.android.UserViewModel

@Composable
fun UserLoginScreen(
    navigateToSignUp: ()->Unit,
    navigateToSignIn: ()->Unit,
    navigateToHome: ()->Unit,
    navigateToEntry: ()->Unit,
    userViewModel: UserViewModel){

    var userName by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val username = userViewModel.username.collectAsState()
    Log.i("UserLoginScreen", "User Name Value Is: "+ username.value)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.intro_screens_background_image),
            contentDescription = "Entry Screen Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 35.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
            )
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("User Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                firestore.collection("Users")
                    .whereEqualTo("UserName", userName)
                    .get()
                    .addOnSuccessListener { documents ->
                        if(documents.isEmpty) {
                            Toast.makeText(context, "Invalid Username ! Please Try Again", Toast.LENGTH_LONG).show()
                            Log.i("UserLoginScreen", "Invalid Username")
                            navigateToSignIn()
                            return@addOnSuccessListener
                        }
                        for (document in documents) {
                            val dbPassword = document.getString("Password") ?: ""
                            if(dbPassword==password) {
                                userViewModel.setUsername(userName)
                                Toast.makeText(context, "Logged In Successfully !", Toast.LENGTH_LONG).show()
                                Log.i("UserLoginScreen", "Successful Login")
                                navigateToHome()
                            } else
                            {
                                Toast.makeText(context, "Invalid Password ! Please Try Again", Toast.LENGTH_LONG).show()
                                Log.i("UserLoginScreen", "Invalid Password")
                                navigateToSignIn()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("UserLoginScreen", "Error! Firestore Error Fetching Documents", exception)
                    }

            },modifier = Modifier.border(BorderStroke(1.dp, Color.White), shape =
            RoundedCornerShape(20.dp))
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text(
                    text="Sign In",
                    style = TextStyle(color = Color.White))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text="Don't Have An Account ?",
                style = TextStyle(color = Color.White))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navigateToSignUp() },
                modifier = Modifier.border(BorderStroke(1.dp, Color.White), shape =
                RoundedCornerShape(20.dp))
                    .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text(
                    text="Sign Up",
                    style = TextStyle(color = Color.White))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text="Know About MindLift",
                style = TextStyle(color = Color.White))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navigateToEntry() },
                modifier = Modifier.border(BorderStroke(1.dp, Color.White), shape =
                RoundedCornerShape(20.dp))
                    .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text(
                    text="Back Home",
                    style = TextStyle(color = Color.White))
            }
        }
    }
}
