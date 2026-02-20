package com.ethio.shop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethio.shop.data.models.Order
import com.ethio.shop.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBuyerOrders(buyerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                orderRepository.getBuyerOrdersFlow(buyerId).collect { orders ->
                    _orders.value = orders
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadSellerOrders(sellerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                orderRepository.getSellerOrdersFlow(sellerId).collect { orders ->
                    _orders.value = orders
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return try {
            val result = orderRepository.getOrderById(orderId)
            if (result.isSuccess) {
                result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
                null
            }
        } catch (e: Exception) {
            _error.value = e.message
            null
        }
    }

    suspend fun createOrder(order: Order): String? {
        return try {
            _isLoading.value = true
            _error.value = null

            val result = orderRepository.createOrder(order)
            if (result.isSuccess) {
                _isLoading.value = false
                result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
                _isLoading.value = false
                null
            }
        } catch (e: Exception) {
            _error.value = e.message
            _isLoading.value = false
            null
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                orderRepository.updateOrderStatus(orderId, status)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun updatePaymentStatus(orderId: String, paymentStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                orderRepository.updatePaymentStatus(orderId, paymentStatus)
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