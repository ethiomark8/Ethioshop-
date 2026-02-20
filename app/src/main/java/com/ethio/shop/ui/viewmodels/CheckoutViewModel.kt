package com.ethio.shop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethio.shop.data.models.CartItem
import com.ethio.shop.data.models.Order
import com.ethio.shop.data.repository.OrderRepository
import com.ethio.shop.data.repository.PaymentRepository
import com.ethio.shop.util.CurrencyUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    private val _shippingCost = MutableStateFlow(0.0)
    val shippingCost: StateFlow<Double> = _shippingCost.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    private val _deliveryMethod = MutableStateFlow("pickup")
    val deliveryMethod: StateFlow<String> = _deliveryMethod.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setCartItems(items: List<CartItem>) {
        _cartItems.value = items
        calculateTotals()
    }

    fun setDeliveryMethod(method: String) {
        _deliveryMethod.value = method
        calculateShipping()
        calculateTotals()
    }

    private fun calculateSubtotal() {
        _subtotal.value = _cartItems.value.sumOf { item ->
            item.priceETB * item.quantity
        }
    }

    private fun calculateShipping() {
        _shippingCost.value = if (_deliveryMethod.value == "shipping") {
            100.0 // ETB 100 for shipping
        } else {
            0.0
        }
    }

    private fun calculateTotals() {
        calculateSubtotal()
        calculateShipping()
        _total.value = _subtotal.value + _shippingCost.value
    }

    suspend fun initiateCheckout(
        buyerId: String,
        buyerName: String,
        sellerId: String,
        sellerName: String,
        address: Order.DeliveryAddress? = null
    ): Result<String> {
        return try {
            _isLoading.value = true
            _error.value = null

            // Create order
            val orderItems = _cartItems.value.map { cartItem ->
                Order.OrderItem(
                    productId = cartItem.productId,
                    title = cartItem.title,
                    quantity = cartItem.quantity,
                    priceETB = cartItem.priceETB,
                    images = cartItem.images
                )
            }

            val order = Order(
                buyerId = buyerId,
                buyerName = buyerName,
                sellerId = sellerId,
                sellerName = sellerName,
                items = orderItems,
                totalETB = _subtotal.value,
                shippingCost = _shippingCost.value,
                deliveryMethod = _deliveryMethod.value,
                address = address,
                paymentMethod = "chapa"
            )

            val orderId = orderRepository.createOrder(order)
            if (orderId.isSuccess && orderId.getOrNull() != null) {
                // Initiate Chapa payment
                val checkoutUrl = paymentRepository.createChapaPayment(
                    orderId.getOrNull()!!,
                    "ethioshop://payment/success"
                )

                _isLoading.value = false

                if (checkoutUrl.isSuccess) {
                    Result.success(checkoutUrl.getOrNull()!!)
                } else {
                    Result.failure(checkoutUrl.exceptionOrNull()!!)
                }
            } else {
                _isLoading.value = false
                Result.failure(orderId.exceptionOrNull() ?: Exception("Failed to create order"))
            }
        } catch (e: Exception) {
            _error.value = e.message
            _isLoading.value = false
            Result.failure(e)
        }
    }

    fun formatPrice(amount: Double): String {
        return CurrencyUtils.formatETB(amount)
    }

    fun clearError() {
        _error.value = null
    }
}