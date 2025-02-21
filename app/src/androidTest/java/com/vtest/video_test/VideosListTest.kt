package com.vtest.video_test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vtest.video_test.database.entity.VideoEntity
import com.vtest.video_test.ui.components.AppNavigation
import com.vtest.video_test.ui.screens.VideosList
import com.vtest.video_test.ui.screens.VideosListScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun videoListDisplaysVideos() {
        val fakeVideos = listOf(
            VideoEntity("1", "url1", "Title1", "10:00", "url3"),
            VideoEntity("2", "url2", "Title2", "05:30", "url4")
        )

        composeTestRule.setContent {
            VideosList(
                videos = fakeVideos,
                onVideoSelected = {}
            )
        }

        composeTestRule.onNodeWithText("Title1").assertExists()
        composeTestRule.onNodeWithText("Title2").assertExists()
    }

}