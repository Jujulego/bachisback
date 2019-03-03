package net.capellari.julien.bachisback.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.capellari.julien.bachisback.R

@Database(version = 1,
    entities = [Photo::class, Partition::class]
)
abstract class AppDatabase : RoomDatabase() {
    // Companion
    companion object {
        fun database(context: Context): AppDatabase {
            val ctx = context.applicationContext

            return Room.databaseBuilder(ctx, AppDatabase::class.java, ctx.getString(R.string.database))
                .build()
        }
    }

    // Daos
    abstract fun photoDao(): Photo.PhotoDao
    abstract fun pisteDao(): Partition.PartitionDao
}