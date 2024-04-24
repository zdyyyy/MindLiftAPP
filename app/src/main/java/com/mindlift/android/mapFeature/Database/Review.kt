package com.mindlift.android.mapFeature.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val placeId: String?,
//    val place: String,
//    val reviewText: String,
//    val rating: Int
    var placeId: String? = "",
    var place: String = "",
    var reviewText: String = "",
    var rating: Int = 0
)
