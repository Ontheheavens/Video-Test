package com.vtest.video_test.api

import com.vtest.video_test.model.VideoData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DummyVideoAPI {

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/"
        const val JSON_TARGET_SUFFIX = "Ontheheavens/Ontheheavens/refs/heads/main/"
    }

    @GET(JSON_TARGET_SUFFIX + "videos.json")
    suspend fun getVideosList(
    ): List<VideoData>

}