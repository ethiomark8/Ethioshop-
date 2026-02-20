package com.ethio.shop.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ethio.shop.R
import com.ethio.shop.data.models.Order
import com.ethio.shop.databinding.ItemOrderBinding
import com.ethio.shop.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                // Set order ID
                tvOrderId.text = "Order #${order.id.takeLast(6)}"

                // Set order date
                order.createdAt?.let { timestamp ->
                    val date = timestamp.toDate()
                    tvOrderDate.text = dateFormat.format(date)
                }

                // Set total including shipping
                val total = order.totalETB + order.shippingCost
                tvOrderTotal.text = "Total: ${CurrencyUtils.formatETB(total)}"

                // Set order status with appropriate color
                tvOrderStatus.text = order.status.replace("_", " ").capitalize()
                val statusColor = when (order.status) {
                    "pending" -> R.color.accent_warning
                    "confirmed" -> R.color.accent_success
                    "shipped" -> R.color.accent_info
                    "delivered" -> R.color.accent_success
                    "cancelled" -> R.color.error
                    else -> R.color.disabled
                }
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(
                    root.context,
                    statusColor
                )

                // Set payment status with appropriate color
                tvPaymentStatus.text = order.paymentStatus.capitalize()
                val paymentColor = when (order.paymentStatus) {
                    "paid" -> R.color.accent_success
                    "pending" -> R.color.accent_warning
                    "failed" -> R.color.error
                    "refunded" -> R.color.accent_info
                    else -> R.color.disabled
                }
                tvPaymentStatus.backgroundTintList = ContextCompat.getColorStateList(
                    root.context,
                    paymentColor
                )

                // Set item count
                tvItemCount.text = "${order.items.size} items"

                // Set click listener
                root.setOnClickListener {
                    onOrderClick(order)
                }
            }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}