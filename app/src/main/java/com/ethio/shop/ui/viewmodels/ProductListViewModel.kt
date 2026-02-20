package com.ethio.shop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethio.shop.data.models.Product
import com.ethio.shop.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val categories: List<String> = productRepository.getCategories()

    init {
        loadProducts()
    }

    fun loadProducts(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _selectedCategory.value = category

                if (category != null) {
                    productRepository.getProductsByCategoryFlow(category).collect { products ->
                        _products.value = products
                        _isLoading.value = false
                    }
                } else {
                    productRepository.getActiveProductsFlow().collect { products ->
                        _products.value = products
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            loadProducts(_selectedCategory.value)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                productRepository.searchProductsFlow(query).collect { products ->
                    _products.value = products
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}