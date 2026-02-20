package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethio.shop.R
import com.ethio.shop.databinding.FragmentSellerDashboardBinding
import com.ethio.shop.ui.adapters.OrderAdapter
import com.ethio.shop.ui.adapters.ProductAdapter
import com.ethio.shop.ui.viewmodels.OrderViewModel
import com.ethio.shop.ui.viewmodels.ProductListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerDashboardFragment : Fragment() {

    private var _binding: FragmentSellerDashboardBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by viewModels()
    private val productViewModel: ProductListViewModel by viewModels()

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()
        setupListeners()
        loadData()
    }

    private fun setupRecyclerViews() {
        // Orders RecyclerView
        orderAdapter = OrderAdapter { order ->
            navigateToOrderDetail(order.id)
        }

        binding.rvRecentOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }

        // Products RecyclerView
        productAdapter = ProductAdapter { product ->
            navigateToProductDetail(product.id)
        }

        binding.rvMyProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    private fun setupObservers() {
        // Observe orders
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            orderAdapter.submitList(orders.take(5)) // Show only 5 recent orders
            
            // Update total orders
            binding.tvTotalOrders.text = orders.size.toString()

            // Calculate total revenue (only from delivered orders)
            val totalRevenue = orders
                .filter { it.status == "delivered" && it.paymentStatus == "paid" }
                .sumOf { it.totalETB }
            binding.tvTotalRevenue.text = com.ethio.shop.util.CurrencyUtils.formatETB(totalRevenue)
        }

        // Observe products
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products.take(5)) // Show only 5 recent products
        }

        // Observe loading state
        orderViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else if (!productViewModel.isLoading.value!!) {
                binding.progressBar.visibility = View.GONE
            }
        }

        productViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else if (!orderViewModel.isLoading.value!!) {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Observe errors
        orderViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                orderViewModel.clearError()
            }
        }

        productViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                productViewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.btnAddProduct.setOnClickListener {
            // TODO: Navigate to add product fragment
            Snackbar.make(binding.root, "Add Product feature coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        // TODO: Get seller ID from AuthRepository
        val sellerId = "seller_user_id" // Replace with actual seller ID

        lifecycleScope.launch {
            // Load seller's orders
            orderViewModel.loadSellerOrders(sellerId)

            // Load seller's products
            productViewModel.loadProducts(null) // Will filter by seller in ProductRepository
        }
    }

    private fun navigateToOrderDetail(orderId: String) {
        val action = SellerDashboardFragmentDirections
            .actionSellerDashboardFragmentToOrderTrackingFragment(orderId)
        findNavController().navigate(action)
    }

    private fun navigateToProductDetail(productId: String) {
        val action = SellerDashboardFragmentDirections
            .actionSellerDashboardFragmentToProductDetailFragment(productId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}