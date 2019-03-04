package net.capellari.julien.bachisback

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_partitions.view.*

class PartitionsFragment: Fragment() {
    // Attributs
    lateinit var model: PartitionsModel
    lateinit var adapter: PartitionsAdapter

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Recup Model
        model = ViewModelProviders.of(requireActivity())[PartitionsModel::class.java]

        adapter = PartitionsAdapter(model)
        adapter.listener = object : PartitionHolder.PartitionListener {
            override fun onMenuItemSelected(holder: PartitionHolder, item: MenuItem): Boolean {
                return when(item.itemId) {
                    R.id.delete -> { holder.toTrash(); true }
                    else -> false
                }
            }
        }

        model.getPartitions().observe(this, adapter.observer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup !
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_partitions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.partitions.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())

            val helper = ItemTouchHelper(PartitionsTouchCallback())
            helper.attachToRecyclerView(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_partitions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_ajout_partition -> { AjoutPartitionDialog().show(childFragmentManager, "dialog"); true }
            else -> false
        }
    }

    // Classes
    class PartitionsTouchCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
                = makeMovementFlags(0, ItemTouchHelper.END)

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)
                = false

        override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
            if (holder is PartitionHolder) holder.toTrash()
        }
    }
}