package dev.bartosz.pretnik.todayidid.data.model.activity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities")
    fun getAllActivities(): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE date = :date")
    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>>

    @Query("SELECT what, MAX(date) as date, SUM(durationInMinutes) as durationInMinutes FROM activities GROUP BY what ORDER BY SUM(durationInMinutes) DESC")
    fun getDistinctActivitiesOrderedByMinutes(): Flow<List<ActivitySummary>>

    @Query("SELECT * FROM activities WHERE what = :what AND date = :date LIMIT 1")
    fun getActivityForNameAndDate(what: String, date: LocalDate): Flow<Activity?>

    @Insert
    suspend fun insertActivity(activity: Activity)

    @Delete
    suspend fun removeActivity(activity: Activity): Int

    @Query("DELETE FROM activities WHERE what = :activityName AND date = :date")
    suspend fun removeAllActivities(activityName: String, date: LocalDate): Int

    @Query("UPDATE activities SET durationInMinutes = :durationInMinutes WHERE what = :what AND date = :date")
    suspend fun updateActivity(what: String, date: LocalDate, durationInMinutes: Int): Int
}