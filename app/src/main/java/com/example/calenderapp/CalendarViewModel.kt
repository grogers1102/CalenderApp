package com.example.calenderapp

import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel: ViewModel() {
    fun getCalendarData(year: Int, month: Int): List<Pair<DayOfWeek, Int?>> {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek

        val calendarData = mutableListOf<Pair<DayOfWeek, Int?>>()

        // Add empty days before the first day of the month for alignment
        repeat(firstDayOfMonth.ordinal) {
            calendarData.add(Pair(DayOfWeek.of(it + 1), null))
        }
        // Add each day of the month
        for (day in 1..daysInMonth) {
            val dayOfWeek = LocalDate.of(year, month, day).dayOfWeek
            calendarData.add(Pair(dayOfWeek, day))
        }
        return calendarData
    }
}