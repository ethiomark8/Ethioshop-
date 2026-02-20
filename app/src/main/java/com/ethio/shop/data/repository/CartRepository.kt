package com.ethio.shop.data.repository

import com.ethio.shop.data.local.AppDatabase
import com.ethio.shop.data.local.CartEntity
import com.ethio.shop.data.models.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val database: AppDatabase
) {
    
    private val cartDao = database.cartDao()
    
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { entity ->
                CartItem(
                    id = entity.id,
                    productId = entity.productId,
                    title = entity.title,
                    priceETB = entity.priceETB,
                    condition = entity.condition,
                    images = entity.images,
                    sellerId = entity.sellerId,
                    sellerName = entity.sellerName,
                    quantity = entity.quantity,
                    addedAt = entity.addedAt
                )
            }
        }
    }
    
    fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }
    
    fun getCartTotal(): Flow<Double?> {
        return cartDao.getCartTotal()
    }
    
    suspend fun addToCart(cartItem: CartItem): Result<Unit> {
        return try {
            val entity = CartEntity(
                id = cartItem.id.ifEmpty { "${cartItem.productId}_${System.currentTimeMillis()}" },
                productId = cartItem.productId,
                title = cartItem.title,
                priceETB = cartItem.priceETB,
                condition = cartItem.condition,
                images = cartItem.images,
                sellerId = cartItem.sellerId,
                sellerName = cartItem.sellerName,
                quantity = cartItem.quantity,
                addedAt = cartItem.addedAt
            )
            cartDao.insertCartItem(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            val cartItem = cartDao.getCartItemById(cartItemId)
            if (cartItem != null) {
                val updatedItem = cartItem.copy(quantity = quantity)
                cartDao.updateCartItem(updatedItem)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Cart item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        return try {
            cartDao.deleteCartItemById(cartItemId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun clearCart(): Result<Unit> {
        return try {
            cartDao.clearCart()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncCartWithFirestore(userId: String): Result<Unit> {
        // This would sync local cart with Firestore when user logs in
        // For now, just return success
        return Result.success(Unit)
    }
}