package dev.bartosz.pretnik.todayidid.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.bartosz.pretnik.todayidid.ui.alltime.AllTimeViewModel
import dev.bartosz.pretnik.todayidid.ui.daily.DailyViewModel
import dev.bartosz.pretnik.todayidid.ui.todayidid.TodayIDidViewModel

class ViewModelFactory(private val repository: ActivityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TodayIDidViewModel::class.java) -> TodayIDidViewModel(repository) as T
            modelClass.isAssignableFrom(DailyViewModel::class.java) -> DailyViewModel(repository) as T
            modelClass.isAssignableFrom(AllTimeViewModel::class.java) -> AllTimeViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}