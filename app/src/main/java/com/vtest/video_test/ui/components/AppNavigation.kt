package com.vtest.video_test.ui.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vtest.video_test.ui.screens.VideoPlayerScreen
import com.vtest.video_test.ui.screens.VideosListScreen

const val LIST_SCREEN = "list_screen"
const val PLAYER_SCREEN = "player_screen/{videoUrl}"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LIST_SCREEN) {
        composable(LIST_SCREEN) {
            VideosListScreen(
                onVideoSelected = { videoData ->
                    val url = videoData.videoUrl
                    val encodedUrl = Uri.encode(url)
                    navController.navigate(PLAYER_SCREEN.replace("{videoUrl}", encodedUrl))
                }
            )
        }

        composable(PLAYER_SCREEN) { backStackEntry ->
            val videoUrl = backStackEntry.arguments?.getString("videoUrl")?.let { Uri.decode(it) } ?: ""
            VideoPlayerScreen(videoUrl = videoUrl)
        }
    }
}