package dev.bartosz.pretnik.todayidid.data.model.activity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val what: String,
    val date: LocalDate,
    val durationInMinutes: Int
)
