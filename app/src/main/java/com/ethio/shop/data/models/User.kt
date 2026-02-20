package com.ethio.shop.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "buyer", // buyer, seller, admin
    val fcmToken: String? = null,
    val verified: Boolean = false,
    val sellerMeta: SellerMeta? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable

@Parcelize
data class SellerMeta(
    val businessName: String = "",
    val businessAddress: String = "",
    val verificationDocuments: List<String> = emptyList(),
    val verificationStatus: String = "none", // none, pending, approved, rejected
    val bankName: String? = null,
    val bankAccount: String? = null,
    val bankCode: String? = null
) : Parcelable