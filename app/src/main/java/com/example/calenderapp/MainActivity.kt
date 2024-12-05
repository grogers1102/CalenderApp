package com.example.calenderapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var navController: NavController
    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Find RecyclerView in the layout
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)

        // Find arrow buttons in the layout
        val leftArrowButton: Button = findViewById(R.id.leftArrowButton)
        val rightArrowButton: Button = findViewById(R.id.rightArrowButton)

        // Set GridLayoutManager programmatically
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        // Get current year and month
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue

        // Get calendar data from ViewModel
        val calendarData = viewModel.getCalendarData(currentYear, currentMonth)

        // Set the adapter with the data
        calendarRecyclerView.adapter = CalendarAdapter(calendarData)

        // Find the createEventButton in the layout
        val createEventButton: Button = findViewById(R.id.createEventButton)
        createEventButton.setOnClickListener {
            // Navigate to EventDetailFragment
            navController.navigate(R.id.action_eventListFragment_to_eventDetailFragment)
        }
    }
}
