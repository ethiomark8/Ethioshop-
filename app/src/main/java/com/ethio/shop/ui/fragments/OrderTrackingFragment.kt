package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethio.shop.R
import com.ethio.shop.databinding.FragmentOrderTrackingBinding
import com.ethio.shop.ui.adapters.OrderItemAdapter
import com.ethio.shop.ui.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OrderTrackingFragment : Fragment() {

    private var _binding: FragmentOrderTrackingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private val args: OrderTrackingFragmentArgs by navArgs()

    private lateinit var orderItemAdapter: OrderItemAdapter

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrderItemsRecyclerView()
        loadOrder()
    }

    private fun setupOrderItemsRecyclerView() {
        orderItemAdapter = OrderItemAdapter()

        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }
    }

    private fun loadOrder() {
        if (args.orderId.isEmpty()) {
            Snackbar.make(binding.root, "Order ID not provided", Snackbar.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE

            val order = viewModel.getOrderById(args.orderId)
            order?.let {
                bindOrderData(it)
            } ?: run {
                Snackbar.make(binding.root, "Order not found", Snackbar.LENGTH_LONG).show()
            }

            binding.progressBar.visibility = View.GONE
        }
    }

    private fun bindOrderData(order: com.ethio.shop.data.models.Order) {
        // Order ID and Date
        binding.tvOrderId.text = "Order #${order.id.takeLast(6)}"
        order.createdAt?.let { timestamp ->
            val date = timestamp.toDate()
            binding.tvOrderDate.text = dateFormat.format(date)
        }

        // Order Status
        binding.tvStatus.text = order.status.replace("_", " ").capitalize()
        val statusColor = when (order.status) {
            "pending" -> R.color.accent_warning
            "confirmed" -> R.color.accent_success
            "shipped" -> R.color.accent_info
            "delivered" -> R.color.accent_success
            "cancelled" -> R.color.error
            else -> R.color.disabled
        }
        binding.cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), statusColor))

        // Order Items
        orderItemAdapter.submitList(order.items)

        // Payment Status
        binding.tvPaymentStatus.text = order.paymentStatus.capitalize()
        val paymentColor = when (order.paymentStatus) {
            "paid" -> R.color.accent_success
            "pending" -> R.color.accent_warning
            "failed" -> R.color.error
            "refunded" -> R.color.accent_info
            else -> R.color.disabled
        }
        binding.tvPaymentStatus.setTextColor(ContextCompat.getColor(requireContext(), paymentColor))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}