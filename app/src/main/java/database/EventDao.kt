package database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import com.example.calenderapp.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getEvents(): Flow<List<Event>>

    @Query("SELECT * FROM event WHERE id=(:id)")
    suspend fun getEvent(id: UUID): Event

    @Update
    suspend fun updateEvent(event: Event)

    @Insert
    suspend fun addEvent(event: Event)

    @Query("SELECT * FROM event")
    suspend fun getAllEvents(): List<Event>

}