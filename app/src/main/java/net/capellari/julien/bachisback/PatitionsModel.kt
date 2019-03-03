package net.capellari.julien.bachisback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import net.capellari.julien.bachisback.db.AppDatabase
import net.capellari.julien.bachisback.db.Partition

class PatitionsModel(application: Application): AndroidViewModel(application) {
    // Propriétés
    private val database: AppDatabase by lazy { AppDatabase.database(application) }
    private val partitionDao: Partition.PartitionDao by lazy { database.pisteDao() }

    // Méthodes
    fun getPartitions() = partitionDao.allPartitions()
}