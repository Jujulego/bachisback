package net.capellari.julien.bachisback.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.io.File

@Entity(
    foreignKeys = [
        ForeignKey(entity = Partition::class,
            parentColumns = ["id"],
            childColumns = ["partition"],
            onDelete = CASCADE
        )
    ]
)
data class Photo(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var partition: Int,
    var file: String
) {
    // Méthode
    fun getFile(context: Context): File {
        return File(context.filesDir, this.file)
    }

    // Dao
    @Dao
    interface PhotoDao {
        @Query("select * from photo where id = :id")
        fun getById(id: Int): LiveData<Photo>

        @Query("select * from photo where partition = :partition")
        fun allByPartition(partition: Int): LiveData<Array<Photo>>

        @Insert
        fun insertPhoto(vararg photos: Photo)

        @Update
        fun updatePhoto(vararg photos: Photo)

        @Delete
        fun deletePhoto(vararg photos: Photo)
    }
}