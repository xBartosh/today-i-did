package dev.bartosz.pretnik.todayidid.ui.todayidid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.blue
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dev.bartosz.pretnik.todayidid.MainActivity
import dev.bartosz.pretnik.todayidid.databinding.FragmentTodayididBinding
import dev.bartosz.pretnik.todayidid.data.model.Activity
import java.sql.Time
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class TodayIDidFragment : Fragment() {
    private lateinit var viewModel: TodayIDidViewModel
    private var _binding: FragmentTodayididBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = (requireActivity() as MainActivity).viewModelFactory
        viewModel = ViewModelProvider(this, factory)[TodayIDidViewModel::class.java]
        _binding = FragmentTodayididBinding.inflate(inflater, container, false)

        setupDatePicker()
        setupSubmitButton()
        setDefaultDate()
        setUpQuote()

        return binding.root
    }

    private fun setUpQuote() {
        binding.quote.text = viewModel.randomQuote
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDefaultDate() {
        val today = LocalDate.now()
        binding.whenInput.setText(today.format(dateFormatter))
    }

    private fun setupDatePicker() {
        binding.whenInput.setOnClickListener {
            val today = MaterialDatePicker.todayInUtcMilliseconds()

            val constraintsBuilder = CalendarConstraints.Builder()
                .setEnd(today)
                .setValidator(DateValidatorPointBackward.before(today))

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(today)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val date = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
                binding.whenInput.setText(date.format(dateFormatter))
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupSubmitButton() {
        binding.button.setOnClickListener {
            val what = binding.what.text.toString().uppercase()
            val whenStr = binding.whenInput.text.toString()
            val hoursStr = binding.hours.text.toString()
            val minutesStr = binding.minutes.text.toString()

            if (what.isBlank() || whenStr.isBlank()) {
                showErrorMessage("Please fill all fields")
                return@setOnClickListener
            }

            val hours = hoursStr.toIntOrNull()?:0
            val minutes = minutesStr.toIntOrNull()?:0
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || (hours <= 0 && minutes <= 0)) {
                showErrorMessage("Please enter valid hours and minutes")
                return@setOnClickListener
            }

            val whenDate = LocalDate.parse(whenStr, dateFormatter)
            val duration = hours * 60 + minutes
            val activity = Activity(what = what, date = whenDate, durationInMinutes = duration)
            viewModel.addActivity(activity)
            showSuccessMessage()
            clearForm()
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, 500).show()
    }

    private fun showSuccessMessage() {
        Snackbar.make(binding.root, "Activity added successfully", 500).show()
    }

    private fun clearForm() {
        binding.what.text?.clear()
        setDefaultDate()
        binding.hours.text?.clear()
        binding.minutes.text?.clear()
    }
}