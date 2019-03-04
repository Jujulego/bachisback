package net.capellari.julien.bachisback.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Partition(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var nom: String,
    var deleted: Boolean = false
) {
    // Dao
    @Dao
    interface PartitionDao {
        @Insert
        fun insertPartition(vararg partitions: Partition)

        @Query("select * from Partition where not deleted")
        fun allPartitions(): LiveData<Array<Partition>>

        @Query("select * from Partition where deleted")
        fun allDeletedPartitions(): LiveData<Array<Partition>>

        @Update
        fun updatePartition(vararg partitions: Partition)

        @Delete
        fun deletePartition(vararg partitions: Partition)
    }
}