package com.mindlift.android.yogaFeature

import com.mindlift.android.R


val poses = listOf<String>(
    "Dhadhasana (Plank Pose)",
    "Pranamasana (Prayer Pose)",
    "Hasta Uttanasana (Raised Arms Pose)",
    "Bhujangasana (Cobra Pose)"
)

val yogaPoses = mapOf(
    "Dhadhasana (Plank Pose)" to "Stand, bend knees like sitting, keeping thighs parallel. Arms extend upward, palms facing, back straight, engaging core. Strengthens legs, improves posture, enhances concentration.",
    "Pranamasana (Prayer Pose)" to "Stand at the front of your mat, palms pressed together in front of the chest, and feet together or hip-width apart. It symbolizes acknowledging the divine within oneself.",
    "Hasta Uttanasana (Raised Arms Pose)" to "Inhale, raise both arms up, arching the back slightly, and gently tilting the head backward. It stretches the arms, shoulders, and chest.",
    "Bhujangasana (Cobra Pose)" to "Inhale, slide forward and raise the chest and gaze upward, keeping the elbows bent and close to the body. It strengthens the back muscles and opens the chest."
)

val pose2image = mapOf(
    "Dhadhasana (Plank Pose)" to R.drawable.dhadhasana,
    "Pranamasana (Prayer Pose)" to R.drawable.pranamasana,
    "Hasta Uttanasana (Raised Arms Pose)" to R.drawable.uttanasana,
    "Bhujangasana (Cobra Pose)" to R.drawable.cobrapose
)

fun yogaPosesHM(): HashMap<String, Double> {
    val yogaPosesMap = hashMapOf<String, Double>()

    // Initialize each YogaPose with a default accuracy of 0.0
    yogaPosesMap["Dhadhasana (Plank Pose)"] = 0.0
    yogaPosesMap["Pranamasana (Prayer Pose)"] = 0.0
    yogaPosesMap["Hasta Uttanasana (Raised Arms Pose)"] = 0.0
    yogaPosesMap["Bhujangasana (Cobra Pose)"] = 0.0

    return yogaPosesMap
}