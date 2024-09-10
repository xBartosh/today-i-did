package dev.bartosz.pretnik.todayidid.ui.daily

import dev.bartosz.pretnik.todayidid.data.model.activity.Activity

interface ActivityInteractionListener {
    fun onUpdateActivity(activity: Activity, instantUpdate: Boolean)
    fun onRemoveActivity(activity: Activity)
}