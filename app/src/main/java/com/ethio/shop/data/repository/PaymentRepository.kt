package com.ethio.shop.data.repository

import com.ethio.shop.data.models.Payment
import com.ethio.shop.data.remote.FirestoreService
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    
    private val functions: FirebaseFunctions = Firebase.functions
    
    suspend fun createChapaPayment(orderId: String, returnUrl: String?): Result<String> {
        return try {
            val data = hashMapOf(
                "orderId" to orderId,
                "returnUrl" to (returnUrl ?: "ethioshop://payment/success")
            )
            
            val result = functions.getHttpsCallable("createChapaPayment")
                .call(data)
                .await()
            
            val checkoutUrl = result.data as? Map<*, *>
            val url = checkoutUrl?.get("checkout_url") as? String
            
            if (url != null) {
                Result.success(url)
            } else {
                Result.failure(Exception("Invalid response from payment function"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPaymentBySessionId(sessionId: String): Result<Payment?> {
        return try {
            val data = firestoreService.getDocumentData<Map<String, Any>>("payments", sessionId)
            
            if (data.isSuccess) {
                val paymentData = data.getOrNull()
                if (paymentData != null) {
                    val payment = Payment(
                        id = sessionId,
                        orderId = paymentData["orderId"] as? String ?: "",
                        buyerId = paymentData["buyerId"] as? String ?: "",
                        provider = paymentData["provider"] as? String ?: "chapa",
                        sessionId = paymentData["sessionId"] as? String ?: "",
                        status = paymentData["status"] as? String ?: "pending",
                        amount = (paymentData["amount"] as? Number)?.toDouble() ?: 0.0,
                        currency = paymentData["currency"] as? String ?: "ETB",
                        checkoutUrl = paymentData["checkoutUrl"] as? String ?: "",
                        transactionId = paymentData["transactionId"] as? String
                    )
                    Result.success(payment)
                } else {
                    Result.success(null)
                }
            } else {
                Result.failure(data.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPaymentByOrderId(orderId: String): Result<Payment?> {
        return try {
            val query = firestoreService.getCollection("payments")
                .whereEqualTo("orderId", orderId)
                .limit(1)
            
            val result = firestoreService.queryDocuments(query, Map::class.java)
            
            if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
                val paymentData = result.getOrNull()!!.first()
                val payment = Payment(
                    id = paymentData["id"] as? String ?: "",
                    orderId = paymentData["orderId"] as? String ?: "",
                    buyerId = paymentData["buyerId"] as? String ?: "",
                    provider = paymentData["provider"] as? String ?: "chapa",
                    sessionId = paymentData["sessionId"] as? String ?: "",
                    status = paymentData["status"] as? String ?: "pending",
                    amount = (paymentData["amount"] as? Number)?.toDouble() ?: 0.0,
                    currency = paymentData["currency"] as? String ?: "ETB",
                    checkoutUrl = paymentData["checkoutUrl"] as? String ?: "",
                    transactionId = paymentData["transactionId"] as? String
                )
                Result.success(payment)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getPaymentStatusFlow(sessionId: String): Flow<String?> {
        return firestoreService.queryDocumentsFlow(
            firestoreService.getDocument("payments", sessionId),
            Map::class.java
        ).map { documents ->
            documents.firstOrNull()?.get("status") as? String
        }
    }
}