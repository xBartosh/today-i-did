package dev.bartosz.pretnik.todayidid.ui.alltime

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dev.bartosz.pretnik.todayidid.data.model.Activity
import dev.bartosz.pretnik.todayidid.data.model.ActivityRepository
import dev.bartosz.pretnik.todayidid.data.model.AllTimeActivity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class AllTimeViewModel(repository: ActivityRepository) : ViewModel() {
    val allTimeActivities: LiveData<List<AllTimeActivity>> = repository.allActivities
        .distinctUntilChanged()
        .map { activities ->
            aggregateActivities(activities)
        }
        .asLiveData()

    private fun aggregateActivities(activities: List<Activity>): List<AllTimeActivity> {
        if (activities.isEmpty()) return emptyList()

        val everStartDate = activities.minOf { it.date }
        return activities.groupBy { it.what }
            .map { (key, groupedActivities) ->
                var daysSinceStarted = abs(ChronoUnit.DAYS.between(LocalDate.now(), everStartDate))
                daysSinceStarted = if (daysSinceStarted <= 0) 1 else daysSinceStarted
                val totalMinutes = groupedActivities.sumOf { it.durationInMinutes }

                AllTimeActivity(
                    what = key,
                    startDate = groupedActivities.minOf { it.date },
                    dailyDurationAverageInMinutes = (totalMinutes.toDouble() / daysSinceStarted),
                    totalMinutes = totalMinutes
                )
            }.sortedByDescending { it.totalMinutes }
    }
}