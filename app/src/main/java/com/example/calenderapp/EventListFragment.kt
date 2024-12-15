package com.example.calenderapp

import EventListAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
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

        AlertDialog.Builder(requireContext())
            .setTitle("New Event")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()
                val newEvent = Event(
                    id = UUID.randomUUID(),
                    title = title,
                    description = description,
                    date = Date()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    eventListViewModel.addEvent(newEvent)
                    findNavController().navigate(
                        EventListFragmentDirections.showEventDetail(newEvent.id)
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
