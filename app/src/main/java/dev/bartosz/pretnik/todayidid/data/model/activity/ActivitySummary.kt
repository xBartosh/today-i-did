package dev.bartosz.pretnik.todayidid.data.model.activity

import androidx.room.ColumnInfo
import java.time.LocalDate

data class ActivitySummary(
    @ColumnInfo(name = "what") val what: String,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "durationInMinutes") val durationInMinutes: Int
)
