package com.example.laughlines.di

import com.example.laughlines.data.repo.*
import com.example.laughlines.utils.SharedPreferencesManager
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
    fun provideHomeRepository(fDb: FirebaseFirestore, sharedPreManager: SharedPreferencesManager) = HomeRepository(fDb, sharedPreManager)

    @Provides
    @Singleton
    fun provideChatRepository(fDb: FirebaseFirestore) = ChatRepository(fDb)

    @Provides
    @Singleton
    fun provideProfileRepository(fDb: FirebaseFirestore, fAuth: FirebaseAuth, sharedPreManager: SharedPreferencesManager) = ProfileRepository(fDb, fAuth, sharedPreManager)

    @Provides
    @Singleton
    fun provideLoginRepository(fDb: FirebaseFirestore, fAuth: FirebaseAuth) = LoginRepository(fDb, fAuth)

    @Provides
    @Singleton
    fun provideQrCodeRepository(fDb: FirebaseFirestore, sharedPreManager: SharedPreferencesManager) = QrCodeRepository(fDb, sharedPreManager)

    @Provides
    @Singleton
    fun provideRequestRepository(fDb: FirebaseFirestore, sharedPreManager: SharedPreferencesManager) = RequestRepository(fDb, sharedPreManager)


}