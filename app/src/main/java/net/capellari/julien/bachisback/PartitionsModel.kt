package net.capellari.julien.bachisback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.capellari.julien.bachisback.db.AppDatabase
import net.capellari.julien.bachisback.db.Partition
import net.capellari.julien.bachisback.db.Photo
import org.jetbrains.anko.doAsync

class PartitionsModel(application: Application): AndroidViewModel(application) {
    // Propriétés
    private val database: AppDatabase by lazy { AppDatabase.database(application) }
    private val partitionDao: Partition.PartitionDao by lazy { database.partitionDao() }
    private val photoDao: Photo.PhotoDao by lazy { database.photoDao() }

    // Méthodes
    fun addPartition(nom: String) {
        doAsync {
            val partition = Partition(0, nom)
            partitionDao.insertPartition(partition)
        }
    }

    fun getPartition(id: Int) = partitionDao.getPartition(id)

    fun allPartitions(fromTrash: Boolean = false): LiveData<Array<Partition>> {
        return if (fromTrash) {
            partitionDao.allDeletedPartitions()
        } else {
            partitionDao.allPartitions()
        }
    }

    fun updatePartition(vararg partitions: Partition) {
        doAsync {
            partitionDao.updatePartition(*partitions)
        }
    }

    fun deletePartition(vararg partitions: Partition) {
        doAsync {
            partitionDao.deletePartition(*partitions)
        }
    }

    fun addPhoto(vararg photos: Photo) {
        doAsync {
            photoDao.insertPhoto(*photos)
        }
    }
}