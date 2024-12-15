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

        binding.switchToCalendar.setOnClickListener {
            findNavController().navigate(R.id.show_calendar)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventListViewModel.events.collect { events ->
                    // Format date and time for each event
                    val formattedEvents = events.map { event ->
                        // Format the date and time separately
                        val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(event.date)
                        val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(event.date)

                        // Return the event along with the formatted date and time
                        event to Pair(formattedDate, formattedTime)
                    }

                    // Set up RecyclerView with the formatted date and time
                    binding.eventListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.eventListRecyclerView.adapter = EventListAdapter(formattedEvents) { eventId ->
                        findNavController().navigate(
                            EventListFragmentDirections.showEventDetail(eventId)
                        )
                    }
                }
            }
        }
    }

    private fun showNewEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newEvent = Event(
                id = UUID.randomUUID(),
                title = "New event",
                description = "Lorem ipsum",
                date = Date(),
            )
            Log.d("EventListFragment", "Inserting new event: $newEvent")
            eventListViewModel.addEvent(newEvent)
            findNavController().navigate(
                EventListFragmentDirections.showEventDetail(newEvent.id)
            )
        }
    }
}
