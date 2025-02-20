package com.vtest.video_test.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val videoUrl: String,
    val title: String,
    val duration: String,
    val thumbnailUrl: String?,
    val lastPosition: Long = 0L
) {

    fun getDurationMillis(): Long {
        val parts = duration.split(":").map { it.toIntOrNull() ?: 0 }
        return if (parts.size == 2) (parts[0] * 60 + parts[1]) * 1000L else 0L
    }

}