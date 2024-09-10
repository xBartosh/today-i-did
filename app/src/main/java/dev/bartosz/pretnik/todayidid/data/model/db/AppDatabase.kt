package dev.bartosz.pretnik.todayidid.data.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.bartosz.pretnik.todayidid.data.model.activity.Activity
import dev.bartosz.pretnik.todayidid.data.model.activity.ActivityDao
import dev.bartosz.pretnik.todayidid.data.model.Converters

@Database(entities = [Activity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}