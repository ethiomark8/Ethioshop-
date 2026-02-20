package com.ethio.shop.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {
    
    private val firestore: FirebaseFirestore = Firebase.firestore
    
    fun getCollection(collectionPath: String): Query {
        return firestore.collection(collectionPath)
    }
    
    fun getDocument(collectionPath: String, documentId: String): Query {
        return firestore.collection(collectionPath).document(documentId)
    }
    
    suspend fun <T> addDocument(
        collectionPath: String,
        data: T
    ): Result<String> {
        return try {
            val documentRef = firestore.collection(collectionPath).add(data).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun <T> setDocument(
        collectionPath: String,
        documentId: String,
        data: T
    ): Result<Void> {
        return try {
            firestore.collection(collectionPath).document(documentId).set(data).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun <T> updateDocument(
        collectionPath: String,
        documentId: String,
        data: Map<String, Any>
    ): Result<Void> {
        return try {
            firestore.collection(collectionPath).document(documentId).update(data).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteDocument(
        collectionPath: String,
        documentId: String
    ): Result<Void> {
        return try {
            firestore.collection(collectionPath).document(documentId).delete().await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun <T> getDocumentData(
        collectionPath: String,
        documentId: String
    ): Result<T?> {
        return try {
            val document = firestore.collection(collectionPath).document(documentId).get().await()
            if (document.exists()) {
                @Suppress("UNCHECKED_CAST")
                Result.success(document.data as? T)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun <T> getCollectionFlow(
        collectionPath: String,
        clazz: Class<T>
    ): Flow<List<T>> = callbackFlow {
        val listener = firestore.collection(collectionPath)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val items = snapshot?.documents?.mapNotNull { document ->
                    try {
                        val data = document.data
                        @Suppress("UNCHECKED_CAST")
                        data as? T
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(items)
            }
        
        awaitClose { listener.remove() }
    }
    
    fun <T> queryDocumentsFlow(
        query: Query,
        clazz: Class<T>
    ): Flow<List<T>> = callbackFlow {
        val listener = query
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val items = snapshot?.documents?.mapNotNull { document ->
                    try {
                        val data = document.data
                        @Suppress("UNCHECKED_CAST")
                        data as? T
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(items)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun <T> queryDocuments(
        query: Query,
        clazz: Class<T>
    ): Result<List<T>> {
        return try {
            val snapshot = query.get().await()
            val items = snapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data
                    @Suppress("UNCHECKED_CAST")
                    data as? T
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}