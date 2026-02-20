package com.ethio.shop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethio.shop.data.local.CartEntity
import com.ethio.shop.data.models.CartItem
import com.ethio.shop.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val cartItems = cartRepository.getCartItems()
    val cartItemCount = cartRepository.getCartItemCount()
    val cartTotal = cartRepository.getCartTotal()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                cartRepository.addToCart(cartItem)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun updateQuantity(cartItem: CartEntity, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                cartRepository.updateCartItemQuantity(cartItem.id, quantity)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun removeFromCart(cartItem: CartEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                cartRepository.removeFromCart(cartItem.id)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                cartRepository.clearCart()
                _isLoading.value = false
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