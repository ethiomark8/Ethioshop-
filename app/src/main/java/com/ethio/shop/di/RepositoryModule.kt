package com.ethio.shop.di

import com.ethio.shop.data.local.AppDatabase
import com.ethio.shop.data.repository.AuthRepository
import com.ethio.shop.data.repository.CartRepository
import com.ethio.shop.data.repository.ChatRepository
import com.ethio.shop.data.repository.NotificationRepository
import com.ethio.shop.data.repository.OrderRepository
import com.ethio.shop.data.repository.PaymentRepository
import com.ethio.shop.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthService: FirebaseAuthService,
        firestoreService: FirestoreService
    ): AuthRepository {
        return AuthRepository(firebaseAuthService, firestoreService)
    }
    
    @Provides
    @Singleton
    fun provideProductRepository(
        firestoreService: FirestoreService
    ): ProductRepository {
        return ProductRepository(firestoreService)
    }
    
    @Provides
    @Singleton
    fun provideOrderRepository(
        firestoreService: FirestoreService
    ): OrderRepository {
        return OrderRepository(firestoreService)
    }
    
    @Provides
    @Singleton
    fun providePaymentRepository(
        firestoreService: FirestoreService
    ): PaymentRepository {
        return PaymentRepository(firestoreService)
    }
    
    @Provides
    @Singleton
    fun provideCartRepository(
        database: AppDatabase
    ): CartRepository {
        return CartRepository(database)
    }
    
    @Provides
    @Singleton
    fun provideChatRepository(
        firestoreService: FirestoreService
    ): ChatRepository {
        return ChatRepository(firestoreService)
    }
    
    @Provides
    @Singleton
    fun provideNotificationRepository(
        firestoreService: FirestoreService
    ): NotificationRepository {
        return NotificationRepository(firestoreService)
    }
}