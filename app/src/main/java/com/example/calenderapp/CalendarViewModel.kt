package com.example.calenderapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class CalendarViewModel : ViewModel() {

    private val _days = MutableStateFlow<List<String>>(emptyList())
    val days: StateFlow<List<String>> = _days.asStateFlow()

    init {
        loadCurrentMonth()
    }

    private fun loadCurrentMonth() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        _days.value = generateDaysForMonth(year, month)
    }

    private fun generateDaysForMonth(year: Int, month: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Adjust to start from Sunday

        val days = MutableList(firstDayOfWeek) { "" } // Empty placeholders
        days.addAll((1..daysInMonth).map { it.toString() })

        // Log the generated days for debugging
        Log.d("CalendarViewModel", "Generated days: $days")
        return days
    }
}