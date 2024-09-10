package dev.bartosz.pretnik.todayidid.data.model.activity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class ActivityRepository(private val activityDao: ActivityDao) {
    val allActivities: Flow<List<Activity>> = activityDao.getAllActivities()

    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>> {
        return activityDao.getActivitiesForDate(date)
    }

    fun getDistinctActivitiesOrderedByMinutes(): Flow<List<ActivitySummary>> {
        return activityDao.getDistinctActivitiesOrderedByMinutes()
    }

    suspend fun getActivityForNameAndDate(what: String, date: LocalDate): Activity? {
        return activityDao.getActivityForNameAndDate(what, date).firstOrNull()
    }

    suspend fun insertActivity(activity: Activity) {
        val existingActivity = getActivityForNameAndDate(activity.what, activity.date)
        if (existingActivity != null) {
            val updatedDuration = existingActivity.durationInMinutes + activity.durationInMinutes
            activityDao.updateActivity(activity.what, activity.date, updatedDuration)
        } else {
            activityDao.insertActivity(activity)
        }
    }

    suspend fun removeActivity(activity: Activity): Boolean {
        return activityDao.removeActivity(activity) > 0
    }

    suspend fun removeActivitiesByNameAndDate(activity: Activity): Int {
        return activityDao.removeAllActivities(activity.what, activity.date)
    }

    suspend fun updateActivity(activity: Activity) {
        val updatedRows = activityDao.updateActivity(activity.what, activity.date, activity.durationInMinutes)
        if (updatedRows == 0) {
            activityDao.insertActivity(activity)
        }
    }
}