package com.ethio.shop.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null,
    val read: Boolean = false,
    val orderId: String? = null,
    val productId: String? = null,
    val imageUrl: String? = null
) : Parcelable