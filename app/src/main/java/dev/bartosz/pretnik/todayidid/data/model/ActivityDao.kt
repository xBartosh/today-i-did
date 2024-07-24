package dev.bartosz.pretnik.todayidid.data.model

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

    @Insert
    suspend fun insertActivity(activity: Activity)

    @Delete
    suspend fun removeActivity(activity: Activity): Int

    @Query("DELETE FROM activities WHERE what = :activityName AND date = :date")
    suspend fun removeAllActivities(activityName: String, date: LocalDate): Int
}