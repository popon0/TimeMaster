package com.unsyiah.timemaster.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
//import android.net.Uri
import android.os.Build
//import android.os.PowerManager
//import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.AlarmManagerCompat
//import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.unsyiah.timemaster.R
import com.unsyiah.timemaster.data.TimeMasterEvent
import com.unsyiah.timemaster.data.TimeMasterEventDao
import com.unsyiah.timemaster.data.UserPreferencesRepository
import com.unsyiah.timemaster.receiver.AlarmReceiver
import com.unsyiah.timemaster.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

const val TAG = "ClockPageViewModel"

@RequiresApi(Build.VERSION_CODES.M)
class ClockPageViewModel (
    application: Application,
    private val database: TimeMasterEventDao,
    timeMasterEvents: LiveData<List<TimeMasterEvent>>,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application) {

    val state: ClockPageViewModelState = ClockPageViewModelState()

    val autofillTaskNames = Transformations.map(timeMasterEvents) { events ->
        state.autofillTaskNames = events.map {
            it.name
        }.toSet()
        state.autofillTaskNames
    }

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    private var currentTimeMasterEvent : TimeMasterEvent? = null
    private var countDownEndTime: Long = 0L

    private val chronometer = Chronometer().apply {
        setOnChronometerTickListener { countUp() }
    }
    private val countDownChronometer = Chronometer().apply {
        setOnChronometerTickListener { countDown() }
    }

    // alarm intent, used to notify the AlarmReceiver when an event is done recording
    private val alarmIntent = Intent(getApplication(), AlarmReceiver::class.java)
    private var pendingAlarmIntent = PendingIntent.getBroadcast(
        getApplication(),
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private var notificationManager = getNotificationManager(getApplication())
    private val alarmManager =
        getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        state.saveCountDownTimerEnabledValue = this::saveCountDownTimerEnabled
        state.saveEventDataOnStart = this::startClock
        state.saveEventDataOnStop = this::stopClock

        notificationManager.cancelAll()

        viewModelScope.launch {
            val preferences = userPreferencesFlow.first()
            state.countDownTimerEnabled = preferences.countDownEnabled
            countDownEndTime = preferences.countDownEndTime

            val currEvent = getCurrentEventFromDatabase()

            if (currEvent != null) {
                currentTimeMasterEvent = currEvent
                state.taskTextFieldValue = TextFieldValue(text = currEvent.name)
                state.isClockRunning = true
                val startTimeDelay = findEventStartTimeDelay(currEvent.startTime)
                if (state.countDownTimerEnabled) {
                    if (countDownEndTime < System.currentTimeMillis()) {
                        currEvent.endTime = countDownEndTime
                        database.update(currEvent)
                        currentTimeMasterEvent = null
                    } else {
                        state.updateCountDownTextFieldValues(
                            calculateCurrCountDownSeconds(countDownEndTime)
                        )
                        countDownChronometer.start(startTimeDelay)
                    }
                } else {
                    state.currSeconds = calculateCurrSeconds(currEvent)
                    chronometer.start(startTimeDelay)
                }
                notificationManager.sendClockInProgressNotification(
                    application,
                    currEvent.name
                )
            }
            Log.i(TAG, "Loading Berhasil")
        }
    }

    private suspend fun getCurrentEventFromDatabase(): TimeMasterEvent? {
        val event = database.getCurrentEvent()
        return if (event == null || !event.isRunning) {
            null
        } else {
            event
        }
    }

    private fun saveCountDownTimerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateCountDownEnabled(enabled)
        }
    }

    private fun timerTextFieldValuesToSeconds(): Int {
        return convertHoursMinutesSecondsToSeconds(
            state.hoursTextFieldValue.text.toInt(),
            state.minutesTextFieldValue.text.toInt(),
            state.secondsTextFieldValue.text.toInt()
        )
    }

    private fun startClock() {
        viewModelScope.launch {
            val newEvent = TimeMasterEvent(
                name = state.taskTextFieldValue.text
            )
            database.insert(newEvent)
            currentTimeMasterEvent = getCurrentEventFromDatabase()
            notificationManager.sendClockInProgressNotification(
                getApplication(),
                newEvent.name
            )
            if (state.countDownTimerEnabled) {
                startCountDown(newEvent.name, newEvent.startTime)
            } else {
                chronometer.start()
            }
        }
    }

    private suspend fun startCountDown(taskName: String, actualStartTime: Long) {
        alarmIntent.putExtra("taskName", taskName)

        pendingAlarmIntent = PendingIntent.getBroadcast(
            getApplication(),
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val currCountDownSeconds = timerTextFieldValuesToSeconds()
        val upcomingEndTime = actualStartTime + currCountDownSeconds * MILLIS_PER_SECOND
        countDownEndTime = upcomingEndTime
        userPreferencesRepository.updateCountDownEndTime(upcomingEndTime)

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            upcomingEndTime,
            pendingAlarmIntent
        )
        countDownChronometer.start()
    }

    private fun stopClock(tappedStopButton: Boolean) {
        viewModelScope.launch {
            val finishedEvent = currentTimeMasterEvent ?: return@launch
            finishedEvent.endTime = System.currentTimeMillis()
            chronometer.stop()
            countDownChronometer.stop()
            database.update(finishedEvent)
            notificationManager.cancelClockInProgressNotification()
            currentTimeMasterEvent = null
            val saved = getApplication<Application>().applicationContext
                .getString(R.string.task_saved_toast, state.taskTextFieldValue.text)
            if (state.countDownTimerEnabled) {
                stopCountDown(tappedStopButton, saved)
            } else {
                showToast(saved)
            }
        }
    }

    private suspend fun stopCountDown(tappedStopButton: Boolean, message: String) {
        if (tappedStopButton) {
            alarmManager.cancel(pendingAlarmIntent)
            showToast(message)
        }
        countDownEndTime = 0L
        userPreferencesRepository.updateCountDownEndTime(countDownEndTime)
        state.isClockRunning = false
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    private fun countUp() {
        val currSeconds = calculateCurrSeconds(currentTimeMasterEvent)
        state.updateCurrSeconds(currSeconds)
    }

    private fun countDown() {
        val currCountDownSeconds = getCountDownSeconds(
            countDownEndTime = countDownEndTime,
            stopClockFunc = this::stopClock
        )
        state.updateCountDownTextFieldValues(currCountDownSeconds)
    }
}

