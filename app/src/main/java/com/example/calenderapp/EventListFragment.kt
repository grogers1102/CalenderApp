package com.example.calenderapp

import EventListAdapter
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calenderapp.databinding.FragmentEventListBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class EventListFragment : Fragment() {
    private lateinit var dateView: TextView
    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private val eventListViewModel: EventListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun showDatePickerDialog(dateTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Format and display the selected date
            val formattedDate = "$selectedMonth/${selectedDay + 1}/$selectedYear"
            dateTextView.text = formattedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            // Format and display the selected time
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            val formattedTime = timeFormat.format(calendar.time)
            timeTextView.text = formattedTime
        }, hour, minute, false)

        timePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBackToCalendar.setOnClickListener {
            findNavController().navigate(R.id.show_calendar)
        }

        // Observe the events from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventListViewModel.events.collect { events ->
                    // Format date and time for each event
                    val formattedEvents = events.map { event ->
                        val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(event.date)
                        val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(event.date)
                        event to Pair(formattedDate, formattedTime)
                    }

                    // Set up RecyclerView with the formatted events
                    binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.eventRecyclerView.adapter = EventListAdapter(formattedEvents) { eventId ->
                        // Handle event click
                        findNavController().navigate(
                            EventListFragmentDirections.showEventDetail(eventId)
                        )
                    }
                }
            }
        }
    }

    private fun showNewEvent() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_event, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.input_event_title)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.input_event_description)
        val dateButton = dialogView.findViewById<Button>(R.id.input_event_date)
        val timeButton = dialogView.findViewById<Button>(R.id.input_event_time)

        // Set listeners for the buttons to show the date and time pickers
        dateButton.setOnClickListener {
            showDatePickerDialog(dateButton)  // Pass the Button to update the date text
        }

        timeButton.setOnClickListener {
            showTimePickerDialog(timeButton)  // Pass the Button to update the time text
        }

        AlertDialog.Builder(requireContext())
            .setTitle("New Event")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()

                if (title.isNotBlank()) {
                    val selectedDate = dateButton.text.toString()
                    val selectedTime = timeButton.text.toString()

                    val calendar = Calendar.getInstance()
                    try {
                        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val date = dateFormat.parse(selectedDate)
                        val time = timeFormat.parse(selectedTime)

                        // Set the time of day on the date object
                        calendar.time = date
                        calendar.set(Calendar.HOUR_OF_DAY, time.hours)
                        calendar.set(Calendar.MINUTE, time.minutes)

                        // Create a new Event
                        val newEvent = Event(
                            id = UUID.randomUUID(),
                            title = title,
                            description = description,
                            date = calendar.time
                        )

                        // Add the event to the ViewModel
                        viewLifecycleOwner.lifecycleScope.launch {
                            eventListViewModel.addEvent(newEvent)
                            findNavController().navigate(
                                EventListFragmentDirections.showEventDetail(newEvent.id)
                            )
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Invalid date or time format", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
