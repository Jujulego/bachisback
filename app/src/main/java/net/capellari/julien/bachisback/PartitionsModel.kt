package net.capellari.julien.bachisback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import net.capellari.julien.bachisback.db.AppDatabase
import net.capellari.julien.bachisback.db.Partition
import org.jetbrains.anko.doAsync

class PartitionsModel(application: Application): AndroidViewModel(application) {
    // Propriétés
    private val database: AppDatabase by lazy { AppDatabase.database(application) }
    private val partitionDao: Partition.PartitionDao by lazy { database.partitionDao() }

    // Méthodes
    fun addPartition(nom: String) {
        doAsync {
            val partition = Partition(0, nom)
            partitionDao.insertPartition(partition)
        }
    }
    fun getPartitions() = partitionDao.allPartitions()
    fun updatePartition(vararg partitions: Partition) {
        doAsync {
            partitionDao.updatePartition(*partitions)
        }
    }
}