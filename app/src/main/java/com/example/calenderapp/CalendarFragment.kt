package com.example.calenderapp

import EventListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.CalendarGridBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CalendarFragment: Fragment() {
    private var _binding: CalendarGridBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val eventListViewModel: EventListViewModel by viewModels()
    private lateinit var dateView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_add_event -> {
                showNewEvent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarGridBinding.inflate(inflater, container, false)
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the current month and year
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthName = dateFormat.format(calendar.time)

        // Set the month name in the TextView
        binding.monthTextView.text = monthName
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        // Initialize RecyclerView here as usual with an empty adapter first
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)

        // Initialize the adapter with placeholder values
        var calendarAdapter = CalendarAdapter(
            generateDaysForMonth(calendar),
            eventDates = emptyList(), // Initially no events
            currentDay = today,
            onDayClicked = { day ->
                Toast.makeText(context, "Clicked: $day", Toast.LENGTH_SHORT).show()
            }
        )
        binding.calendarRecyclerView.adapter = calendarAdapter

        // Handle the switch to the event list
        binding.switchToList.setOnClickListener {
            findNavController().navigate(R.id.show_event_list)
        }

        // Collect events from the ViewModel and update the adapter dynamically
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventListViewModel.events.collect { events ->
                    // Extract event dates
                    val eventDates = events.map { it.date }

                    // Update the calendar adapter with the event dates
                    calendarAdapter = CalendarAdapter(
                        generateDaysForMonth(calendar),
                        eventDates = eventDates, // Pass the event dates
                        currentDay = today,
                        onDayClicked = { day ->
                            Toast.makeText(context, "Clicked: $day", Toast.LENGTH_SHORT).show()
                        }
                    )
                    binding.calendarRecyclerView.adapter = calendarAdapter

                    // Optionally update the event list if displayed
                    val formattedEvents = events.map { event ->
                        val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(event.date)
                        val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(event.date)
                        event to Pair(formattedDate, formattedTime)
                    }
                    binding.eventListRecyclerView.adapter = EventListAdapter(formattedEvents) { eventId ->
                        findNavController().navigate(
                            CalendarFragmentDirections.showEventDetail(eventId)
                        )
                    }
                }
            }
        }
    }
    private fun showNewEvent(){
        viewLifecycleOwner.lifecycleScope.launch{
            val newEvent = Event(id = UUID.randomUUID(), title = "", date = Date(), description = "")
            eventListViewModel.addEvent(newEvent)
            findNavController().navigate(
                CalendarFragmentDirections.showEventDetail(newEvent.id)
            )
        }
    }
    private fun generateDaysForMonth(calendar: Calendar): List<String> {
        // Same logic as before to generate days with placeholders
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val days = MutableList(firstDayOfWeek) { "" } // Empty placeholders
        days.addAll((1..daysInMonth).map { it.toString() })
        return days
    }
}