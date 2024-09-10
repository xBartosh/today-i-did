package dev.bartosz.pretnik.todayidid.ui.todayidid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bartosz.pretnik.todayidid.data.model.activity.Activity
import dev.bartosz.pretnik.todayidid.data.model.activity.ActivityRepository
import dev.bartosz.pretnik.todayidid.data.model.activity.ActivitySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodayIDidViewModel(private val repository: ActivityRepository) : ViewModel() {
    private val _timeQuotes: List<String> = listOf(
        "Time is the most valuable currency so spend it wisely. — Debasish Mridha",
        "Time is what we want most, but what we use worst. — William Penn",
        "The key is in not spending time, but in investing it. — Stephen R. Covey",
        "Time is the most valuable thing a man can spend. — Theophrastus",
        "Lost time is never found again. — Benjamin Franklin",
        "Your time is limited, so don't waste it living someone else's life. — Steve Jobs",
        "It's not that we have little time, but more that we waste a good deal of it. — Seneca",
        "The bad news is time flies. The good news is you're the pilot. — Michael Altshuler",
        "Don't watch the clock; do what it does. Keep going. — Sam Levenson",
        "Yesterday is gone. Tomorrow has not yet come. We have only today. Let us begin. — Mother Teresa",
        "Time is a created thing. To say 'I don't have time' is to say 'I don't want to.' — Lao Tzu",
        "The way we spend our time defines who we are. — Jonathan Estrin",
        "Time is the wisest counselor of all. — Pericles",
        "The best time to plant a tree was 20 years ago. The second best time is now. — Chinese Proverb",
        "Do not wait; the time will never be 'just right.' Start where you stand. — Napoleon Hill",
        "An inch of time is an inch of gold, but you can't buy that inch of time with an inch of gold. — Chinese Proverb",
        "Don't let time slip through your fingers, it's worth more than gold. — Unknown",
        "Time is a valuable asset, invest it wisely. — Unknown",
        "Time is the great equalizer, everyone has the same 24 hours. — Unknown",
        "Time is the bridge between dreams and reality, use it to cross. — Unknown"
    )

    val randomQuote = _timeQuotes.random()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private var activitySummaries: List<ActivitySummary> = emptyList()

    init {
        viewModelScope.launch {
            repository.getDistinctActivitiesOrderedByMinutes().collect { summaries ->
                activitySummaries = summaries
                updateSuggestions("")
            }
        }
    }

    fun updateSuggestions(query: String) {
        viewModelScope.launch {
            _suggestions.value = if (query.isEmpty()) {
                activitySummaries
                    .map { formatActivity(it) }
                    .take(3)
            } else {
                activitySummaries
                    .filter { it.what.contains(query, ignoreCase = true) }
                    .map { formatActivity(it) }
                    .take(5)
            }
        }
    }

    private fun formatActivity(activity: ActivitySummary): String {
        return activity.what.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun addActivity(activity: Activity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }
}