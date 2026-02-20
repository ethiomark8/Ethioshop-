package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethio.shop.R
import com.ethio.shop.databinding.FragmentCheckoutBinding
import com.ethio.shop.ui.adapters.CartAdapter
import com.ethio.shop.ui.viewmodels.CartViewModel
import com.ethio.shop.ui.viewmodels.CheckoutViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels()
    private val checkoutViewModel: CheckoutViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, quantity ->
                cartViewModel.updateQuantity(cartItem, quantity)
            },
            onRemoveClicked = { cartItem ->
                cartViewModel.removeFromCart(cartItem)
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupObservers() {
        // Observe cart items
        lifecycleScope.launch {
            cartViewModel.cartItems.collect { items ->
                cartAdapter.submitList(items)
                checkoutViewModel.setCartItems(items)
            }
        }

        // Observe checkout totals
        checkoutViewModel.subtotal.observe(viewLifecycleOwner) { subtotal ->
            binding.tvSubtotal.text = checkoutViewModel.formatPrice(subtotal)
        }

        checkoutViewModel.shippingCost.observe(viewLifecycleOwner) { shippingCost ->
            binding.tvShipping.text = checkoutViewModel.formatPrice(shippingCost)
        }

        checkoutViewModel.total.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = checkoutViewModel.formatPrice(total)
        }

        // Observe loading state
        checkoutViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCheckout.isEnabled = !isLoading
        }

        // Observe errors
        checkoutViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                checkoutViewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.radioDeliveryMethod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPickup -> {
                    binding.cardAddress.visibility = View.GONE
                    checkoutViewModel.setDeliveryMethod("pickup")
                }
                R.id.rbShipping -> {
                    binding.cardAddress.visibility = View.VISIBLE
                    checkoutViewModel.setDeliveryMethod("shipping")
                }
            }
        }

        binding.btnCheckout.setOnClickListener {
            proceedToCheckout()
        }
    }

    private fun proceedToCheckout() {
        // Validate shipping address if shipping is selected
        if (binding.rbShipping.isChecked) {
            val fullName = binding.etFullName.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val subcity = binding.etSubcity.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (fullName.isEmpty()) {
                binding.tilFullName.error = getString(R.string.required)
                return
            }
            if (city.isEmpty()) {
                binding.tilCity.error = getString(R.string.required)
                return
            }
            if (subcity.isEmpty()) {
                binding.tilSubcity.error = getString(R.string.required)
                return
            }
            if (phone.isEmpty()) {
                binding.tilPhone.error = getString(R.string.required)
                return
            }
        }

        // TODO: Get user info from AuthRepository
        val buyerId = "current_user_id" // Replace with actual user ID
        val buyerName = "Current User" // Replace with actual user name

        // Get seller info from first cart item (simplified - in real app, handle multiple sellers)
        val firstItem = cartAdapter.currentList.firstOrNull()
        if (firstItem == null) {
            Snackbar.make(binding.root, "Cart is empty", Snackbar.LENGTH_LONG).show()
            return
        }

        val sellerId = firstItem.sellerId
        val sellerName = firstItem.sellerName

        // Create delivery address if shipping
        val address = if (binding.rbShipping.isChecked) {
            com.ethio.shop.data.models.Order.DeliveryAddress(
                fullName = binding.etFullName.text.toString().trim(),
                phone = binding.etPhone.text.toString().trim(),
                city = binding.etCity.text.toString().trim(),
                subcity = binding.etSubcity.text.toString().trim(),
                woreda = "",
                houseNo = ""
            )
        } else {
            null
        }

        lifecycleScope.launch {
            val result = checkoutViewModel.initiateCheckout(
                buyerId = buyerId,
                buyerName = buyerName,
                sellerId = sellerId,
                sellerName = sellerName,
                address = address
            )

            if (result.isSuccess) {
                val checkoutUrl = result.getOrNull()
                // Open Chapa payment page in Chrome Custom Tab
                checkoutUrl?.let { url ->
                    openPaymentPage(url)
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to initiate checkout"
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun openPaymentPage(url: String) {
        // TODO: Implement Chrome Custom Tabs integration
        Toast.makeText(requireContext(), "Opening payment page: $url", Toast.LENGTH_SHORT).show()
        
        // Navigate to order tracking after successful payment
        // In real app, you would open the URL and handle the callback
        findNavController().navigate(R.id.action_checkoutFragment_to_ordersFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}