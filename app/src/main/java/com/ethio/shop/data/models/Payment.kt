package com.ethio.shop.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payment(
    val id: String = "",
    val orderId: String = "",
    val buyerId: String = "",
    val provider: String = "chapa",
    val sessionId: String = "",
    val status: String = "pending", // pending, success, failed, cancelled
    val amount: Double = 0.0,
    val currency: String = "ETB",
    val checkoutUrl: String = "",
    val transactionId: String? = null,
    val meta: Map<String, Any>? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable