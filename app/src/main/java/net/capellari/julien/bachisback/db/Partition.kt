package net.capellari.julien.bachisback.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Partition(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var nom: String
) {
    // Dao
    @Dao
    interface PartitionDao {
        @Insert
        fun insertPartition(vararg partitions: Partition)

        @Query("select * from Partition")
        fun allPartitions(): LiveData<Array<Partition>>

        @Update
        fun updatePartition(vararg partitions: Partition)

        @Delete
        fun deletePartition(vararg partitions: Partition)
    }
}