fun getCountDownSeconds(
    countDownEndTime: Long = 0L,
    stopClockFunc: (Boolean) -> Unit = {}
): Int {
    var currSeconds = calculateCurrCountDownSeconds(countDownEndTime)
    if(currSeconds <= 0) {
        stopClockFunc(false)
        currSeconds = 0
    }
    return currSeconds
}

class ClockPageViewModelState(
    taskTextFieldValue: TextFieldValue = TextFieldValue(""),
    autofillTaskNames: Set<String> = setOf(),
    isClockRunning: Boolean = false,
    dropdownExpanded: Boolean = false,
    currSeconds: Int = 0,
    batteryWarningDialogVisible: Boolean = false,
    countDownTimerEnabled: Boolean = false,
    hoursTextFieldValue: TextFieldValue = TextFieldValue(
        text = "00",
        selection = TextRange(0)
    ),
    minutesTextFieldValue: TextFieldValue = TextFieldValue(
        text = "00",
        selection = TextRange(0)
    ),
    secondsTextFieldValue: TextFieldValue = TextFieldValue(
        text = "00",
        selection = TextRange(0)
    ),

    var saveCountDownTimerEnabledValue: (Boolean) -> Unit = {_ -> },
    var onTimerAnimationFinish: () -> Unit = {},
    var saveEventDataOnStart: () -> Unit = { },
    var saveEventDataOnStop: (Boolean) -> Unit = { _ -> },
) {
    val clockButtonEnabled: Boolean
        get() {
            return if (countDownTimerEnabled) {
                val countDownClockIsZero =
                    hoursTextFieldValue.text.toInt() == 0 &&
                            minutesTextFieldValue.text.toInt() == 0 &&
                            secondsTextFieldValue.text.toInt() == 0
                taskTextFieldValue.text.isNotBlank() && !countDownClockIsZero
            } else {
                taskTextFieldValue.text.isNotBlank()
            }
        }
    val filteredEventNames: List<String>
        get() {
            return autofillTaskNames.filter {
                it.contains(taskTextFieldValue.text)
            }
        }
    var taskTextFieldValue by mutableStateOf(taskTextFieldValue)
    var autofillTaskNames by mutableStateOf(autofillTaskNames)
    var isClockRunning by mutableStateOf(isClockRunning)
    var dropdownExpanded by mutableStateOf(dropdownExpanded)

    var currSeconds by mutableStateOf(currSeconds)
    var batteryWarningDialogVisible by mutableStateOf(batteryWarningDialogVisible)
    var countDownTimerEnabled by mutableStateOf(countDownTimerEnabled)
    var hoursTextFieldValue by mutableStateOf(hoursTextFieldValue)
    var minutesTextFieldValue by mutableStateOf(minutesTextFieldValue)
    var secondsTextFieldValue by mutableStateOf(secondsTextFieldValue)
    private var dropdownClicked = false

    fun dismissBatteryWarningDialog() {
        batteryWarningDialogVisible = false
    }

    fun onTaskNameChange(tfv: TextFieldValue) {
        if (dropdownClicked) {
            dropdownClicked = false
            return
        }
        taskTextFieldValue = tfv
        val taskName = tfv.text
        dropdownExpanded = taskName.isNotBlank() && filteredEventNames.isNotEmpty()
    }

    fun dismissDropdown() {
        dropdownExpanded = false
    }

    fun onDropdownMenuItemClick(label: String) {
        dropdownClicked = true
        taskTextFieldValue = TextFieldValue(
            text = label,
            selection = TextRange(label.length)
        )
        dropdownExpanded = false
    }

    fun onMinutesValueChanged(value: TextFieldValue) {
        minutesTextFieldValue = onMinutesOrSecondsValueChanged(value)
    }

    fun onSecondsValueChanged(value: TextFieldValue) {
        secondsTextFieldValue = onMinutesOrSecondsValueChanged(value)
    }

    private fun onMinutesOrSecondsValueChanged(value: TextFieldValue): TextFieldValue {
        return when (value.text.length) {
            0 -> cursorAtEnd(value)
            1 -> {
                if (value.text.toInt() >= 6) {
                    selectAllValue(value)
                } else {
                    cursorAtEnd(value)
                }
            }
            2 -> selectAllValue(value)
            else -> TextFieldValue()
        }
    }

    fun onHoursValueChanged(value: TextFieldValue) {
        hoursTextFieldValue = when (value.text.length) {
            0 -> cursorAtEnd(value)
            1 -> cursorAtEnd(value)
            2 -> selectAllValue(value)
            else -> TextFieldValue()
        }
    }

    private fun onTimerStringFocusChanged(
        focusState: FocusState,
        textFieldValue: TextFieldValue
    ) : TextFieldValue {
        return if(focusState.isFocused) {
            selectAllValue(textFieldValue)
        } else {
            TextFieldValue(
                text = formatDigitsAfterLeavingFocus(textFieldValue.text),
                selection = TextRange(0)
            )
        }
    }

    fun onHoursFocusChanged(focusState: FocusState) {
        hoursTextFieldValue = onTimerStringFocusChanged(focusState, hoursTextFieldValue)
    }

    fun onMinutesFocusChanged(focusState: FocusState) {
        minutesTextFieldValue = onTimerStringFocusChanged(focusState, minutesTextFieldValue)
    }

    fun onSecondsFocusChanged(focusState: FocusState) {
        secondsTextFieldValue = onTimerStringFocusChanged(focusState, secondsTextFieldValue)
    }

    fun updateCurrSeconds(seconds: Int) {
        currSeconds = seconds
    }

    fun updateCountDownTextFieldValues(currSeconds: Int) {
        val hms = convertSecondsToHoursMinutesSeconds(currSeconds)
        hoursTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.first.toString())
        )
        minutesTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.second.toString())
        )
        secondsTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.third.toString())
        )
    }

    private fun formatDigitsAfterLeavingFocus(digits: String): String {
        if (digits.isEmpty()) return "00"
        if (digits.length > 1) return digits
        return "0$digits"
    }

    fun onClockStart() {
        saveEventDataOnStart()
        currSeconds = 0
        isClockRunning = true
    }

    fun onClockStop() {
        saveEventDataOnStop(true)
        isClockRunning = false
    }
}
