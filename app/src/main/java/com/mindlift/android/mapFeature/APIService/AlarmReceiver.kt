package com.mindlift.android.mapFeature.APIService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        FirestoreService.fetchHighestRatedPlace(context)
        Log.d("AlarmReceiver", "Alarm Received")
    }
}
