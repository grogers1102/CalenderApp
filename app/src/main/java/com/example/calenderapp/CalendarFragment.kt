package com.example.calenderapp

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.CalendarGridBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class CalendarFragment : Fragment() {
    private var _binding: CalendarGridBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
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
        return when (item.itemId) {
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
    ): View {
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

        // Set the layout manager for the RecyclerView
        binding.eventListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        // Collect events and update calendar and upcoming events
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventListViewModel.events.collect { events ->
                    Log.d("CalendarFragment", "Fetched Events: $events")

                    // Highlight event dates on the calendar
                    val eventDates = events.map { it.date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() }
                    setupCalendar(calendar, today, events)

                    // Show upcoming events
                    showUpcomingEvents(events)
                }
            }
        }

        binding.switchToList.setOnClickListener {
            findNavController().navigate(R.id.show_event_list)
        }
    }


    private fun setupCalendar(calendar: Calendar, today: Int, events: List<Event>) {
        val days = generateDaysForMonth(calendar)
        binding.calendarRecyclerView.adapter = CalendarAdapter(
            days = days,
            currentDay = today,
            events = events,
            onDayClicked = { event ->
                if (event != null) {
                    // Log and navigate to edit the existing event
                    Log.d("CalendarFragment", "Editing event: ${event.id}")
                    findNavController().navigate(
                        CalendarFragmentDirections.showEventDetail(event.id)
                    )
                } else {
                    // Log and navigate to create a new event
                    Log.d("CalendarFragment", "Creating a new event")
                    val newEventId = UUID.randomUUID()
                    findNavController().navigate(
                        CalendarFragmentDirections.showEventDetail(newEventId)
                    )
                }
            }
        )
    }


    private fun generateDaysForMonth(calendar: Calendar): List<String> {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val days = MutableList(firstDayOfWeek) { "" } // Empty placeholders for alignment
        days.addAll((1..daysInMonth).map { it.toString() }) // Add days of the month
        return days
    }


    private fun showUpcomingEvents(events: List<Event>) {
        val upcomingEvents = events.filter { it.date.after(Date()) }
        Log.d("CalendarFragment", "Upcoming Events: $upcomingEvents")

        if (binding.eventListRecyclerView.adapter == null) {
            binding.eventListRecyclerView.adapter = EventListAdapter(upcomingEvents) { eventId ->
                findNavController().navigate(
                    CalendarFragmentDirections.showEventDetail(eventId)
                )
            }
        } else {
            (binding.eventListRecyclerView.adapter as EventListAdapter).updateEvents(upcomingEvents)
        }

        binding.closeEventsTextView.text = if (upcomingEvents.isEmpty()) {
            "No Upcoming Events"
        } else {
            "Upcoming Events"
        }
    }


    private fun showNewEvent() {
        findNavController().navigate(CalendarFragmentDirections.showEventDetail(UUID.randomUUID()))
    }
    fun Date.toLocalDate(): LocalDate {
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

}
