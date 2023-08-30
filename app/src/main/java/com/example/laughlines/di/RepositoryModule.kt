package com.example.laughlines.di

import com.example.laughlines.data.repo.HomeRepository
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
    fun getRepository(fDb: FirebaseFirestore) = HomeRepository(fDb)
}