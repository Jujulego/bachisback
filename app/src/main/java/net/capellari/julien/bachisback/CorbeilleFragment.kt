package net.capellari.julien.bachisback

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_partitions.view.*
import net.capellari.julien.bachisback.db.Partition

class CorbeilleFragment: Fragment() {
    // Attributs
    lateinit var model: PartitionsModel
    lateinit var adapter: PartitionsAdapter

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Recup Model
        model = ViewModelProviders.of(requireActivity())[PartitionsModel::class.java]

        adapter = PartitionsAdapter(model, R.menu.menu_partition_corbeille)
        adapter.listener = object : PartitionHolder.PartitionListener {
            override fun onMenuItemSelected(holder: PartitionHolder, item: MenuItem): Boolean {
                return when(item.itemId) {
                    R.id.delete  -> { holder.delete();  true }
                    R.id.restore -> { holder.restore(); true }
                    else -> false
                }
            }
        }

        model.getPartitions(true).observe(this, adapter.observer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_partitions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.partitions.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_corbeille, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.delete_all -> { model.deletePartition(*adapter.partitions); true }
            else -> false
        }
    }
}