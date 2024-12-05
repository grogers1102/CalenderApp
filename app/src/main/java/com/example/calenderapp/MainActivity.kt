package com.example.calenderapp

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.calenderapp.ui.theme.CalenderAppTheme
import com.example.calenderapp.CalendarAdapter
import com.example.calenderapp.databinding.ActivityMainBinding
import database.EventDao
import database.EventDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var calendarRecyclerView: RecyclerView
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private lateinit var calendar: CalendarView
    private lateinit var dateView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find your views
        calendar = findViewById(R.id.calendar)
        dateView = findViewById(R.id.date)

        // Hide the calendar initially when opening EventDetailFragment
        val createEventButton: Button = findViewById(R.id.createEventButton)
        createEventButton.setOnClickListener {
            // Hide the calendar when navigating to EventDetailFragment
            calendar.visibility = View.GONE
            dateView.visibility = View.GONE // Hide the date text as well if needed

            val eventDetailFragment = EventDetailFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, eventDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // RecyclerView setup (for events)
        val recyclerView: RecyclerView = findViewById(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Calendar view setup
        calendar.isEnabled = true
        val today = LocalDate.now()
        val formattedDate = today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
        dateView.text = "Today's date is $formattedDate"

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue
        val calendarData = viewModel.getCalendarData(currentYear, currentMonth)

        calendarRecyclerView.adapter = CalendarAdapter(calendarData)
    }

    // Ensure calendar is visible when navigating back
    override fun onBackPressed() {
        super.onBackPressed()
        calendar.visibility = View.VISIBLE
        dateView.visibility = View.VISIBLE
    }
}