package com.vtest.video_test.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vtest.video_test.database.entity.VideoEntity
import com.vtest.video_test.ui.components.CenteredText
import com.vtest.video_test.ui.viewmodels.VideoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosListScreen(
    viewModel: VideoViewModel = hiltViewModel(),
    onVideoSelected: (VideoEntity) -> Unit
) {

    val videos by viewModel.videos.collectAsState(initial = emptyList())
    val error by viewModel.error.collectAsState(initial = null)

    val refreshing = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(refreshing.value) {
        if (refreshing.value) {
            val task = viewModel.fetchVideos()
            task.invokeOnCompletion {
                coroutineScope.launch(Dispatchers.Main) {
                    refreshing.value = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshing.value = true
    }

    Box(
        modifier = Modifier.pullToRefresh(
            state = pullToRefreshState,
            isRefreshing = refreshing.value,
            onRefresh = {
                refreshing.value = true
            }
        ),
        contentAlignment = Alignment.TopStart
    ) {
        // Ideally this needs AnimatedVisibility.
        if (error != null) {
            CenteredText(text = "Error: $error")
        } else if (videos.isEmpty()) {
            CenteredText(text = "Loading...")
        } else {
            VideosList(
                videos = videos,
                onVideoSelected = onVideoSelected
            )
        }

        PullToRefreshDefaults.Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = refreshing.value,
            state = pullToRefreshState
        )
    }
}

@Composable
fun VideosList(
    videos: List<VideoEntity>,
    onVideoSelected: (VideoEntity) -> Unit
) {
    LazyColumn {
        items(videos) { video ->
            VideoCard(
                onVideoSelected = onVideoSelected,
                video = video
            )
        }
    }
}

@Composable
fun VideoCard(
    onVideoSelected: (VideoEntity) -> Unit,
    video: VideoEntity
) {
    Card(
        modifier = Modifier.padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(),
        onClick = {
            onVideoSelected.invoke(video)
        },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = video.title,
                onLoading = {
                    Log.d("ImageLoading", "Loading image: ${video.thumbnailUrl}")
                },
                onSuccess = {
                    Log.d("ImageLoaded", "Image loaded: ${video.thumbnailUrl}")
                },
                onError = {
                    it.result.throwable.printStackTrace()
                    Log.d("ImageError", "Error loading image: ${video.thumbnailUrl}")
                }
            )

            val durationMillis = video.getDurationMillis()
            val progress = if (durationMillis > 0) {
                (video.lastPosition.toFloat() / durationMillis.toFloat()) * 100
            } else 0f

            LinearProgressIndicator(
                progress = { progress / 100 },
                color = Color.Red,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                    text = video.title
                )
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                    text = video.duration
                )
            }
        }

    }
}