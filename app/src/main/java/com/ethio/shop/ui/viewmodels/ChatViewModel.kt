package com.ethio.shop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethio.shop.data.models.Message
import com.ethio.shop.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadChatMessages(senderId: String, receiverId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                chatRepository.getChatMessagesFlow(senderId, receiverId).collect { messages ->
                    // Reverse to show oldest messages first
                    _messages.value = messages.reversed()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadOrderMessages(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                chatRepository.getOrderMessagesFlow(orderId).collect { messages ->
                    _messages.value = messages
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadProductMessages(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                chatRepository.getProductMessagesFlow(productId).collect { messages ->
                    _messages.value = messages
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                chatRepository.sendMessage(message)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(senderId: String, receiverId: String) {
        viewModelScope.launch {
            try {
                chatRepository.markMessagesAsRead(senderId, receiverId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}