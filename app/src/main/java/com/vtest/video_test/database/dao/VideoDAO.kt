package com.vtest.video_test.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vtest.video_test.database.entity.VideoEntity

@Dao
interface VideoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVideos(videos: List<VideoEntity>)

    @Query("SELECT * FROM videos WHERE videoUrl = :videoUrl LIMIT 1")
    suspend fun getVideo(videoUrl: String): VideoEntity?

    @Query("SELECT * FROM videos")
    suspend fun getAllVideos(): List<VideoEntity>

    @Query("UPDATE videos SET lastPosition = :position WHERE videoUrl = :videoUrl")
    suspend fun updatePlaybackPosition(videoUrl: String, position: Long)
}