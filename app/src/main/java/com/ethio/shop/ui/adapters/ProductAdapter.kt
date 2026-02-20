package com.ethio.shop.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ethio.shop.R
import com.ethio.shop.data.models.Product
import com.ethio.shop.databinding.ItemProductBinding
import com.ethio.shop.util.CurrencyUtils

class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                // Load product image using Coil
                val imageUrl = product.optimizedImages.firstOrNull() ?: product.images.firstOrNull()
                ivProductImage.load(imageUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                    crossfade(true)
                }

                // Set product details
                tvProductTitle.text = product.title
                tvProductPrice.text = CurrencyUtils.formatETB(product.priceETB)
                tvProductLocation.text = product.location

                // Set condition chip
                chipCondition.text = product.condition.replace("_", " ").capitalize()
                
                // Show/hide verified badge
                ivVerified.visibility = if (product.sellerVerified) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // Set click listener
                root.setOnClickListener {
                    onProductClick(product)
                }
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}