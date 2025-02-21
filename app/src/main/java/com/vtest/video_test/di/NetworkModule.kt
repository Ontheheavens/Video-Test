package com.vtest.video_test.di

import android.content.Context
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.vtest.video_test.api.DummyVideoAPI
import com.vtest.video_test.database.AppDatabase
import com.vtest.video_test.repository.VideoRepository
import com.vtest.video_test.repository.VideoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper {
        val objectMapper: ObjectMapper = JsonMapper
            .builder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .build()
            .registerModules(KotlinModule.Builder().build())

        return objectMapper
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        objectMapper: ObjectMapper
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DummyVideoAPI.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
    }

    @Provides
    @Singleton
    fun provideDummyVideoAPI(retrofit: Retrofit): DummyVideoAPI {
        return retrofit.create(DummyVideoAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        videoAPI: DummyVideoAPI,
        @ApplicationContext context: Context
    ): VideoRepository {
        val dao = AppDatabase.getDatabase(context).videoDao()
        return VideoRepositoryImpl(videoApi = videoAPI, videoDao = dao)
    }

}