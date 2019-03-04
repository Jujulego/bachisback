package net.capellari.julien.bachisback

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_partition.view.*
import net.capellari.julien.bachisback.db.Partition

class PartitionHolder(val view: View, val model: PartitionsModel) : RecyclerView.ViewHolder(view) {
    // Attributs
    var partition: Partition? = null

    // Propriétés
    val context get() = view.context

    // Méthodes
    fun bind(partition: Partition) {
        this.partition = partition

        // Affichage
        view.nom.text = partition.nom
    }

    fun toTrash() {
        partition?.let {
            it.deleted = true
            model.updatePartition(it)

            Snackbar.make(
                view,
                context.getString(R.string.partition_deleted, it.nom),
                Snackbar.LENGTH_LONG
            )
                .setAction(android.R.string.cancel) { _ ->
                    it.deleted = false
                    model.updatePartition(it)
                }
                .show()
        }
    }
}