package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethio.shop.R
import com.ethio.shop.data.models.Product
import com.ethio.shop.data.repository.ProductRepository
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductListFragment : Fragment() {
    
    @Inject
    lateinit var productRepository: ProductRepository
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryChipGroup: com.google.android.material.chip.ChipGroup
    private val products = mutableListOf<Product>()
    private var selectedCategory: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupCategories()
        loadProducts()
    }
    
    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // TODO: Set adapter when ready
    }
    
    private fun setupCategories() {
        val categories = productRepository.getCategories()
        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = category
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategory = category
                        loadProducts()
                    } else {
                        selectedCategory = null
                        loadProducts()
                    }
                }
            }
            categoryChipGroup.addView(chip)
        }
    }
    
    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (selectedCategory != null) {
                    productRepository.getProductsByCategoryFlow(selectedCategory!!).collect { productList ->
                        products.clear()
                        products.addAll(productList)
                        // TODO: Update adapter
                    }
                } else {
                    productRepository.getActiveProductsFlow().collect { productList ->
                        products.clear()
                        products.addAll(productList)
                        // TODO: Update adapter
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading products", Toast.LENGTH_SHORT).show()
            }
        }
    }
}