package dev.bartosz.pretnik.todayidid.data.model.activity

import java.time.LocalDate

data class AllTimeActivity(
    val what: String,
    val startDate: LocalDate,
    val dailyDurationAverageInMinutes: Double,
    val totalMinutes: Int
)