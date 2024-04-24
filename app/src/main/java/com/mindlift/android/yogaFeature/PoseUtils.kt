package com.mindlift.android.yogaFeature

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.impl.utils.TransformUtils
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetector
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.atan2
import kotlin.math.min


// PoseAnalyzer class for analysis of poses, which inherits ImageAnalysis.Analyzer

class PoseAnalyzer(
    private val mTargetCoordinateSystem: Int,
    private val poseDetector: PoseDetector,
    private val onPoseDetected: (Map<String?, Pair<Float, Float>>) -> Unit
) : ImageAnalysis.Analyzer {

    // declare a private variable for the target matrix
    // converting to late init as it cant be initialized here in its declaration
    private lateinit var mSensorToTarget: Matrix

    @SuppressLint("RestrictedApi")
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {

            // this is an MLKit object (fromMediaImage)
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            ////// from MLKit Analyzer applying transformations
            val analysisToTarget = Matrix()
            if (mTargetCoordinateSystem != ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL) {
                val sensorToTarget = mSensorToTarget
                if (sensorToTarget == null) {
                    // If the app set a target coordinate system, do not perform detection until the
                    // transform is ready.
                    Log.d(ContentValues.TAG, "Transform is null.")
                    imageProxy.close()
                    return
                }
                val sensorToAnalysis =
                    Matrix(imageProxy.imageInfo.sensorToBufferTransformMatrix)
                // width, height: 640 x 480 (4:3) is the default Image Analysis image width, height
                Log.e(
                    ContentValues.TAG,
                    "SensorToBufferTransformMatrix: ${sensorToAnalysis.toString()} proxy width, height: ${imageProxy.width},${imageProxy.height}"
                )
                // Calculate the rotation added by ML Kit.

                // 640x480
                val sourceRect = RectF(
                    0f, 0f, imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
                // 480x640 (after rotation)
                val bufferRect = TransformUtils.rotateRect(
                    sourceRect,
                    imageProxy.imageInfo.rotationDegrees
                )
                // get the transformation matrix to go from sourceRect to bufferRect
                val analysisToMlKitRotation = TransformUtils.getRectToRect(
                    sourceRect, bufferRect,
                    imageProxy.imageInfo.rotationDegrees
                )
                // Concat the MLKit transformation with sensor to Analysis.
                // 480x640
                sensorToAnalysis.postConcat(analysisToMlKitRotation)
                // Invert to get analysis to sensor. (identity matrix, because analysis
                // dim is 640x480 and so is target dimension by default
                // to change target resolution, ImageAnalysis.setTargetResolution
                // or from the camera controller setTargetResolutionSelector as we are using it

                // analysisToTarget is set as inverse of sensorToAnalysis
                sensorToAnalysis.invert(analysisToTarget)
                // Concat sensor to target to get analysisToTarget.
                analysisToTarget.postConcat(sensorToTarget)

            }
            Log.e(ContentValues.TAG, "${analysisToTarget.toString()}")
            //////

            poseDetector.process(image)
                .addOnSuccessListener { results ->

                    val posePoints = mutableMapOf<String?, Pair<Float, Float>>()
                    val transformedPosePoints = mutableMapOf<String?, Pair<Float, Float>>()

                    for (poseLandmark in results.getAllPoseLandmarks()) {
                        // add the points only if its confidence is above 95%
                        if (poseLandmark.inFrameLikelihood >= 0.85 && poseLandmark.landmarkType in keyPoints) {
                            posePoints.put(
                                bodyPartsMap[poseLandmark.landmarkType],
                                Pair(poseLandmark.position.x, poseLandmark.position.y)
                            )
                        }
                    }

                    // Log.e(TAG, "Image Proxy dimensions: ${imageProxy.width} ${imageProxy.height}")
                    // 640, 480 and rotation given by image Proxy is 90 degrees
                    if (posePoints.all { it != null }) {

                        // create copy
                        val matrix = Matrix(analysisToTarget)
                        matrix.postRotate(90F)
                        matrix.postScale(-2F, 2F)
                        matrix.postTranslate(-175F, -170F)
                        val transformedPoints: MutableList<Float> = mutableListOf()
                        for ((_, pointsPair) in posePoints) {
                            // Map the points using the transformation matrix
                            transformedPoints.add(pointsPair.first)
                            transformedPoints.add(pointsPair.second)
                        }
                        val transformedPPoints = transformedPoints.toFloatArray()
                        matrix.mapPoints(transformedPPoints)


                        for ((index, pose) in posePoints.keys.withIndex()) {
                            transformedPosePoints[pose] = Pair(
                                transformedPPoints[index * 2],
                                transformedPPoints[index * 2 + 1]
                            )
                        }
                    }
                    // update the UI
                    // need to transform the cordinates to that of the system
                    onPoseDetected(transformedPosePoints)

                }
                .addOnFailureListener { e ->
                    // Handle detection failure
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    override fun updateTransform(matrix: Matrix?) {
        mSensorToTarget = Matrix(matrix)
        // updateTransform returns the Identity Matrix
        Log.e(ContentValues.TAG, "Got the msensor target ${mSensorToTarget.toString()}")
    }

}

// Utility for computing the angles
fun getAngle(
    firstPoint: Pair<Float, Float>?,
    midPoint: Pair<Float, Float>?,
    lastPoint: Pair<Float, Float>?
): Double {
    if (firstPoint == null || midPoint == null || lastPoint == null) {
        return 0.0
    }
    val firstAngle = atan2(lastPoint.second - midPoint.second, lastPoint.first - midPoint.first)
    val secondAngle =
        atan2(firstPoint.second - midPoint.second, firstPoint.first - midPoint.first)
    var result = Math.toDegrees(firstAngle.toDouble() - secondAngle.toDouble())

    result = Math.abs(result) // Angle should never be negative
    if (result > 180) {
        result = 360.0 - result // Always get the acute representation of the angle
    }
    return result
}

// defining a viewModel for YogaPoses
class YogaPoseViewModel : ViewModel() {
    var yogaPose: String = ""
}


// define suspend functions for DB operations
@RequiresApi(Build.VERSION_CODES.O)
suspend fun updateAccForPose(poseAccs: HashMap<String, Float>, userName: String){
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val keyDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val userDocumentReference = firestore.collection("Users").document(userName)

    // update by computing macro avg of values observed in same day
    fun updateLogic(hm: HashMap<String, Float>): HashMap<String, Float>{
        val newHM = HashMap<String, Float>()
        hm.forEach { (key, oldValue) ->
            var newVal = (min(oldValue*2.0f,2.0f) + (poseAccs[key] ?: 0.0f))/3.0f
            if (poseAccs[key] == 0.0f){
                newVal = oldValue
            }
            // set the new value
            newHM[key] = newVal
        }
        return newHM
    }
    try {
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDocumentReference)
            val yogaEntries = snapshot.get("YogaStats") as? HashMap<String, HashMap<String, Float>> ?: hashMapOf()

            val emptyInstance = yogaPosesHM()
            val yogaEntryToday = yogaEntries[keyDate] ?: emptyInstance
            yogaEntries[keyDate] = if (yogaEntryToday === emptyInstance) poseAccs else updateLogic(yogaEntryToday as HashMap<String, Float>)
            // perform the transaction
            transaction.set(userDocumentReference, hashMapOf("YogaStats" to yogaEntries), SetOptions.merge())
        }.await()
        // await is called to suspend the coroutine /block it until db operation completes
        // coroutine scopes can be considered as separated from main , concurrent threads
        // so this block doesnt effect the main thread but just makes the DB call
        // inside the synchronous execution of the coroutine scope , synchronous..
        Log.d("YogaAccUpdate", "Yoga Update happened successfully !")
    }  catch (exception: Exception) {
        Log.e("YogaAccUpdate", exception.message.toString())
    }
}