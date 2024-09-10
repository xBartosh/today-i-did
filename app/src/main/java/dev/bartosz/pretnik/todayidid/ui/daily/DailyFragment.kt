package dev.bartosz.pretnik.todayidid.ui.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dev.bartosz.pretnik.todayidid.MainActivity
import dev.bartosz.pretnik.todayidid.data.model.activity.Activity
import dev.bartosz.pretnik.todayidid.databinding.FragmentDailyBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DailyFragment : Fragment(), ActivityInteractionListener {
    private lateinit var binding: FragmentDailyBinding
    private lateinit var viewModel: DailyViewModel
    private lateinit var adapter: ActivityAdapter
    private val visibleDays = 15
    private var startDate: LocalDate = LocalDate.now().minusDays((visibleDays - 1).toLong())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = (requireActivity() as MainActivity).viewModelFactory
        viewModel = ViewModelProvider(this, factory)[DailyViewModel::class.java]
        binding = FragmentDailyBinding.inflate(inflater, container, false)

        setupDateSelector()
        setupCalendarButton()
        setupActivityList()
        observeSelectedDate()

        return binding.root
    }

    private fun setupDateSelector() {
        updateDateChips(viewModel.selectedDate.value)
    }

    private fun updateDateChips(selectedDate: LocalDate) {
        val today = LocalDate.now()

        // Update startDate if selectedDate is outside the current range
        if (selectedDate.isBefore(startDate) || selectedDate.isAfter(startDate.plusDays(visibleDays.toLong() - 1))) {
            startDate = selectedDate.minusDays((visibleDays / 2).toLong())
        }

        binding.dateChips.removeAllViews()
        val displayFormat = DateTimeFormatter.ofPattern("dd MMM")

        for (i in 0 until visibleDays) {
            val date = startDate.plusDays(i.toLong())
            if (date.isAfter(today)) break

            val chip = Chip(requireContext())
            chip.text = date.format(displayFormat)
            chip.isCheckable = true
            chip.tag = date

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setSelectedDate(date)
                }
            }

            binding.dateChips.addView(chip)

            // Select the chip for the selected date
            if (date == selectedDate) {
                chip.isChecked = true
            }
        }

        // Scroll to make the selected date visible
        scrollToDate(selectedDate)
    }

    private fun setupCalendarButton() {
        binding.calendarButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val selectedDate = viewModel.selectedDate.value
        val initialSelection = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setEnd(today)
            .setValidator(DateValidatorPointBackward.before(today))

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(initialSelection)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
            viewModel.setSelectedDate(date)
            updateDateChips(date)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun observeSelectedDate() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDate.collectLatest { selectedDate ->
                updateDateChips(selectedDate)
            }
        }
    }

    private fun scrollToDate(date: LocalDate) {
        binding.root.post {
            val chipToSelect = binding.dateChips.children
                .filterIsInstance<Chip>()
                .find { it.tag as? LocalDate == date }

            chipToSelect?.let { chip ->
                val scrollView = binding.dateChips.parent as? HorizontalScrollView
                val chipLeft = chip.left
                val chipRight = chip.right
                val scrollViewWidth = scrollView?.width ?: 0

                val idealScrollX = when {
                    chipLeft < scrollView?.scrollX ?: 0 -> chipLeft // Chip is to the left of view
                    chipRight > (scrollView?.scrollX ?: 0) + scrollViewWidth -> chipRight - scrollViewWidth // Chip is to the right of view
                    else -> scrollView?.scrollX ?: 0 // Chip is already visible
                }

                scrollView?.smoothScrollTo(idealScrollX, 0)
            }
        }
    }

    private fun setupActivityList() {
        adapter = ActivityAdapter(this)
        binding.activityList.adapter = adapter
        binding.activityList.layoutManager = LinearLayoutManager(context)

        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            adapter.submitList(activities)
            updateEmptyState(activities)
        }
    }

    private fun updateEmptyState(activities: List<Activity>) {
        if (activities.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.activityList.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.activityList.visibility = View.VISIBLE
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

    override fun onUpdateActivity(activity: Activity, instantUpdate: Boolean) {
        if (instantUpdate) {
            adapter.updateItemInstantly(activity)
        }
        viewModel.updateActivity(activity)
    }
}