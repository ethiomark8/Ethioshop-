package com.ethio.shop.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ethio.shop.R
import com.ethio.shop.data.local.CartEntity
import com.ethio.shop.databinding.ItemCartBinding
import com.ethio.shop.util.CurrencyUtils

class CartAdapter(
    private val onQuantityChanged: (CartEntity, Int) -> Unit,
    private val onRemoveClicked: (CartEntity) -> Unit
) : ListAdapter<CartEntity, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartEntity) {
            binding.apply {
                // Load product image using Coil
                val imageUrl = cartItem.images.firstOrNull()
                ivProductImage.load(imageUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                    crossfade(true)
                }

                // Set product details
                tvProductTitle.text = cartItem.title
                tvProductPrice.text = CurrencyUtils.formatETB(cartItem.priceETB * cartItem.quantity)
                tvQuantity.text = cartItem.quantity.toString()

                // Set condition chip
                chipCondition.text = cartItem.condition.replace("_", " ").capitalize()

                // Set quantity button listeners
                btnDecrease.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        onQuantityChanged(cartItem, cartItem.quantity - 1)
                    }
                }

                btnIncrease.setOnClickListener {
                    onQuantityChanged(cartItem, cartItem.quantity + 1)
                }

                // Set remove button listener
                btnRemove.setOnClickListener {
                    onRemoveClicked(cartItem)
                }
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartEntity>() {
        override fun areItemsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
            return oldItem == newItem
        }
    }
}