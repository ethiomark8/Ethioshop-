package com.ethio.shop.data.repository

import com.ethio.shop.data.models.Notification
import com.ethio.shop.data.remote.FirestoreService
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    
    fun getUserNotificationsFlow(userId: String): Flow<List<Notification>> {
        val query = firestoreService.getCollection("notifications")
            .whereEqualTo("recipientId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToNotification(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun getUnreadNotificationsFlow(userId: String): Flow<List<Notification>> {
        val query = firestoreService.getCollection("notifications")
            .whereEqualTo("recipientId", userId)
            .whereEqualTo("status", "unread")
            .orderBy("createdAt", Query.Direction.DESCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToNotification(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return firestoreService.updateDocument(
            "notifications",
            notificationId,
            mapOf("status" to "read")
        )
    }
    
    suspend fun markAllAsRead(userId: String): Result<Unit> {
        // This would need to be done with a batch write
        return Result.success(Unit)
    }
    
    private fun convertToNotification(data: Map<String, Any>): Notification {
        return Notification(
            id = data["id"] as? String ?: "",
            type = data["type"] as? String ?: "",
            recipientId = data["recipientId"] as? String ?: "",
            payload = (data["payload"] as? Map<String, Any>)?.let { payload ->
                Notification.NotificationPayload(
                    title = payload["title"] as? String ?: "",
                    body = payload["body"] as? String ?: "",
                    data = (payload["data"] as? Map<String, String>) ?: emptyMap()
                )
            },
            status = data["status"] as? String ?: "unread"
        )
    }
}