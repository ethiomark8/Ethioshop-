package com.ethio.shop.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    @Query("SELECT * FROM cart ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>
    
    @Query("SELECT * FROM cart WHERE id = :id")
    suspend fun getCartItemById(id: String): CartEntity?
    
    @Query("SELECT * FROM cart WHERE productId = :productId")
    suspend fun getCartItemByProductId(productId: String): CartEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(cartItems: List<CartEntity>)
    
    @Update
    suspend fun updateCartItem(cartItem: CartEntity)
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartEntity)
    
    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteCartItemById(id: String)
    
    @Query("DELETE FROM cart")
    suspend fun clearCart()
    
    @Query("SELECT COUNT(*) FROM cart")
    fun getCartItemCount(): Flow<Int>
    
    @Query("SELECT SUM(priceETB * quantity) FROM cart")
    fun getCartTotal(): Flow<Double?>
}