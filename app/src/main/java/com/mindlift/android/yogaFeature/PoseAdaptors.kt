package com.mindlift.android.yogaFeature

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min


// PlankPose
@Composable
fun PlankPose(poses: Map<String?, Pair<Float, Float>>, accuracyCallback: (Float)->Unit) {

    // Progress spinner
    var shoulder_hip_ankle by remember { mutableStateOf(false) }
    var shoulder_elbow_wrist by remember { mutableStateOf(false) }
    var elbow_shoulder_hip by remember { mutableStateOf(false) }


    fun Boolean.toInt() = if (this) 1 else 0
    val progress by remember {
        derivedStateOf {
            (shoulder_hip_ankle.toInt() + shoulder_elbow_wrist.toInt() + elbow_shoulder_hip.toInt()) / 3.0f
        }
    }

    var cumulativeProgress by remember { mutableStateOf(0f) }


    LaunchedEffect(progress) {
        // This block will execute whenever progress changes
        cumulativeProgress = progressUpdateLogic(cumulativeProgress, progress)
        // change variable by reference
        accuracyCallback(cumulativeProgress)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { max(progress, 0.01f) },
            color = Color.Green,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd),
            strokeWidth = 14.dp
        )

        var tip = ""
        if (!shoulder_hip_ankle){
            tip = "Level your thigh with respect to your upper body"
        }else if (!shoulder_elbow_wrist){
            tip = "Straighten your arms with respect to your shoulder"
        }else if (!elbow_shoulder_hip){
            tip = "Extend Arms upwards and engage your core"
        }else{
            tip = "Beautiful alignment! You've mastered the pose."
        }

        Text(
            text = "Tip: $tip",
            modifier = Modifier.offset(x = 20.dp, y = 700.dp).fillMaxWidth(0.75f),
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (edges in validKPPairs) {
                // get the poseIndices of edge KP start and edge KP end
                val uKP = poses[edges.first]
                val vKP = poses[edges.second]

                if (uKP != null && vKP != null) {
                    val u = Offset(uKP.first, uKP.second)
                    val v = Offset(vKP.first, vKP.second)
                    Log.e(
                        ContentValues.TAG,
                        "from(${edges.first}): ${u.x},${u.y} to (${edges.second}): ${v.x},${v.y}"
                    )

                    var colorFromCircle = Color.Blue
                    var colorToCircle = Color.Blue
                    if ("left" in edges.first) {
                        colorFromCircle = Color(0xFFFFA500)
                    }
                    if ("left" in edges.second) {
                        colorToCircle = Color(0xFFFFA500)
                    }

                    // Draw circles for points (larger circles colored in blue)
                    drawCircle(color = colorFromCircle, center = u, radius = 24f)
                    drawCircle(color = colorToCircle, center = v, radius = 24f)

                    // Draw white lines
                    drawLine(color = Color.White, start = u, end = v, strokeWidth = 12f)
                }
            }

            // first angle
            val angle1Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left hip"],
                lastPoint = poses["left ankle"]
            )
            val angle1Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right hip"],
                lastPoint = poses["right ankle"]
            )

            // second angle
            val angle2Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left elbow"],
                lastPoint = poses["left wrist"]
            )
            val angle2Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right elbow"],
                lastPoint = poses["right wrist"]
            )

            // third angle
            val angle3Left = getAngle(
                firstPoint = poses["left elbow"],
                midPoint = poses["left shoulder"],
                lastPoint = poses["left hip"]
            )
            val angle3Right = getAngle(
                firstPoint = poses["right elbow"],
                midPoint = poses["right shoulder"],
                lastPoint = poses["right hip"]
            )

            Log.e(ContentValues.TAG, "angles 1: ${angle1Left} ${angle1Right}")
            Log.e(ContentValues.TAG, "angles 2: ${angle2Left} ${angle2Right}")
            Log.e(ContentValues.TAG, "angles 3: ${angle3Left} ${angle3Right}")
            // correctness heuristics

            shoulder_hip_ankle = angle1Left in (170.0..190.0) || angle1Right in (170.0..190.0)
            shoulder_elbow_wrist = angle2Left in (170.0..190.0) || angle2Right in (170.0..190.0)
            elbow_shoulder_hip = angle3Left in (19.0..32.0) || angle3Right in (19.0..32.0)
        }
    }

}

