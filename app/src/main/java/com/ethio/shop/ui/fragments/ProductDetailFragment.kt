package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.ethio.shop.R
import com.ethio.shop.databinding.FragmentProductDetailBinding
import com.ethio.shop.ui.adapters.ProductImageAdapter
import com.ethio.shop.ui.viewmodels.ProductDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductDetailViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()

    private lateinit var imageAdapter: ProductImageAdapter
    private val imageIndicators = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()

        // Load product
        if (args.productId.isNotEmpty()) {
            viewModel.loadProduct(args.productId)
        } else {
            Snackbar.make(binding.root, "Product ID not provided", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupObservers() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let {
                bindProductData(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.currentImageIndex.observe(viewLifecycleOwner) { index ->
            updateImageIndicators(index)
        }
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            viewModel.previousImage()
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextImage()
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.product.value?.let { product ->
                addToCart(product)
            }
        }

        binding.btnChat.setOnClickListener {
            viewModel.product.value?.let { product ->
                navigateToChat(product)
            }
        }
    }

    private fun bindProductData(product: com.ethio.shop.data.models.Product) {
        // Setup image adapter
        val images = product.optimizedImages.ifEmpty { product.images }
        imageAdapter = ProductImageAdapter(images)
        binding.viewPagerImages.adapter = imageAdapter

        // Setup image indicators
        setupImageIndicators(images.size)

        // Setup ViewPager callbacks
        binding.viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentImageIndex(position)
            }
        })

        // Set product details
        binding.tvProductTitle.text = product.title
        binding.tvProductPrice.text = com.ethio.shop.util.CurrencyUtils.formatETB(product.priceETB)
        binding.tvLocation.text = product.location
        binding.tvDescription.text = product.description

        // Set condition
        binding.chipCondition.text = product.condition.replace("_", " ").capitalize()
        binding.chipCondition.setChipBackgroundColorResource(
            when (product.condition) {
                "new" -> R.color.accent_success
                "like_new" -> R.color.accent_info
                "used" -> R.color.accent_warning
                else -> R.color.disabled
            }
        )

        // Set seller info
        binding.tvSellerName.text = product.sellerName
        binding.ivVerified.visibility = if (product.sellerVerified) {
            View.VISIBLE
        } else {
            View.GONE
        }

        // Update image indicators
        updateImageIndicators(viewModel.currentImageIndex.value)
    }

    private fun setupImageIndicators(count: Int) {
        binding.imageIndicators.removeAllViews()
        imageIndicators.clear()

        for (i in 0 until count) {
            val indicator = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    if (i == 0) 24 else 8,
                    8
                )
                background = ContextCompat.getDrawable(
                    requireContext(),
                    if (i == 0) R.color.accent_primary else R.color.disabled
                )
            }
            binding.imageIndicators.addView(indicator)
            imageIndicators.add(indicator)
        }
    }

    private fun updateImageIndicators(index: Int) {
        imageIndicators.forEachIndexed { i, indicator ->
            indicator.background = ContextCompat.getDrawable(
                requireContext(),
                if (i == index) R.color.accent_primary else R.color.disabled
            )
            indicator.layoutParams = ViewGroup.LayoutParams(
                if (i == index) 24 else 8,
                8
            )
        }
    }

    private fun addToCart(product: com.ethio.shop.data.models.Product) {
        // TODO: Implement cart functionality
        Snackbar.make(
            binding.root,
            "${product.title} added to cart",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun navigateToChat(product: com.ethio.shop.data.models.Product) {
        // TODO: Navigate to chat fragment
        Toast.makeText(requireContext(), "Chat feature coming soon", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}