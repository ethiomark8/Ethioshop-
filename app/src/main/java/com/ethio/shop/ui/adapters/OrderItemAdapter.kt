package com.ethio.shop.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ethio.shop.R
import com.ethio.shop.data.models.Order
import com.ethio.shop.databinding.ItemOrderItemBinding
import com.ethio.shop.util.CurrencyUtils

class OrderItemAdapter : ListAdapter<Order.OrderItem, OrderItemAdapter.OrderItemViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(
        private val binding: ItemOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItem: Order.OrderItem) {
            binding.apply {
                // Load product image
                val imageUrl = orderItem.images.firstOrNull()
                ivProductImage.load(imageUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                    crossfade(true)
                }

                // Set item details
                tvTitle.text = orderItem.title
                tvQuantity.text = "Qty: ${orderItem.quantity}"
                tvPrice.text = CurrencyUtils.formatETB(orderItem.priceETB * orderItem.quantity)
            }
        }
    }

    private class OrderItemDiffCallback : DiffUtil.ItemCallback<Order.OrderItem>() {
        override fun areItemsTheSame(oldItem: Order.OrderItem, newItem: Order.OrderItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Order.OrderItem, newItem: Order.OrderItem): Boolean {
            return oldItem == newItem
        }
    }
}