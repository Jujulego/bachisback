package net.capellari.julien.bachisback

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import net.capellari.julien.bachisback.db.Partition

class PartitionsAdapter(val model: PartitionsModel) : RecyclerView.Adapter<PartitionHolder>() {
    // Attributs
    var partitions: Array<Partition> = arrayOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val observer = Observer<Array<Partition>> { partitions = it }

    // Méthodes
    override fun getItemCount() = partitions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartitionHolder {
        return PartitionHolder(
            parent.inflate(
                R.layout.item_partition,
                false
            ), model
        )
    }

    override fun onBindViewHolder(holder: PartitionHolder, position: Int) {
        holder.bind(partitions[position])
    }
}