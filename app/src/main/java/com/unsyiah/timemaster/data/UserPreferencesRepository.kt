package com.unsyiah.timemaster.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserPreferences(
    val countDownEnabled: Boolean,
    val countDownEndTime: Long
)

object PreferenceKeys {
    val COUNT_DOWN_END_TIME = longPreferencesKey("count_down_end_time")
    val COUNT_DOWN_ENABLED = booleanPreferencesKey("count_down_enabled")
}

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val countDownEnabled = preferences[PreferenceKeys.COUNT_DOWN_ENABLED] ?: false
        val countDownEndTime = preferences[PreferenceKeys.COUNT_DOWN_END_TIME] ?: 0
        return UserPreferences(countDownEnabled, countDownEndTime)
    }

    suspend fun updateCountDownEndTime(endTime: Long) {
        dataStore.edit {
            it[PreferenceKeys.COUNT_DOWN_END_TIME] = endTime
        }
    }

    suspend fun updateCountDownEnabled(enabled: Boolean) {
        dataStore.edit {
            it[PreferenceKeys.COUNT_DOWN_ENABLED] = enabled
        }
    }
}