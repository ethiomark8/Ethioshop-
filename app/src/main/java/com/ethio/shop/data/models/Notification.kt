package com.ethio.shop.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: String = "",
    val type: String = "", // new_message, order_update, payment_success, etc.
    val recipientId: String = "",
    val payload: NotificationPayload? = null,
    val status: String = "unread", // unread, read
    val createdAt: Timestamp? = null
) : Parcelable

@Parcelize
data class NotificationPayload(
    val title: String = "",
    val body: String = "",
    val data: Map<String, String> = emptyMap()
) : Parcelable