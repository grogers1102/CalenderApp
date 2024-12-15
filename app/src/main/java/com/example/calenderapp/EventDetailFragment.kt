package com.example.calenderapp

import android.Manifest
import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.calenderapp.databinding.FragmentEventDetailBinding
import database.EventDatabase
import database.migration_1_2
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class EventDetailFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerFragment.OnTimeSelectedListener {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    private val database by lazy {
        Room.databaseBuilder(
            requireContext(),
            EventDatabase::class.java,
            "event_database"
        ).addMigrations(migration_1_2)
            .build()
    }

    private val args: EventDetailFragmentArgs by navArgs()

    private val eventDetailViewModel: EventDetailViewModel by viewModels {
        EventDetailViewModelFactory(args.eventId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the hint of the button to the current date
        val currentDate = getCurrentFormattedDate()
        binding.eventDate.hint = currentDate

        val currentCalendar = Calendar.getInstance()
        selectedHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        selectedMinute = currentCalendar.get(Calendar.MINUTE)
        updateEventTimeDisplay()

        binding.recurringSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.recurrenceOptions.visibility = View.VISIBLE
            } else {
                binding.recurrenceOptions.visibility = View.GONE
            }
        }

        val recurrenceOptions = arrayOf("Weekly", "Monthly", "Daily")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, recurrenceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.recurrenceSpinner.adapter = adapter

        binding.eventDate.setOnClickListener {
            showDatePickerDialog()
            /* to test database
            lifecycleScope.launch {
                val events = database.eventDao().getAllEvents()
                Log.d("EventDetailFragment", "Fetched events: $events")
            }*/
        }

        binding.eventTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.eventSubmit.setOnClickListener {
            val title = binding.eventTitle.text.toString()
            val description = binding.eventDescription.text.toString()

            if (title.isNotBlank()) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.HOUR_OF_DAY, selectedMinute)
                val newEvent = Event(UUID.randomUUID(), title, description, calendar.time)

                val recurrence = if (binding.recurringSwitch.isChecked) {
                    binding.recurrenceSpinner.selectedItem.toString()
                } else {
                    null
                }
                saveEvent(newEvent, recurrence)
            } else {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateEventTimeDisplay() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        binding.eventTime.text = timeFormat.format(calendar.time)
    }

    private fun showTimePickerDialog() {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.listener = this
        timePickerFragment.show(parentFragmentManager, "timePicker")
    }

    override fun onTimeSelected(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        binding.eventTime.text = timeFormat.format(calendar.time)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDate = calendar.timeInMillis

        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.eventDate.text = sdf.format(Date(selectedDate))
    }

    private fun saveEvent(event: Event, recurrence: String?) {
        lifecycleScope.launch {
            try {
                database.eventDao().addEvent(event)
                scheduleNotification(event, recurrence)
                Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error saving event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun scheduleNotification(event: Event, recurrence: String?) {
        val inputData = workDataOf(
            "eventTitle" to event.title,
            "eventDescription" to event.description
        )

        if (recurrence == null) {
            // Schedule an immediate notification for testing purposes
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
                .setInputData(inputData)  // Set the input data here
                .build()

            WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(
                    "notification_${event.id}",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
        } else {
            // Your existing logic for recurrence can stay
            val workRequest = when (recurrence) {
                "Weekly" -> PeriodicWorkRequestBuilder<NotificationWorker>(7, TimeUnit.DAYS)
                "Monthly" -> PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.DAYS)
                "Daily" -> PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                else -> return
            }
                .setInputData(inputData)  // Set the input data here
                .build()

            WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(
                    "notification_${event.id}",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }

    private fun updateUi(event: Event){
        binding.apply {
            if(eventTitle.text.toString() != event.title){
                eventTitle.setText(event.title)
            }
            eventDate.text = event.date.toString()
            eventDate.setOnClickListener{
                findNavController().navigate(
                    EventDetailFragmentDirections.selectDate(event.date)
                )
            }
            if(eventDescription.text.toString() != event.description){
                eventDescription.setText(event.description)
            }
        }
    }
}