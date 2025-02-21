package com.vtest.video_test

import com.vtest.video_test.database.entity.VideoEntity
import com.vtest.video_test.repository.VideoRepository
import com.vtest.video_test.repository.VideoRepositoryImpl
import com.vtest.video_test.ui.viewmodels.VideoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class VideoViewModelTest {

    private lateinit var viewModel: VideoViewModel
    private lateinit var repository: FakeVideoRepository

    private val testDispatcher = StandardTestDispatcher()
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeVideoRepository()
        viewModel = VideoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchVideosUpdatesVideosState() = runTest {
        viewModel.fetchVideos().join() // Wait for coroutine to complete

        assertEquals(2, viewModel.videos.value.size)
        assertEquals("Test Video 1", viewModel.videos.value[0].title)
    }

    @Test
    fun fetchVideosHandlesErrorsCorrectly() = runTest {
        val errorRepository = object : VideoRepository {
            override fun fetchVideos(): Flow<List<VideoEntity>> = flow {
                throw RuntimeException("Network Error")
            }

            override suspend fun saveVideo(video: VideoEntity) {}
            override suspend fun getSavedVideo(videoUrl: String): VideoEntity? = null
            override suspend fun updatePlaybackPosition(videoUrl: String, position: Long) {}
        }

        val errorViewModel = VideoViewModel(errorRepository)
        errorViewModel.fetchVideos().join()

        assertEquals("Failed to fetch videos: Network Error", errorViewModel.error.value)
    }

    @Test
    fun getSavedPositionReturnsCorrectPosition(): Unit = runTest {
        val position = viewModel.getSavedPosition("url1")

        assertEquals(0L, position)
    }

    class FakeVideoRepository : VideoRepository {
        private val fakeVideos = listOf(
            VideoEntity("1", "url1", "Test Video 1", "10:00", "url3"),
            VideoEntity("2", "url2", "Test Video 2", "05:30", "url4")
        )

        override fun fetchVideos(): Flow<List<VideoEntity>> = flow {
            emit(fakeVideos)
        }

        override suspend fun getSavedVideo(videoUrl: String): VideoEntity? {
            return fakeVideos.find { it.videoUrl == videoUrl }
        }
        override suspend fun saveVideo(video: VideoEntity) {}
        override suspend fun updatePlaybackPosition(videoUrl: String, position: Long) {}
    }

}