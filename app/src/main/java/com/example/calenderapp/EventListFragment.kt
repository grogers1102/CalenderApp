package com.example.calenderapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
    /*private fun showNewEvent(){
        viewLifecycleOwner.lifecycleScope.launch {
            val newEvent = Event(id = UUID.randomUUID(), title = "", description = "", date = Date())
            eventListViewModel.addEvent(newEvent)
            //findNavController().navigate(
            //EventListFragmentDirections.showEventDetail(newEvent.id)
            //)
        }
    }*/
}