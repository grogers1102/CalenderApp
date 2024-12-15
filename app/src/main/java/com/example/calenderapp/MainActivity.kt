package com.example.calenderapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var dateView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dateView = findViewById(R.id.date)
        setupToolbar()
        setupCurrentDate()
        setupNavigation()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupCurrentDate() {
        val today = LocalDate.now()
        val formattedDate = today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
        dateView.text = "Today's date is $formattedDate"
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            dateView.visibility =
                if (destination.id == R.id.calendarRecyclerView) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        dateView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            dateView.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}
