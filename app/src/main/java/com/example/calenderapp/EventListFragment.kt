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
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calenderapp.databinding.FragmentEventListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
class EventListFragment: Fragment(){
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
        return when(item.itemId){
            R.id.action_add_event -> {
                showNewEvent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private lateinit var adapter: EventListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView and Adapter
        binding.eventListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EventListAdapter(emptyList()) { eventId ->
            findNavController().navigate(
                EventListFragmentDirections.showEventDetail(eventId)
            )
        }
        binding.eventListRecyclerView.adapter = adapter

        // Switch to Calendar Button
        binding.switchToCalendar.setOnClickListener {
            findNavController().navigate(R.id.show_calendar)
        }

        // Collect events and update the adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventListViewModel.events.collect { events ->
                    Log.d("EventListFragment", "Fetched events: $events")
                    adapter.updateEvents(events)
                }
            }
        }
    }


    private fun showNewEvent() {
        // Navigate to EventDetailFragment with a placeholder or empty ID
        findNavController().navigate(
            EventListFragmentDirections.showEventDetail(UUID.randomUUID())
        )
    }





}