package dev.bartosz.pretnik.todayidid.ui.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dev.bartosz.pretnik.todayidid.MainActivity
import dev.bartosz.pretnik.todayidid.data.model.Activity
import dev.bartosz.pretnik.todayidid.databinding.FragmentDailyBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyFragment : Fragment(), ActivityInteractionListener {
    private lateinit var binding: FragmentDailyBinding
    private lateinit var viewModel: DailyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = (requireActivity() as MainActivity).viewModelFactory
        viewModel = ViewModelProvider(this, factory)[DailyViewModel::class.java]
        binding = FragmentDailyBinding.inflate(inflater, container, false)

        setupDateSelector()
        setupActivityList()

        return binding.root
    }

    private fun setupDateSelector() {
        val displayFormat = DateTimeFormatter.ofPattern("dd MMMM")
        val today = LocalDate.now()

        for (i in -14..0) {
            val date = today.plusDays(i.toLong())

            val chip = Chip(requireContext())
            chip.text = date.format(displayFormat)
            chip.isCheckable = true
            chip.isChecked = i == 0

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setSelectedDate(date)
                }
            }

            binding.dateChips.addView(chip)
        }

        scrollToDate(0)
    }

    private fun scrollToDate(offset: Int) {
        val chipCount = binding.dateChips.childCount
        val index = chipCount - 1 + offset
        val chip = binding.dateChips.getChildAt(index) as? Chip
        chip?.isChecked = true

        binding.root.post {
            val scrollView = binding.dateChips.parent as? HorizontalScrollView
            scrollView?.smoothScrollTo(chip?.left ?: 0, 0)
        }
    }

    private fun setupActivityList() {
        val adapter = ActivityAdapter(this)
        binding.activityList.adapter = adapter
        binding.activityList.layoutManager = LinearLayoutManager(context)

        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            adapter.submitList(activities)
        }
    }

    override fun onRemoveActivity(activity: Activity) {
        viewModel.removeActivity(activity)
        viewModel.removeResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Activity removed successfully", 500).show()
            } else {
                Snackbar.make(binding.root, "Failed to remove activity", 500).show()
            }
        }
    }
}