package dev.bartosz.pretnik.todayidid.ui.alltime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.bartosz.pretnik.todayidid.MainActivity
import dev.bartosz.pretnik.todayidid.R
import dev.bartosz.pretnik.todayidid.databinding.FragmentAlltimeBinding

class AllTimeFragment : Fragment() {
    private lateinit var binding: FragmentAlltimeBinding
    private lateinit var viewModel: AllTimeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = (requireActivity() as MainActivity).viewModelFactory
        viewModel = ViewModelProvider(this, factory)[AllTimeViewModel::class.java]
        binding = FragmentAlltimeBinding.inflate(inflater, container, false)

        setupAllTimeActivityList()
        setupEmptyStateButton()

        return binding.root
    }

    private fun setupAllTimeActivityList() {
        val adapter = AllTimeActivityAdapter()
        binding.allTimeActivityList.adapter = adapter
        binding.allTimeActivityList.layoutManager = LinearLayoutManager(context)

        viewModel.allTimeActivities.observe(viewLifecycleOwner) { activities ->
            adapter.submitList(activities)
            updateEmptyState(activities.isEmpty())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.allTimeActivityList.visibility = View.GONE
        } else {
            binding.emptyStateContainer.visibility = View.GONE
            binding.allTimeActivityList.visibility = View.VISIBLE
        }
    }

    private fun setupEmptyStateButton() {
        binding.addActivityButton.setOnClickListener {
            // Navigate to the "Today I Did" screen by simulating a click on the bottom navigation item
            val bottomNavView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavView?.selectedItemId = R.id.navigation_todayidid
        }
    }
}