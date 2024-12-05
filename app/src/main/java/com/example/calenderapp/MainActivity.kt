package com.example.calenderapp

import android.os.Bundle
import android.text.TextUtils.replace
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

        binding.createEventButton.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, EventListFragment())
                addToBackStack(null)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val createEventButton: Button = findViewById(R.id.createEventButton)
        createEventButton.setOnClickListener {
            val eventDetailFragment = EventDetailFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, eventDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        calendar = findViewById(R.id.calendar)
        dateView = findViewById(R.id.date)

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
}
