package com.ethio.shop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val title: String,
    val priceETB: Double,
    val condition: String,
    val images: List<String>,
    val sellerId: String,
    val sellerName: String,
    val quantity: Int,
    val addedAt: Long
)