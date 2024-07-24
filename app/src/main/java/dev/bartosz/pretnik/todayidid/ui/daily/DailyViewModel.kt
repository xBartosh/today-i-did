package dev.bartosz.pretnik.todayidid.ui.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.bartosz.pretnik.todayidid.data.model.Activity
import dev.bartosz.pretnik.todayidid.data.model.ActivityRepository
import dev.bartosz.pretnik.todayidid.ui.alltime.AllTimeActivityAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyViewModel(private val repository: ActivityRepository) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _removeResult = MutableLiveData<Boolean>()
    val removeResult: LiveData<Boolean> = _removeResult

    val activities = _selectedDate.flatMapLatest { date ->
        repository.getActivitiesForDate(date)
    }.map { activities ->
        aggregateActivities(activities)
    }.asLiveData()

    private fun aggregateActivities(activities: List<Activity>): List<Activity> {
        return activities.groupBy { it.what }
            .map { (key, groupedActivities) ->
                Activity(
                    what = key,
                    date = groupedActivities.first().date,
                    durationInMinutes = groupedActivities.sumOf { it.durationInMinutes }
                )
            }
            .sortedByDescending { it.durationInMinutes }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun removeActivity(activity: Activity) {
        viewModelScope.launch {
            val howManyRemoved = repository.removeActivitiesByNameAndDate(activity)
            _removeResult.postValue(howManyRemoved > 0)
        }
    }
}