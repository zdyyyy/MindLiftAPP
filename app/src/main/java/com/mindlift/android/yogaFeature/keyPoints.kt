package com.mindlift.android.yogaFeature

val bodyPartsMap: Map<Int, String> = mapOf(
    0 to "nose",
    1 to "left eye (inner)",
    2 to "left eye",
    3 to "left eye (outer)",
    4 to "right eye (inner)",
    5 to "right eye",
    6 to "right eye (outer)",
    7 to "left ear",
    8 to "right ear",
    9 to "mouth (left)",
    10 to "mouth (right)",
    11 to "left shoulder",
    12 to "right shoulder",
    13 to "left elbow",
    14 to "right elbow",
    15 to "left wrist",
    16 to "right wrist",
    17 to "left pinky",
    18 to "right pinky",
    19 to "left index",
    20 to "right index",
    21 to "left thumb",
    22 to "right thumb",
    23 to "left hip",
    24 to "right hip",
    25 to "left knee",
    26 to "right knee",
    27 to "left ankle",
    28 to "right ankle",
    29 to "left heel",
    30 to "right heel",
    31 to "left foot index",
    32 to "right foot index"
)
val reverseBodyPartsMap: Map<String, Int> = bodyPartsMap.entries.associate { (key, value) -> value to key }

// definition of skeletal kinematics
val validKPPairs: List<Pair<String, String>> = listOf(

    // face edges
    Pair("nose", "left eye (inner)"),
    Pair("nose", "right eye (inner)"),
    Pair("left eye (inner)", "right eye (inner)"),
    Pair("nose", "mouth (left)"),
    Pair("nose", "mouth (right)"),
    Pair("mouth (left)", "mouth (right)"),

    // upper body
    Pair("left shoulder", "right shoulder"),
    Pair("left shoulder", "left hip"),
    Pair("left hip", "right hip"),
    Pair("right hip", "right shoulder"),
    Pair("left shoulder", "left elbow"),
    Pair("right shoulder", "right elbow"),
    Pair("left elbow", "left wrist"),
    Pair("right elbow", "right wrist"),

    // upper body to lower body
    Pair("left hip", "left knee"),
    Pair("right hip", "right knee"),
    Pair("left knee", "left ankle"),
    Pair("right knee", "right ankle"),

    )

// define landmarks types we need
// for yoga pose we need following key_points
val keyPoints: List<Int?> = listOf(
    // face points
    reverseBodyPartsMap["nose"],
    reverseBodyPartsMap["left eye (inner)"],
    reverseBodyPartsMap["right eye (inner)"],
    reverseBodyPartsMap["mouth (left)"],
    reverseBodyPartsMap["mouth (right)"],

    // upper body points
    reverseBodyPartsMap["left shoulder"],
    reverseBodyPartsMap["right shoulder"],
    reverseBodyPartsMap["left elbow"],
    reverseBodyPartsMap["right elbow"],
    reverseBodyPartsMap["left wrist"],
    reverseBodyPartsMap["right wrist"],
    reverseBodyPartsMap["left hip"],
    reverseBodyPartsMap["right hip"],

    // lower body points
    reverseBodyPartsMap["left knee"],
    reverseBodyPartsMap["right knee"],
    reverseBodyPartsMap["left ankle"],
    reverseBodyPartsMap["right ankle"]
)
