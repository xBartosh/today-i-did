package dev.bartosz.pretnik.todayidid.ui.alltime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.bartosz.pretnik.todayidid.MainActivity
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

        setUpAllTimeActivityList()

        return binding.root
    }

    private fun setUpAllTimeActivityList() {
        val adapter = AllTimeActivityAdapter()
        binding.allTimeActivityList.adapter = adapter
        binding.allTimeActivityList.layoutManager = LinearLayoutManager(context)

        viewModel.allTimeActivities.observe(viewLifecycleOwner) { activities ->
            adapter.submitList(activities)
        }
    }
}
