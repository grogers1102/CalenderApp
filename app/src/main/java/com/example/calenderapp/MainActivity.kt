package com.example.calenderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.ui.theme.CalenderAppTheme
import com.example.calenderapp.CalendarAdapter
import java.time.LocalDate

//ComponentActivity()
class MainActivity : AppCompatActivity() {
    private lateinit var calendarRecyclerView: RecyclerView
    private val viewModel: CalendarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Calendar View Test
        // Find RecyclerView in the layout
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)

        // Set GridLayoutManager programmatically
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        // Get current year and month
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue

        // Get calendar data from ViewModel
        val calendarData = viewModel.getCalendarData(currentYear, currentMonth)

        // Set the adapter with the data
        calendarRecyclerView.adapter = CalendarAdapter(calendarData)
        /*
        setContent {
            CalenderAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }*/
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalenderAppTheme {
        Greeting("Android")
    }
}