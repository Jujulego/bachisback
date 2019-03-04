package net.capellari.julien.bachisback

import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_partition.view.*
import net.capellari.julien.bachisback.db.Partition

class PartitionHolder(val view: View, val model: PartitionsModel, @MenuRes val menu_id: Int = R.menu.menu_partition, var listener: PartitionListener? = null)
        : RecyclerView.ViewHolder(view), PopupMenu.OnMenuItemClickListener {

    // Attributs
    var menu: PopupMenu
    var partition: Partition? = null

    // Propriétés
    val context get() = view.context

    // Initialisation
    init {
        // Menu
        menu = PopupMenu(context, view.menu)
        menu.setOnMenuItemClickListener(this)
        menu.inflate(menu_id)

        view.menu.setOnClickListener { menu.show() }
    }

    // Events
    override fun onMenuItemClick(item: MenuItem): Boolean {
        return listener?.onMenuItemSelected(this, item) ?: false
    }

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

            Snackbar.make(view, context.getString(R.string.partition_deleted, it.nom), Snackbar.LENGTH_LONG)
                .setAction(android.R.string.cancel) { restore() }
                .show()
        }
    }

    fun restore() {
        partition?.let {
            it.deleted = false
            model.updatePartition(it)
        }
    }

    fun delete() {
        partition?.let {
            model.deletePartition(it)

            Snackbar.make(view, context.getString(R.string.partition_deleted, it.nom), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    // Interface
    interface PartitionListener {
        fun onMenuItemSelected(holder: PartitionHolder, item: MenuItem): Boolean
    }
}