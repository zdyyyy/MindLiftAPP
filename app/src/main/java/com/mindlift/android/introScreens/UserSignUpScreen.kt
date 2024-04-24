package com.mindlift.android.introScreens

import android.content.Context
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mindlift.android.R

@Composable
fun UserSignUpScreen(
    navigateToLogin: ()->Unit,
    navigateToEntry: ()->Unit){

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
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
                text = "Sign Up",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 35.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                )
                )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
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
                signUpUser(firstName, lastName, email, userName, password, context)
                navigateToLogin()
            }, modifier = Modifier.border(
                BorderStroke(1.dp, Color.White), shape =
            RoundedCornerShape(20.dp)
            )
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text="Already Have An Account ?",
                style = TextStyle(color = Color.White))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navigateToLogin()
            },modifier = Modifier.border(BorderStroke(1.dp, Color.White), shape =
            RoundedCornerShape(20.dp))
                .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(Color.Transparent)){
                Text("Sign In")
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

fun signUpUser(firstName: String, lastName: String, email: String, userName: String, password: String, context: Context) {
    val db = Firebase.firestore
    val user = hashMapOf(
        "FirstName" to firstName,
        "LastName" to lastName,
        "eMail" to email,
        "UserName" to userName,
        "Password" to password
    )
    db.collection("Users")
        .document(userName)
        .set(user)
        .addOnSuccessListener {
            Log.i("UserSignUpScreen", "Inserted User")
        }
        .addOnFailureListener { exception ->
            Log.e("UserSignUpScreen", "Error! Inserting User", exception)
        }
    Toast.makeText(context, "SignUp Successful! Sign In To Enjoy Our Services!", Toast.LENGTH_LONG).show()
}
