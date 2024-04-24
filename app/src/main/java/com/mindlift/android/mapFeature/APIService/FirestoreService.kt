package com.mindlift.android.mapFeature.APIService

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mindlift.android.MainActivity
import com.mindlift.android.R
import com.mindlift.android.mapFeature.Database.Review
import java.util.Calendar

object FirestoreService {
    val db by lazy { Firebase.firestore }

    fun submitReview(review: Review, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reviews").add(review)
            .addOnSuccessListener {
                Log.d("FirestoreService", "Review successfully written!")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreService", "Error writing review", exception)
                onFailure(exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun fetchHighestRatedPlace(context: Context) {
        val db = Firebase.firestore
        db.collection("reviews")
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val highestRatedReview = documents.documents[0].toObject(Review::class.java)
                    highestRatedReview?.let {
                        Log.d("Firestore", "Highest rated place: ${it.place} with rating ${it.rating}")
                        sendPushNotification("Best Place", "Place ${it.place} gets the highest score: ${it.rating}",context)
                    }
                } else {
                    Log.d("Firestore", "No reviews found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendPushNotification(title: String, message: String, context: Context) {
        val channelId = "review_notifications"
        val notificationId = (System.currentTimeMillis() and 0xfffffff).toInt() // Unique ID for each notification

        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(title)
            setContentText(message)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(true)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d("Push Notification","Notify Successfully!")
    }

    fun scheduleDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 10)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, 1)
        }


        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            60 * 1000,
            pendingIntent
        )

        // If the current time has exceeded 10 o'clock today, the first trigger time is set to 10 o'clock tomorrow
//        if (calendar.timeInMillis < System.currentTimeMillis()) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1)
//        }
//
//        alarmManager.setInexactRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
    }

}
