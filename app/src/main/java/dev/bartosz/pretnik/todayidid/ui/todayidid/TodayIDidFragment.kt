package dev.bartosz.pretnik.todayidid.ui.todayidid

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dev.bartosz.pretnik.todayidid.MainActivity
import dev.bartosz.pretnik.todayidid.data.model.activity.Activity
import dev.bartosz.pretnik.todayidid.databinding.FragmentTodayididBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        setupSuggestions()

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

            val hours = hoursStr.toIntOrNull() ?: 0
            val minutes = minutesStr.toIntOrNull() ?: 0
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

    private fun setupSuggestions() {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.what.setAdapter(adapter)

        binding.what.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSuggestions(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.suggestions.collectLatest { suggestions ->
                adapter.clear()
                adapter.addAll(suggestions)
                adapter.notifyDataSetChanged()
                if (suggestions.isNotEmpty()) {
                    binding.what.showDropDown()
                }
            }
        }

        viewModel.updateSuggestions("")
    }

    private fun showErrorMessage(message: String) {
        showMessage(message, true)
    }

    private fun showSuccessMessage() {
        showMessage("Activity added successfully", false)
    }

    private fun showMessage(message: String, isError: Boolean) {
        val binding = _binding
            ?: // The view has been destroyed, we can't show the message
            return

        val context = requireContext()
        val typedValue = TypedValue()

        binding.messageCard.apply {
            visibility = View.VISIBLE
            setCardBackgroundColor(
                if (isError) {
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorError, typedValue, true)
                    typedValue.data
                } else {
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
                    typedValue.data
                }
            )
        }
        binding.messageText.apply {
            text = message
            setTextColor(
                if (isError) {
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnError, typedValue, true)
                    typedValue.data
                } else {
                    context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
                    typedValue.data
                }
            )
        }

        Handler(Looper.getMainLooper()).postDelayed({
            _binding?.messageCard?.visibility = View.GONE
        }, 1000)
    }
    private fun clearForm() {
        binding.what.text?.clear()
        binding.hours.text?.clear()
        binding.minutes.text?.clear()
    }
}