// define for raised arm pose
@Composable
fun CobraArmPose(poses: Map<String?, Pair<Float, Float>>, accuracyCallback: (Float)->Unit) {

    // Progress spinner
    var shoulder_hip_ankle by remember { mutableStateOf(false) }
    var shoulder_elbow_wrist by remember { mutableStateOf(false) }


    fun Boolean.toInt() = if (this) 1 else 0
    val progress by remember {
        derivedStateOf {
            (shoulder_hip_ankle.toInt() + shoulder_elbow_wrist.toInt()) / 2.0f
        }
    }
    var cumulativeProgress by remember { mutableStateOf(0f) }


    LaunchedEffect(progress) {
        // This block will execute whenever progress changes
        cumulativeProgress = progressUpdateLogic(cumulativeProgress, progress)
        // change variable by reference
        accuracyCallback(cumulativeProgress)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { max(progress, 0.01f) },
            color = Color.Green,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd),
            strokeWidth = 14.dp
        )

        var tip = ""
        if (!shoulder_hip_ankle){
            tip = "Level your thighs with the ground and lift your upper body up"
        }else if (!shoulder_elbow_wrist) {
            tip = "Ensure a perpendicular alignment from shoulder to elbow to wrist"
        }else{
            tip = "Beautiful alignment! You've mastered the pose."
        }

        Text(
            text = "Tip: $tip",
            modifier = Modifier.offset(x = 20.dp, y = 700.dp).fillMaxWidth(0.75f),
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (edges in validKPPairs) {
                // get the poseIndices of edge KP start and edge KP end
                val uKP = poses[edges.first]
                val vKP = poses[edges.second]

                if (uKP != null && vKP != null) {
                    val u = Offset(uKP.first, uKP.second)
                    val v = Offset(vKP.first, vKP.second)
                    Log.e(
                        ContentValues.TAG,
                        "from(${edges.first}): ${u.x},${u.y} to (${edges.second}): ${v.x},${v.y}"
                    )

                    var colorFromCircle = Color.Blue
                    var colorToCircle = Color.Blue
                    if ("left" in edges.first) {
                        colorFromCircle = Color(0xFFFFA500)
                    }
                    if ("left" in edges.second) {
                        colorToCircle = Color(0xFFFFA500)
                    }

                    // Draw circles for points (larger circles colored in blue)
                    drawCircle(color = colorFromCircle, center = u, radius = 24f)
                    drawCircle(color = colorToCircle, center = v, radius = 24f)

                    // Draw white lines
                    drawLine(color = Color.White, start = u, end = v, strokeWidth = 12f)
                }
            }

            // first angle
            val angle1Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left hip"],
                lastPoint = poses["left ankle"]
            )
            val angle1Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right hip"],
                lastPoint = poses["right ankle"]
            )

            // second angle
            val angle2Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left elbow"],
                lastPoint = poses["left wrist"]
            )
            val angle2Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right elbow"],
                lastPoint = poses["right wrist"]
            )

            Log.e(ContentValues.TAG, "angles 1: ${angle1Left} ${angle1Right}")
            Log.e(ContentValues.TAG, "angles 2: ${angle2Left} ${angle2Right}")
            // correctness heuristics

            shoulder_hip_ankle = angle1Left in (90.0..150.0) || angle1Right in (90.0..150.0)
            shoulder_elbow_wrist = angle2Left in (75.0..180.0) || angle2Right in (75.0..180.0)
        }
    }
}

// raised arm pose
@Composable
fun RaisedArmPose(poses: Map<String?, Pair<Float, Float>>, accuracyCallback: (Float)->Unit) {

    // Progress spinner
    var shoulder_hip_ankle by remember { mutableStateOf(false) }
    var shoulder_elbow_wrist by remember { mutableStateOf(false) }


    fun Boolean.toInt() = if (this) 1 else 0
    val progress by remember {
        derivedStateOf {
            (shoulder_hip_ankle.toInt() + shoulder_elbow_wrist.toInt()) / 2.0f
        }
    }

    var cumulativeProgress by remember { mutableStateOf(0f) }


    LaunchedEffect(progress) {
        // This block will execute whenever progress changes
        cumulativeProgress = progressUpdateLogic(cumulativeProgress, progress)
        // change variable by reference
        accuracyCallback(cumulativeProgress)
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { max(progress, 0.01f) },
            color = Color.Green,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd),
            strokeWidth = 14.dp
        )

        var tip = ""
        if (!shoulder_hip_ankle){
            tip = "Stand Straight and lean backwards by engaging your core"
        }else if (!shoulder_elbow_wrist) {
            tip = "Stretch your arms backwards with palms facing each other"
        }else{
            tip = "Beautiful alignment! You've mastered the pose."
        }

        Text(
            text = "Tip: $tip",
            modifier = Modifier.offset(x = 20.dp, y = 700.dp).fillMaxWidth(0.75f),
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (edges in validKPPairs) {
                // get the poseIndices of edge KP start and edge KP end
                val uKP = poses[edges.first]
                val vKP = poses[edges.second]

                if (uKP != null && vKP != null) {
                    val u = Offset(uKP.first, uKP.second)
                    val v = Offset(vKP.first, vKP.second)
                    Log.e(
                        ContentValues.TAG,
                        "from(${edges.first}): ${u.x},${u.y} to (${edges.second}): ${v.x},${v.y}"
                    )

                    var colorFromCircle = Color.Blue
                    var colorToCircle = Color.Blue
                    if ("left" in edges.first) {
                        colorFromCircle = Color(0xFFFFA500)
                    }
                    if ("left" in edges.second) {
                        colorToCircle = Color(0xFFFFA500)
                    }

                    // Draw circles for points (larger circles colored in blue)
                    drawCircle(color = colorFromCircle, center = u, radius = 24f)
                    drawCircle(color = colorToCircle, center = v, radius = 24f)

                    // Draw white lines
                    drawLine(color = Color.White, start = u, end = v, strokeWidth = 12f)
                }
            }

            // first angle
            val angle1Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left hip"],
                lastPoint = poses["left ankle"]
            )
            val angle1Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right hip"],
                lastPoint = poses["right ankle"]
            )

            // second angle
            val angle2Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left elbow"],
                lastPoint = poses["left wrist"]
            )
            val angle2Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right elbow"],
                lastPoint = poses["right wrist"]
            )

            Log.e(ContentValues.TAG, "angles 1: ${angle1Left} ${angle1Right}")
            Log.e(ContentValues.TAG, "angles 2: ${angle2Left} ${angle2Right}")
            // correctness heuristics

            shoulder_hip_ankle = angle1Left in (90.0..180.0) || angle1Right in (90.0..180.0)
            shoulder_elbow_wrist = angle2Left in (175.0..195.0) || angle2Right in (175.0..195.0)
        }
    }
}


