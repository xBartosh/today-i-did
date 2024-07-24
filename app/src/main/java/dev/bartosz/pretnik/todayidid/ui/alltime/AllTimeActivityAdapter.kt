package dev.bartosz.pretnik.todayidid.ui.alltime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.bartosz.pretnik.todayidid.data.model.AllTimeActivity
import dev.bartosz.pretnik.todayidid.databinding.AlltimeItemActivityBinding
import java.time.format.DateTimeFormatter
import java.util.Locale

class AllTimeActivityAdapter : ListAdapter<AllTimeActivity, AllTimeActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = AlltimeItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ActivityViewHolder(private val binding: AlltimeItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: AllTimeActivity) {
            binding.alltimeWhat.text = activity.what.lowercase().replaceFirstChar(Char::titlecase)
            binding.alltimeStartTime.text = String.format(Locale.UK, "Started on: %s", activity.startDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).toString())
            binding.alltimeAverageMinutes.text = String.format(Locale.UK, "Average: %.2f minutes", activity.dailyDurationAverageInMinutes)
            binding.alltimeTotalMinutes.text = String.format(Locale.UK, "Total: %d minutes", activity.totalMinutes)
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<AllTimeActivity>() {
        override fun areItemsTheSame(oldItem: AllTimeActivity, newItem: AllTimeActivity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AllTimeActivity, newItem: AllTimeActivity): Boolean {
            return oldItem == newItem
        }
    }
}