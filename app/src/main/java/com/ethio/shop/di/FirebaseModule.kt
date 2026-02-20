package com.ethio.shop.di

import com.ethio.shop.data.remote.FirebaseAuthService
import com.ethio.shop.data.remote.FirestoreService
import com.ethio.shop.data.remote.StorageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
    }
    
    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService {
        return FirestoreService()
    }
    
    @Provides
    @Singleton
    fun provideStorageService(): StorageService {
        return StorageService()
    }
}