package com.ethio.shop.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerVerified: Boolean = false,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val priceETB: Double = 0.0,
    val condition: String = "new", // new, like_new, used
    val images: List<String> = emptyList(),
    val optimizedImages: List<String> = emptyList(),
    val location: String = "",
    val status: String = "active", // active, sold, deleted
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable

@Parcelize
data class CartItem(
    val id: String = "",
    val productId: String = "",
    val title: String = "",
    val priceETB: Double = 0.0,
    val condition: String = "",
    val images: List<String> = emptyList(),
    val sellerId: String = "",
    val sellerName: String = "",
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) : Parcelable