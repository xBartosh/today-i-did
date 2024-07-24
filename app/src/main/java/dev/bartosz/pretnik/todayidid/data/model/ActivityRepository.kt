package dev.bartosz.pretnik.todayidid.data.model

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.last
import java.time.LocalDate

class ActivityRepository(private val activityDao: ActivityDao) {
    val allActivities: Flow<List<Activity>> = activityDao.getAllActivities()

    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>> {
        return activityDao.getActivitiesForDate(date)
    }

    suspend fun insertActivity(activity: Activity) {
        activityDao.insertActivity(activity)
    }

    suspend fun removeActivity(activity: Activity): Boolean {
        return activityDao.removeActivity(activity) > 0
    }

    suspend fun removeActivitiesByNameAndDate(activity: Activity): Int {
        return activityDao.removeAllActivities(activity.what, activity.date)
    }
}