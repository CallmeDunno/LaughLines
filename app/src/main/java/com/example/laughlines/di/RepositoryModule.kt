package com.example.laughlines.di

import com.example.laughlines.data.repo.ChatRepository
import com.example.laughlines.data.repo.HomeRepository
import com.example.laughlines.data.repo.LoginRepository
import com.example.laughlines.data.repo.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(fDb : FirebaseFirestore) = HomeRepository(fDb)

    @Provides
    @Singleton
    fun provideChatRepository(fDb: FirebaseFirestore) = ChatRepository(fDb)

    @Provides
    @Singleton
    fun provideProfileRepository(fDb: FirebaseFirestore, fAuth: FirebaseAuth) = ProfileRepository(fDb, fAuth)

    @Provides
    @Singleton
    fun provideLoginRepository(fDb: FirebaseFirestore, fAuth: FirebaseAuth) = LoginRepository(fDb, fAuth)
}