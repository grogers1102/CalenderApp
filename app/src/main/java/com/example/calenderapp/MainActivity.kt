package com.example.calenderapp

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.ui.theme.CalenderAppTheme
import com.example.calenderapp.CalendarAdapter
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var calendarRecyclerView: RecyclerView
    private val viewModel: CalendarViewModel by viewModels()

    // Declare CalendarView and TextView
    private lateinit var calendar: CalendarView
    private lateinit var dateView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendar = findViewById(R.id.calendar)

        calendar.isEnabled = false

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)

        val leftArrowButton: Button = findViewById(R.id.leftArrowButton)
        val rightArrowButton: Button = findViewById(R.id.rightArrowButton)

        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue

        val calendarData = viewModel.getCalendarData(currentYear, currentMonth)

        calendarRecyclerView.adapter = CalendarAdapter(calendarData)

    }
}
