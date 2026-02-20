package com.ethio.shop.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageService @Inject constructor() {
    
    private val storage = FirebaseStorage.getInstance()
    
    suspend fun uploadImage(
        filePath: String,
        storagePath: String
    ): Result<Uri> {
        return try {
            val file = Uri.fromFile(File(filePath))
            val ref = storage.reference.child(storagePath)
            
            val uploadTask = ref.putFile(file)
            uploadTask.await()
            
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadImageUri(
        uri: Uri,
        storagePath: String
    ): Result<Uri> {
        return try {
            val ref = storage.reference.child(storagePath)
            
            val uploadTask = ref.putFile(uri)
            uploadTask.await()
            
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadImageBytes(
        bytes: ByteArray,
        storagePath: String
    ): Result<Uri> {
        return try {
            val ref = storage.reference.child(storagePath)
            
            val uploadTask = ref.putBytes(bytes)
            uploadTask.await()
            
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteImage(storagePath: String): Result<Void> {
        return try {
            val ref = storage.reference.child(storagePath)
            ref.delete().await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getStorageUrl(storagePath: String): String {
        val ref = storage.reference.child(storagePath)
        return ref.toString()
    }
    
    fun getProductImageOriginalPath(productId: String, fileName: String): String {
        return "products/$productId/originals/$fileName"
    }
    
    fun getProductImageOptimizedPath(productId: String, fileName: String): String {
        return "products/$productId/optimized/$fileName"
    }
    
    fun getUserAvatarPath(userId: String, fileName: String): String {
        return "users/$userId/avatars/$fileName"
    }
    
    fun getSellerDocumentPath(sellerId: String, fileName: String): String {
        return "sellers/$sellerId/documents/$fileName"
    }
}