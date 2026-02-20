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
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentImageIndex = MutableStateFlow(0)
    val currentImageIndex: StateFlow<Int> = _currentImageIndex.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = productRepository.getProductById(productId)
                if (result.isSuccess) {
                    _product.value = result.getOrNull()
                    _isLoading.value = false
                } else {
                    _error.value = result.exceptionOrNull()?.message
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun nextImage() {
        _product.value?.let { product ->
            val images = product.optimizedImages.ifEmpty { product.images }
            val nextIndex = (_currentImageIndex.value + 1) % images.size
            _currentImageIndex.value = nextIndex
        }
    }

    fun previousImage() {
        _product.value?.let { product ->
            val images = product.optimizedImages.ifEmpty { product.images }
            val prevIndex = if (_currentImageIndex.value > 0) {
                _currentImageIndex.value - 1
            } else {
                images.size - 1
            }
            _currentImageIndex.value = prevIndex
        }
    }

    fun setCurrentImageIndex(index: Int) {
        _product.value?.let { product ->
            val images = product.optimizedImages.ifEmpty { product.images }
            if (index in images.indices) {
                _currentImageIndex.value = index
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}