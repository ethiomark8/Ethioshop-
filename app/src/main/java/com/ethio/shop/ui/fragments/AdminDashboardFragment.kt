package com.ethio.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ethio.shop.R
import com.ethio.shop.databinding.FragmentAdminDashboardBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        // TODO: Load admin dashboard data
    }

    private fun setupListeners() {
        binding.btnManageUsers.setOnClickListener {
            // TODO: Navigate to user management
            Snackbar.make(binding.root, "User Management coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnManageProducts.setOnClickListener {
            // TODO: Navigate to product management
            Snackbar.make(binding.root, "Product Management coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnViewReports.setOnClickListener {
            // TODO: Navigate to reports
            Snackbar.make(binding.root, "Reports coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnSystemSettings.setOnClickListener {
            // TODO: Navigate to system settings
            Snackbar.make(binding.root, "System Settings coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}