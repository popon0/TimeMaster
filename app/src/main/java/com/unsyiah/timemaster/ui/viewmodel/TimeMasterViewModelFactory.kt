package com.unsyiah.timemaster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unsyiah.timemaster.data.TimeMasterEventDao
import com.unsyiah.timemaster.data.UserPreferencesRepository

class TimeMasterViewModelFactory(
    private val dataSource: TimeMasterEventDao,
    private val application: Application,
    private val userPrefsRepo: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeMasterViewModel::class.java)) {
            return TimeMasterViewModel(
                database = dataSource,
                userPreferencesRepository = userPrefsRepo,
                application = application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
