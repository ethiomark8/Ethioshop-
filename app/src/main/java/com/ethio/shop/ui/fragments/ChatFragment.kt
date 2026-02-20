package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethio.shop.R
import com.ethio.shop.databinding.FragmentChatBinding
import com.ethio.shop.ui.adapters.MessageAdapter
import com.ethio.shop.ui.viewmodels.ChatViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMessagesRecyclerView()
        setupObservers()
        setupListeners()
        loadMessages()
    }

    private fun setupMessagesRecyclerView() {
        // TODO: Get current user ID from AuthRepository
        val currentUserId = "current_user_id" // Replace with actual user ID

        messageAdapter = MessageAdapter(currentUserId)

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupObservers() {
        // Observe messages
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                messageAdapter.submitList(messages)
                // Scroll to bottom when new messages arrive
                if (messages.isNotEmpty()) {
                    binding.rvMessages.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.tilMessage.setEndIconOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        val senderId = args.senderId
        val receiverId = args.receiverId

        if (senderId.isEmpty() || receiverId.isEmpty()) {
            Snackbar.make(binding.root, "Invalid chat parameters", Snackbar.LENGTH_LONG).show()
            return
        }

        // Set chat title
        binding.tvChatTitle.text = "Chat with ${args.receiverName}"

        // Load messages based on context
        when {
            args.orderId.isNotEmpty() -> {
                viewModel.loadOrderMessages(args.orderId)
            }
            args.productId.isNotEmpty() -> {
                viewModel.loadProductMessages(args.productId)
            }
            else -> {
                viewModel.loadChatMessages(senderId, receiverId)
            }
        }
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()

        if (messageText.isEmpty()) {
            return
        }

        // TODO: Get current user info from AuthRepository
        val currentUserId = "current_user_id" // Replace with actual user ID
        val currentUserName = "Current User" // Replace with actual user name

        val message = com.ethio.shop.data.models.Message(
            senderId = currentUserId,
            senderName = currentUserName,
            receiverId = args.receiverId,
            receiverName = args.receiverName,
            text = messageText,
            orderId = args.orderId.takeIf { it.isNotEmpty() },
            productId = args.productId.takeIf { it.isNotEmpty() }
        )

        viewModel.sendMessage(message)

        // Clear input
        binding.etMessage.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}