// raised arm pose
@Composable
fun PrayerPose(poses: Map<String?, Pair<Float, Float>>, accuracyCallback: (Float)->Unit) {

    // Progress spinner
    var shoulder_hip_ankle by remember { mutableStateOf(false) }
    var shoulder_elbow_wrist by remember { mutableStateOf(false) }


    fun Boolean.toInt() = if (this) 1 else 0
    val progress by remember {
        derivedStateOf {
            (shoulder_hip_ankle.toInt() + shoulder_elbow_wrist.toInt()) / 2.0f
        }
    }
    var cumulativeProgress by remember { mutableStateOf(0f) }


    LaunchedEffect(progress) {
        // This block will execute whenever progress changes

        cumulativeProgress = progressUpdateLogic(cumulativeProgress, progress)
        // change variable by reference
        accuracyCallback(cumulativeProgress)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { max(progress, 0.01f) },
            color = Color.Green,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd),
            strokeWidth = 14.dp
        )

        var tip = ""
        if (!shoulder_hip_ankle){
            tip = "Elongate your back and stretch your spine"
        }else if (!shoulder_elbow_wrist) {
            tip = "Place your palm together in front of your chest"
        }else{
            tip = "Beautiful alignment! You've mastered the pose."
        }

        Text(
            text = "Tip: $tip",
            modifier = Modifier.offset(x = 20.dp, y = 700.dp).fillMaxWidth(0.75f),
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (edges in validKPPairs) {
                // get the poseIndices of edge KP start and edge KP end
                val uKP = poses[edges.first]
                val vKP = poses[edges.second]

                if (uKP != null && vKP != null) {
                    val u = Offset(uKP.first, uKP.second)
                    val v = Offset(vKP.first, vKP.second)
                    Log.e(
                        ContentValues.TAG,
                        "from(${edges.first}): ${u.x},${u.y} to (${edges.second}): ${v.x},${v.y}"
                    )

                    var colorFromCircle = Color.Blue
                    var colorToCircle = Color.Blue
                    if ("left" in edges.first) {
                        colorFromCircle = Color(0xFFFFA500)
                    }
                    if ("left" in edges.second) {
                        colorToCircle = Color(0xFFFFA500)
                    }

                    // Draw circles for points (larger circles colored in blue)
                    drawCircle(color = colorFromCircle, center = u, radius = 24f)
                    drawCircle(color = colorToCircle, center = v, radius = 24f)

                    // Draw white lines
                    drawLine(color = Color.White, start = u, end = v, strokeWidth = 12f)
                }
            }

            // first angle
            val angle1Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left hip"],
                lastPoint = poses["left ankle"]
            )
            val angle1Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right hip"],
                lastPoint = poses["right ankle"]
            )

            // second angle
            val angle2Left = getAngle(
                firstPoint = poses["left shoulder"],
                midPoint = poses["left elbow"],
                lastPoint = poses["left wrist"]
            )
            val angle2Right = getAngle(
                firstPoint = poses["right shoulder"],
                midPoint = poses["right elbow"],
                lastPoint = poses["right wrist"]
            )

            Log.e(ContentValues.TAG, "angles 1: ${angle1Left} ${angle1Right}")
            Log.e(ContentValues.TAG, "angles 2: ${angle2Left} ${angle2Right}")
            // correctness heuristics

            shoulder_hip_ankle = angle1Left in (175.0..195.0) || angle1Right in (175.0..195.0)
            shoulder_elbow_wrist = angle2Left in (5.0..85.0) || angle2Right in (5.0..85.0)
        }
    }
}

// logic on how to handle changes in per-pose accuracies in single session for any day
fun progressUpdateLogic(cumVal: Float, newVal: Float):Float{
    return ((min(cumVal*2, 2.0f) + newVal)/3.0f + max(cumVal, newVal))/2.0f
}