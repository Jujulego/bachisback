package net.capellari.julien.bachisback.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.capellari.julien.bachisback.R

@Database(version = 2,
    entities = [Photo::class, Partition::class]
)
abstract class AppDatabase : RoomDatabase() {
    // Companion
    companion object {
        // Méthodes
        fun database(context: Context): AppDatabase {
            val ctx = context.applicationContext

            return Room.databaseBuilder(ctx, AppDatabase::class.java, ctx.getString(R.string.database))
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        // Migrations
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Partition ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

    // Daos
    abstract fun photoDao(): Photo.PhotoDao
    abstract fun partitionDao(): Partition.PartitionDao
}