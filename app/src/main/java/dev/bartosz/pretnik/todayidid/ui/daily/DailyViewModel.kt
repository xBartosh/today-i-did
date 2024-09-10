package dev.bartosz.pretnik.todayidid.ui.daily

import androidx.lifecycle.*
import dev.bartosz.pretnik.todayidid.data.model.activity.Activity
import dev.bartosz.pretnik.todayidid.data.model.activity.ActivityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyViewModel(private val repository: ActivityRepository) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()
    private val _removeResult = MutableLiveData<Boolean>()
    val removeResult: LiveData<Boolean> = _removeResult

    val activities = _selectedDate.flatMapLatest { date ->
        repository.getActivitiesForDate(date)
    }.map { activities ->
        aggregateActivities(activities)
    }.asLiveData()

    private fun aggregateActivities(activities: List<Activity>): List<Activity> {
        return activities.sortedByDescending { it.durationInMinutes }
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

    fun updateActivity(activity: Activity) {
        viewModelScope.launch {
            repository.updateActivity(activity)
        }
    }
}