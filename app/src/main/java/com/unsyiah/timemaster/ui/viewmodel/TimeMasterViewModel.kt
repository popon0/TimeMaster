package com.unsyiah.timemaster.ui.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import com.unsyiah.timemaster.R
import com.unsyiah.timemaster.data.TimeMasterEventDao
import com.unsyiah.timemaster.data.UserPreferencesRepository

class TimeMasterViewModel (
    application: Application,
    database: TimeMasterEventDao,
    userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application) {

    private var timeMasterEvents = database.getAllEvents()

    @RequiresApi(Build.VERSION_CODES.M)
    val clockPage = ClockPageViewModel(
        application = application,
        database = database,
        userPreferencesRepository = userPreferencesRepository,
        timeMasterEvents = timeMasterEvents
    )

    val listPage = ListPageViewModel(
        database,
        timeMasterEvents,
        application
    )

    val analysisPage = AnalysisPageViewModel(
        timeMasterEvents,
        application.applicationContext
    )
}

sealed class Screen(
    @StringRes val routeResourceId: Int,
    @StringRes val labelResourceId: Int,
    @DrawableRes val iconResourceId: Int
) {
    object Clock :
        Screen(R.string.route_clock, R.string.label_clock, R.drawable.ic_baseline_check_circle_outline_24)

    object List :
        Screen(R.string.route_list, R.string.label_list, R.drawable.ic_baseline_list_24)

    object Metrics :
        Screen(R.string.route_metrics, R.string.label_metrics, R.drawable.ic_baseline_pie_chart_24)
}
