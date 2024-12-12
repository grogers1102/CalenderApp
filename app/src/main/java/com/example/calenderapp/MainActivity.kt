package com.example.calenderapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.CalendarAdapter
import com.example.calenderapp.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var calendar: CalendarView
    private lateinit var dateView: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendar = findViewById(R.id.calendar)
        dateView = findViewById(R.id.date)

        val createEventButton: Button = findViewById(R.id.createEventButton)
        createEventButton.setOnClickListener {
            navigateToEventDetailFragment()
        }

        val recyclerView: RecyclerView = findViewById(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

    override fun onResume() {
        super.onResume()
        calendar.visibility = View.VISIBLE
        dateView.visibility = View.VISIBLE
    }

    private fun navigateToEventDetailFragment() {
        calendar.visibility = View.GONE
        dateView.visibility = View.GONE

        val eventDetailFragment = EventDetailFragment()
        supportFragmentManager.commit {
            replace(R.id.fragment_container, eventDetailFragment)
            addToBackStack(null)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            calendar.visibility = View.VISIBLE
            dateView.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}
