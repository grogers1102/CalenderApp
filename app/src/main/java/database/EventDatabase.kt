package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calenderapp.Event

@Database(entities = [Event::class], version = 3)
@TypeConverters(EventTypeConverter::class)
abstract class EventDatabase: RoomDatabase(){
    abstract fun eventDao(): EventDao
}
val migration_1_2 = object : Migration(1, 2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE event ADD COLUMN title TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE Event ADD COLUMN description TEXT NOT NULL DEFAULT ''")
    }
}