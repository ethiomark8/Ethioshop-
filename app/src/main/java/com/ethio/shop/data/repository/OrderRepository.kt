package com.ethio.shop.data.repository

import com.ethio.shop.data.models.Order
import com.ethio.shop.data.remote.FirestoreService
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    
    fun getBuyerOrdersFlow(buyerId: String): Flow<List<Order>> {
        val query = firestoreService.getCollection("orders")
            .whereEqualTo("buyerId", buyerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToOrder(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun getSellerOrdersFlow(sellerId: String): Flow<List<Order>> {
        val query = firestoreService.getCollection("orders")
            .whereEqualTo("sellerId", sellerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToOrder(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    suspend fun getOrderById(orderId: String): Result<Order?> {
        return try {
            val data = firestoreService.getDocumentData<Map<String, Any>>("orders", orderId)
            
            if (data.isSuccess) {
                val orderData = data.getOrNull()
                if (orderData != null) {
                    val order = convertToOrder(orderData)
                    Result.success(order)
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
    
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderData = mutableMapOf<String, Any>(
                "buyerId" to order.buyerId,
                "buyerName" to order.buyerName,
                "sellerId" to order.sellerId,
                "sellerName" to order.sellerName,
                "items" to order.items,
                "totalETB" to order.totalETB,
                "shippingCost" to order.shippingCost,
                "deliveryMethod" to order.deliveryMethod,
                "address" to (order.address ?: mapOf<String, Any>()),
                "status" to "pending",
                "paymentStatus" to "pending",
                "paymentMethod" to order.paymentMethod,
                "escrowReleased" to false
            )
            
            firestoreService.addDocument("orders", orderData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return firestoreService.updateDocument(
            "orders",
            orderId,
            mapOf("status" to status, "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp())
        )
    }
    
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: String): Result<Unit> {
        return firestoreService.updateDocument(
            "orders",
            orderId,
            mapOf("paymentStatus" to paymentStatus, "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp())
        )
    }
    
    private fun convertToOrder(data: Map<String, Any>): Order {
        return Order(
            id = data["id"] as? String ?: "",
            buyerId = data["buyerId"] as? String ?: "",
            buyerName = data["buyerName"] as? String ?: "",
            sellerId = data["sellerId"] as? String ?: "",
            sellerName = data["sellerName"] as? String ?: "",
            items = (data["items"] as? List<Map<String, Any>>)?.map { item ->
                Order.OrderItem(
                    productId = item["productId"] as? String ?: "",
                    title = item["title"] as? String ?: "",
                    quantity = (item["quantity"] as? Number)?.toInt() ?: 1,
                    priceETB = (item["priceETB"] as? Number)?.toDouble() ?: 0.0,
                    images = item["images"] as? List<String> ?: emptyList()
                )
            } ?: emptyList(),
            totalETB = (data["totalETB"] as? Number)?.toDouble() ?: 0.0,
            shippingCost = (data["shippingCost"] as? Number)?.toDouble() ?: 0.0,
            deliveryMethod = data["deliveryMethod"] as? String ?: "pickup",
            address = (data["address"] as? Map<String, Any>)?.let { addr ->
                Order.DeliveryAddress(
                    fullName = addr["fullName"] as? String ?: "",
                    phone = addr["phone"] as? String ?: "",
                    city = addr["city"] as? String ?: "",
                    subcity = addr["subcity"] as? String ?: "",
                    woreda = addr["woreda"] as? String ?: "",
                    houseNo = addr["houseNo"] as? String ?: ""
                )
            },
            status = data["status"] as? String ?: "pending",
            paymentStatus = data["paymentStatus"] as? String ?: "pending",
            paymentMethod = data["paymentMethod"] as? String ?: "chapa",
            paymentMeta = (data["paymentMeta"] as? Map<String, Any>)?.let { meta ->
                Order.PaymentMeta(
                    sessionId = meta["sessionId"] as? String,
                    transactionId = meta["transactionId"] as? String,
                    transferId = meta["transferId"] as? String
                )
            },
            escrowReleased = data["escrowReleased"] as? Boolean ?: false
        )
    }
}