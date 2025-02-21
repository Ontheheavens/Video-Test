package com.vtest.video_test.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtest.video_test.database.entity.VideoEntity
import com.vtest.video_test.repository.VideoRepository
import com.vtest.video_test.repository.VideoRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {

    private val mutableVideoFlow = MutableStateFlow<List<VideoEntity>>(emptyList())
    val videos: StateFlow<List<VideoEntity>> = mutableVideoFlow.asStateFlow()

    private val errorsMutableFlow = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = errorsMutableFlow.asStateFlow()

    fun savePosition(videoUrl: String, position: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlaybackPosition(videoUrl, position)
        }
    }

    suspend fun getSavedPosition(videoUrl: String): Long {
        return withContext(Dispatchers.IO) {
            repository.getSavedVideo(videoUrl)?.lastPosition ?: 0L
        }
    }

    fun fetchVideos(): Job {
        return viewModelScope.launch {
            try {
                repository.fetchVideos().collect { videoList ->
                    mutableVideoFlow.value = videoList
                }
            } catch (exception: Exception) {
                errorsMutableFlow.value = "Failed to fetch videos: ${exception.message}"
            }
        }
    }

}