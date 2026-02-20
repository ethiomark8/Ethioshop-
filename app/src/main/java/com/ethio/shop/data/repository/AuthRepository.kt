package com.ethio.shop.data.repository

import com.ethio.shop.data.models.User
import com.ethio.shop.data.remote.FirebaseAuthService
import com.ethio.shop.data.remote.FirestoreService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) {
    
    val currentUser: FirebaseUser?
        get() = firebaseAuthService.currentUser
    
    val isUserLoggedIn: Boolean
        get() = firebaseAuthService.isUserLoggedIn
    
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: String = "buyer"
    ): Result<Unit> {
        return try {
            val authResult = firebaseAuthService.signUp(email, password, name)
            
            if (authResult.isSuccess) {
                val user = authResult.getOrNull()!!
                
                // Create user document in Firestore
                val userData = User(
                    uid = user.uid,
                    name = name,
                    email = email,
                    phone = phone,
                    role = role
                )
                
                firestoreService.setDocument("users", user.uid, userData)
                
                Result.success(Unit)
            } else {
                Result.failure(authResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<User?> {
        return try {
            val authResult = firebaseAuthService.signIn(email, password)
            
            if (authResult.isSuccess) {
                val user = authResult.getOrNull()!!
                getUserData(user.uid)
            } else {
                Result.failure(authResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithGoogle(idToken: String): Result<User?> {
        return try {
            val authResult = firebaseAuthService.signInWithGoogle(idToken)
            
            if (authResult.isSuccess) {
                val user = authResult.getOrNull()!!
                getUserData(user.uid)
            } else {
                Result.failure(authResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return firebaseAuthService.sendPasswordResetEmail(email)
    }
    
    fun signOut() {
        firebaseAuthService.signOut()
    }
    
    fun getCurrentUserId(): String? {
        return firebaseAuthService.getCurrentUserId()
    }
    
    fun getUserDataStream(userId: String): Flow<User?> = callbackFlow {
        val listener = firestoreService.getDocument("users", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    val user = User(
                        uid = snapshot.id,
                        name = data?.get("name") as? String ?: "",
                        email = data?.get("email") as? String ?: "",
                        phone = data?.get("phone") as? String ?: "",
                        role = data?.get("role") as? String ?: "buyer",
                        fcmToken = data?.get("fcmToken") as? String,
                        verified = data?.get("verified") as? Boolean ?: false
                    )
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun getUserData(userId: String): Result<User?> {
        return try {
            val data = firestoreService.getDocumentData<Map<String, Any>>("users", userId)
            
            if (data.isSuccess) {
                val userData = data.getOrNull()
                if (userData != null) {
                    val user = User(
                        uid = userId,
                        name = userData["name"] as? String ?: "",
                        email = userData["email"] as? String ?: "",
                        phone = userData["phone"] as? String ?: "",
                        role = userData["role"] as? String ?: "buyer",
                        fcmToken = userData["fcmToken"] as? String,
                        verified = userData["verified"] as? Boolean ?: false
                    )
                    Result.success(user)
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
    
    suspend fun updateFcmToken(userId: String, token: String): Result<Unit> {
        return try {
            firestoreService.updateDocument(
                "users",
                userId,
                mapOf("fcmToken" to token)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}