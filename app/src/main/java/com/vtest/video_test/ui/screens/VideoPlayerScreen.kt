package com.vtest.video_test.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.vtest.video_test.ui.viewmodels.VideoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun VideoPlayerScreen(videoUrl: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VideoPlayer(videoUrl = videoUrl)
    }
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    viewModel: VideoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var savedPosition by remember { mutableLongStateOf(0L) }


    LaunchedEffect(videoUrl) {
        savedPosition = viewModel.getSavedPosition(videoUrl)
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            seekTo(savedPosition)
            playWhenReady = true
        }
    }

    LaunchedEffect(savedPosition) {
        savedPosition.let { exoPlayer.seekTo(it) }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(1000)
            val current =  exoPlayer.currentPosition
            withContext(Dispatchers.IO) {
                viewModel.savePosition(videoUrl, current)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.savePosition(videoUrl, exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { viewContext ->
            PlayerView(viewContext).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = modifier.fillMaxSize()
    )
}