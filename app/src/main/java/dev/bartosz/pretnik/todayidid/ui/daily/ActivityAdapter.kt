package dev.bartosz.pretnik.todayidid.ui.daily

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.bartosz.pretnik.todayidid.data.model.Activity
import dev.bartosz.pretnik.todayidid.databinding.ItemActivityBinding
import java.time.format.DateTimeFormatter
import java.util.Locale

class ActivityAdapter(private val interactionListener: ActivityInteractionListener) : ListAdapter<Activity, ActivityAdapter.ViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), interactionListener)
    }

    class ViewHolder(private val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: Activity, interactionListener: ActivityInteractionListener) {
            binding.whatText.text = activity.what.lowercase().replaceFirstChar(Char::titlecase)
            binding.whenText.text = activity.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            binding.durationText.text = String.format(Locale.UK, "%d minutes", activity.durationInMinutes)
            binding.removeButton.setOnClickListener {
                interactionListener.onRemoveActivity(activity)
            }
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }
    }
}