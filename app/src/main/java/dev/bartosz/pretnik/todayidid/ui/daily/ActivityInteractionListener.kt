package dev.bartosz.pretnik.todayidid.ui.daily

import dev.bartosz.pretnik.todayidid.data.model.Activity

interface ActivityInteractionListener {
    fun onRemoveActivity(activity: Activity)
}