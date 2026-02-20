package com.ethio.shop.data.repository

import com.ethio.shop.data.models.Message
import com.ethio.shop.data.remote.FirestoreService
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    
    suspend fun sendMessage(message: Message): Result<String> {
        return try {
            val messageData = mapOf(
                "senderId" to message.senderId,
                "senderName" to message.senderName,
                "receiverId" to message.receiverId,
                "receiverName" to message.receiverName,
                "text" to message.text,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "read" to false,
                "orderId" to (message.orderId ?: ""),
                "productId" to (message.productId ?: "")
            )
            
            firestoreService.addDocument("messages", messageData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getChatMessagesFlow(senderId: String, receiverId: String): Flow<List<Message>> {
        val query = firestoreService.getCollection("messages")
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToMessage(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun getOrderMessagesFlow(orderId: String): Flow<List<Message>> {
        val query = firestoreService.getCollection("messages")
            .whereEqualTo("orderId", orderId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToMessage(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun getProductMessagesFlow(productId: String): Flow<List<Message>> {
        val query = firestoreService.getCollection("messages")
            .whereEqualTo("productId", productId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToMessage(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    suspend fun markMessagesAsRead(senderId: String, receiverId: String): Result<Unit> {
        return try {
            // This would need to be done differently in Firestore
            // For now, just return success
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun convertToMessage(data: Map<String, Any>): Message {
        return Message(
            id = data["id"] as? String ?: "",
            senderId = data["senderId"] as? String ?: "",
            senderName = data["senderName"] as? String ?: "",
            receiverId = data["receiverId"] as? String ?: "",
            receiverName = data["receiverName"] as? String ?: "",
            text = data["text"] as? String ?: "",
            read = data["read"] as? Boolean ?: false,
            orderId = data["orderId"] as? String,
            productId = data["productId"] as? String,
            imageUrl = data["imageUrl"] as? String
        )
    }
}