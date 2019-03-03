package net.capellari.julien.bachisback.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

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
    // Dao
    @Dao
    interface PhotoDao {
        @Insert
        fun insertPhoto(vararg photos: Photo)

        @Update
        fun updatePhoto(vararg photos: Photo)

        @Delete
        fun deletePhoto(vararg photos: Photo)
    }
}