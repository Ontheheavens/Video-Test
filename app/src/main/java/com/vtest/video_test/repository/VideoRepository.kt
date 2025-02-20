package com.vtest.video_test.repository

import android.util.Log
import com.vtest.video_test.api.DummyVideoAPI
import com.vtest.video_test.database.dao.VideoDAO
import com.vtest.video_test.database.entity.VideoEntity
import com.vtest.video_test.model.VideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val videoApi: DummyVideoAPI,
    private val videoDao: VideoDAO
) {

    // We differentiate between API data and DB data to divide responsibilities.
    fun fetchVideos(): Flow<List<VideoEntity>> = flow {
        val localVideos = videoDao.getAllVideos().associateBy { it.id }
        emit(localVideos.values.toList())

        try {
            val remoteVideos = videoApi.getVideosList()
            val videoEntities = remoteVideos.map { video ->
                val existingVideo = localVideos[video.id]
                video.toDBEntity(lastPlaybackPosition = existingVideo?.lastPosition ?: 0L)
            }
            videoDao.insertOrUpdateVideos(videoEntities)
            emit(videoEntities)
        } catch (exception: Exception) {
            Log.d("VideoRepository", "Video fetch failed")
            // Rethrowing to let UI handle this,
            // could employ a wrapper but no need since this is a test task.
            throw exception
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveVideo(video: VideoEntity) {
        videoDao.insertOrUpdateVideos(listOf(video))
    }

    suspend fun getSavedVideo(videoUrl: String): VideoEntity? {
        return videoDao.getVideo(videoUrl)
    }

    suspend fun updatePlaybackPosition(videoUrl: String, position: Long) {
        videoDao.updatePlaybackPosition(videoUrl, position)
    }
}

fun VideoData.toDBEntity(lastPlaybackPosition: Long): VideoEntity {
    return VideoEntity(
        id = this.id,
        videoUrl = this.videoUrl,
        title = this.title,
        thumbnailUrl = this.thumbnailUrl,
        duration = this.duration,
        lastPosition = lastPlaybackPosition
    )
}