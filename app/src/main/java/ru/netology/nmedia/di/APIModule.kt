package ru.netology.nmedia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.BuildConfig
import javax.inject.Singleton

@InstallIn(SingletonComponent)
@Module
class APIModule {

    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL
    }

    @Provides
    @Singleton
    fun provideLogger() = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}