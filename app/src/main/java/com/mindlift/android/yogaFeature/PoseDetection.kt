package com.mindlift.android.yogaFeature

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
//import android.graphics.Color
import androidx.compose.ui.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.impl.utils.TransformUtils
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlin.math.atan2
import kotlin.math.max
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.mindlift.android.UserViewModel
import kotlinx.coroutines.launch

// composable defined for camera permissions enabling
@Composable
fun CameraPermission(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Please grant permissions for Camera access")
        Button(onClick = {
            onRequest()
        }) {
            Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera")
            Text(text = "Grant Permission")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(yogaPoseViewModel: YogaPoseViewModel,
                 userViewModel: UserViewModel,
                 navigateToHome: () -> Unit,
                 navigateToYogaHome: () -> Unit) {
    // permissions library
    val cameraPermission: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraController = remember { LifecycleCameraController(context) }
    cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    // launch permission requires function
    val launch_permission_fnc = cameraPermission::launchPermissionRequest


    // calling vision api for pose detection and retrieving keypoints of the user
    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    val poseDetector = PoseDetection.getClient(options)

    var keyPoints by remember { mutableStateOf<Map<String?, Pair<Float, Float>>>(mutableMapOf()) }

    // create a variable to store average accuracy of person pose over for the particular day
    // need to load for the current day from the database (store day, total_score, total_entries)
    var plankPoseAccuracy by remember { mutableStateOf(0.0f) }
    var prayerPoseAccuracy by remember { mutableStateOf(0.0f) }
    var raisedArmsPoseAccuracy by remember { mutableStateOf(0.0f) }
    var cobraPoseAccuracy by remember { mutableStateOf(0.0f) }

    fun createPoseDataMap(): HashMap<String, Float> {
        return hashMapOf(
            "Dhadhasana (Plank Pose)" to plankPoseAccuracy,
            "Pranamasana (Prayer Pose)" to prayerPoseAccuracy,
            "Hasta Uttanasana (Raised Arms Pose)" to raisedArmsPoseAccuracy,
            "Bhujangasana (Cobra Pose)" to cobraPoseAccuracy
        )
    }

    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(), //contentAlignment = Alignment.Center
    ) {

        // check if permission granted
        if (cameraPermission.status.isGranted) {
            Box(
                modifier = Modifier
                    .width(640.dp)
                    .height(640.dp)
                    .offset(0.dp, 0.dp)
            )
            {

                AndroidView(
                    modifier = Modifier
                        .fillMaxSize(),
                    factory = { context ->
                        PreviewView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            // if camera not open and loading
                            setBackgroundColor(android.graphics.Color.BLACK)
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }.also { previewView ->

                            // set image analyzer for cameraController
                            cameraController.setImageAnalysisAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                PoseAnalyzer(
                                    CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                                    poseDetector
                                ) { x ->
                                    keyPoints = x
                                }
                            )
                            // image proxy resolution when width and height before rotation comes to be 1280 960
                            // we have to set output size correctly which is after rotation here then image proxy becomes size 640,480
                            // we could also set target rotation, but then the imageProxy rotation would be set to this value
                            // instead we take target rotation as default rotation required to orient image correctly based
                            // on device.

                            previewView.controller = cameraController
                            // previewView.rotation = 90F
                            cameraController.bindToLifecycle(lifecycleOwner)

                        }
                    }
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                // Button to go back to home
                Button(
                    onClick = {
                        // store the updated accuracy in DB by creating a co-routine scope
                        coroutineScope.launch {
                            val allPoseData: HashMap<String, Float> = createPoseDataMap()
                            // call suspend function so that screen is only navigated
                            // on its completion
                            updateAccForPose(allPoseData, userViewModel.username.value)
                            // navigate to YogaHome after suspend function resumes
                            navigateToYogaHome()
                        }
                    }
                ) {
                    Text("Poses")
                }

                // Existing button (assumed to be inside the Box)
                Button(
                    onClick = {
                        // store the updated accuracy in DB by creating a co-routine scope
                        coroutineScope.launch {
                            val allPoseData: HashMap<String, Float> = createPoseDataMap()
                            // call suspend function so that screen is only navigated
                            // on its completion
                            updateAccForPose(allPoseData, userViewModel.username.value)
                            // navigate to YogaHome after suspend function resumes
                            navigateToHome()
                        }
                    },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text("Home")
                }
            }
            // run pose analysis and overlay for specific pose types
            when(yogaPoseViewModel.yogaPose){
                "Dhadhasana (Plank Pose)" -> PlankPose(keyPoints){ x->
                    plankPoseAccuracy = x
                }
                "Pranamasana (Prayer Pose)" -> PrayerPose(keyPoints){ x->
                    prayerPoseAccuracy = x
                }
                "Hasta Uttanasana (Raised Arms Pose)" -> RaisedArmPose(keyPoints){ x->
                    raisedArmsPoseAccuracy = x
                }
                "Bhujangasana (Cobra Pose)" -> CobraArmPose(keyPoints){ x->
                    cobraPoseAccuracy = x
                }
            }
        } else {
            CameraPermission(launch_permission_fnc)
        }
    }
}

