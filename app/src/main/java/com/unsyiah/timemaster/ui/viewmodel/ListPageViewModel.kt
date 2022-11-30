package com.unsyiah.timemaster.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.unsyiah.timemaster.data.TimeMasterEvent
import com.unsyiah.timemaster.data.TimeMasterEventDao
import com.unsyiah.timemaster.util.decorateMillisToDateString
import kotlinx.coroutines.launch

class ListPageViewModel(
    private val database: TimeMasterEventDao,
    timeMasterEvents: LiveData<List<TimeMasterEvent>>,
    application: Application
): AndroidViewModel(application) {
    val groupedEventsByDate = Transformations.map(timeMasterEvents) { events ->
        val listRows: List<ListRow> = events.map {
            ListRow(it.name, it.startTime, it.endTime, it.id)
        }
        listRows.groupBy {
            decorateMillisToDateString(it.startTime)
        }
    }
    var editingEventId by mutableStateOf(-1L)

    fun changeEditId(id: Long) {
        editingEventId = id
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            val eventToDelete = database.get(id)
            if(eventToDelete != null) {
                database.delete(eventToDelete)
                editingEventId = -1
            } else {
                showToast("Failed to delete event")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}