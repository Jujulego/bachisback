package net.capellari.julien.bachisback

import android.view.MenuItem
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import net.capellari.julien.bachisback.db.Partition

class PartitionsAdapter(val model: PartitionsModel, @MenuRes val menu_id: Int = R.menu.menu_partition)
        : RecyclerView.Adapter<PartitionHolder>() {

    // Attributs
    var partitions: Array<Partition> = arrayOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val observer = Observer<Array<Partition>> { partitions = it }
    var listener: PartitionHolder.PartitionListener? = null

    // Méthodes
    override fun getItemCount() = partitions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartitionHolder {
        return PartitionHolder(parent.inflate(R.layout.item_partition, false), model, menu_id,
            object : PartitionHolder.PartitionListener {
                override fun onMenuItemSelected(holder: PartitionHolder, item: MenuItem): Boolean {
                    return listener?.onMenuItemSelected(holder, item) ?: false
                }
            }
        )
    }

    override fun onBindViewHolder(holder: PartitionHolder, position: Int) {
        holder.bind(partitions[position])
    }
}