package com.ethio.shop.data.repository

import com.ethio.shop.data.models.Product
import com.ethio.shop.data.remote.FirestoreService
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    
    fun getActiveProductsFlow(): Flow<List<Product>> {
        val query = firestoreService.getCollection("products")
            .whereEqualTo("status", "active")
            .orderBy("createdAt", Query.Direction.DESCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToProduct(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun getProductsByCategoryFlow(category: String): Flow<List<Product>> {
        val query = firestoreService.getCollection("products")
            .whereEqualTo("status", "active")
            .whereEqualTo("category", category)
            .orderBy("priceETB", Query.Direction.ASCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToProduct(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun getProductsBySellerFlow(sellerId: String): Flow<List<Product>> {
        val query = firestoreService.getCollection("products")
            .whereEqualTo("sellerId", sellerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToProduct(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    fun searchProductsFlow(searchQuery: String): Flow<List<Product>> {
        // Note: Firestore doesn't support full-text search natively
        // For production, consider using Algolia or Elasticsearch
        val query = firestoreService.getCollection("products")
            .whereEqualTo("status", "active")
            .orderBy("title")
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff")
        
        return firestoreService.queryDocumentsFlow(query, Map::class.java)
            .map { documents ->
                documents.mapNotNull { data ->
                    try {
                        convertToProduct(data as Map<String, Any>)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
    
    suspend fun getProductById(productId: String): Result<Product?> {
        return try {
            val data = firestoreService.getDocumentData<Map<String, Any>>("products", productId)
            
            if (data.isSuccess) {
                val productData = data.getOrNull()
                if (productData != null) {
                    val product = convertToProduct(productData)
                    Result.success(product)
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
    
    suspend fun createProduct(product: Product): Result<String> {
        return try {
            val productData = mutableMapOf<String, Any>(
                "sellerId" to product.sellerId,
                "sellerName" to product.sellerName,
                "sellerVerified" to product.sellerVerified,
                "title" to product.title,
                "description" to product.description,
                "category" to product.category,
                "priceETB" to product.priceETB,
                "condition" to product.condition,
                "images" to product.images,
                "optimizedImages" to emptyList<String>(),
                "location" to product.location,
                "status" to "active"
            )
            
            firestoreService.addDocument("products", productData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProduct(productId: String, updates: Map<String, Any>): Result<Unit> {
        return firestoreService.updateDocument("products", productId, updates)
    }
    
    suspend fun deleteProduct(productId: String): Result<Unit> {
        // Soft delete by updating status
        return firestoreService.updateDocument("products", productId, mapOf("status" to "deleted"))
    }
    
    private fun convertToProduct(data: Map<String, Any>): Product {
        return Product(
            id = data["id"] as? String ?: "",
            sellerId = data["sellerId"] as? String ?: "",
            sellerName = data["sellerName"] as? String ?: "",
            sellerVerified = data["sellerVerified"] as? Boolean ?: false,
            title = data["title"] as? String ?: "",
            description = data["description"] as? String ?: "",
            category = data["category"] as? String ?: "",
            priceETB = (data["priceETB"] as? Number)?.toDouble() ?: 0.0,
            condition = data["condition"] as? String ?: "new",
            images = data["images"] as? List<String> ?: emptyList(),
            optimizedImages = data["optimizedImages"] as? List<String> ?: emptyList(),
            location = data["location"] as? String ?: "",
            status = data["status"] as? String ?: "active"
        )
    }
    
    fun getCategories(): List<String> {
        return listOf(
            "Electronics",
            "Clothing",
            "Home & Garden",
            "Books",
            "Sports",
            "Beauty",
            "Automotive",
            "Health",
            "Food",
            "Services"
        )
    }
    
    fun getConditions(): List<String> {
        return listOf("new", "like_new", "used")
    }
}