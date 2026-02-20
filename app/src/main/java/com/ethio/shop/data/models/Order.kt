package com.ethio.shop.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalETB: Double = 0.0,
    val shippingCost: Double = 0.0,
    val deliveryMethod: String = "pickup", // pickup, shipping
    val address: DeliveryAddress? = null,
    val status: String = "pending", // pending, confirmed, shipped, delivered, cancelled
    val paymentStatus: String = "pending", // pending, paid, failed, refunded
    val paymentMethod: String = "chapa",
    val paymentMeta: PaymentMeta? = null,
    val escrowReleased: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable

@Parcelize
data class OrderItem(
    val productId: String = "",
    val title: String = "",
    val quantity: Int = 1,
    val priceETB: Double = 0.0,
    val images: List<String> = emptyList()
) : Parcelable

@Parcelize
data class DeliveryAddress(
    val fullName: String = "",
    val phone: String = "",
    val city: String = "",
    val subcity: String = "",
    val woreda: String = "",
    val houseNo: String = ""
) : Parcelable

@Parcelize
data class PaymentMeta(
    val sessionId: String? = null,
    val transactionId: String? = null,
    val transferId: String? = null
) : Parcelable