package dev.bartosz.pretnik.todayidid.ui.daily

import android.app.AlertDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.bartosz.pretnik.todayidid.R
import dev.bartosz.pretnik.todayidid.data.model.activity.Activity
import dev.bartosz.pretnik.todayidid.databinding.ItemActivityBinding
import java.time.format.DateTimeFormatter

class ActivityAdapter(private val interactionListener: ActivityInteractionListener) :
    ListAdapter<Activity, ActivityAdapter.ViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), interactionListener)
    }

    fun updateItemInstantly(updatedActivity: Activity) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.what == updatedActivity.what && it.date == updatedActivity.date }
        if (index != -1) {
            currentList[index] = updatedActivity
            submitList(currentList)
        }
    }

    class ViewHolder(private val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: Activity, interactionListener: ActivityInteractionListener) {
            binding.whatText.text = activity.what.lowercase().replaceFirstChar(Char::titlecase)
            binding.whenText.text = activity.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            updateDurationText(activity.durationInMinutes)

            binding.decreaseButton.setOnClickListener {
                if (activity.durationInMinutes > 0) {
                    val updatedActivity = activity.copy(durationInMinutes = activity.durationInMinutes - 1)
                    interactionListener.onUpdateActivity(updatedActivity, true)
                }
            }

            binding.increaseButton.setOnClickListener {
                val updatedActivity = activity.copy(durationInMinutes = activity.durationInMinutes + 1)
                interactionListener.onUpdateActivity(updatedActivity, true)
            }

            binding.durationText.setOnClickListener {
                showDurationInputDialog(activity, interactionListener)
            }

            binding.removeButton.setOnClickListener {
                interactionListener.onRemoveActivity(activity)
            }
        }

        private fun updateDurationText(minutes: Int) {
            binding.durationText.text = "$minutes min"
            binding.durationText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_menu_edit, 0)
            binding.durationText.compoundDrawablePadding = 8
        }

        private fun showDurationInputDialog(activity: Activity, interactionListener: ActivityInteractionListener) {
            val context = binding.root.context
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.setText(activity.durationInMinutes.toString())

            AlertDialog.Builder(context)
                .setTitle("Enter duration in minutes")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val newDuration = input.text.toString().toIntOrNull() ?: return@setPositiveButton
                    if (newDuration >= 0) {
                        val updatedActivity = activity.copy(durationInMinutes = newDuration)
                        interactionListener.onUpdateActivity(updatedActivity, false)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.what == newItem.what && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }
    }
}