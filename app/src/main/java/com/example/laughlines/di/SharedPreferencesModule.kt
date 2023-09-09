package com.example.laughlines.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.laughlines.utils.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.applicationContext.getSharedPreferences(
            "data_user",
            Context.MODE_PRIVATE
        )

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(sharedPreferences: SharedPreferences) =
        SharedPreferencesManager(sharedPreferences)
}