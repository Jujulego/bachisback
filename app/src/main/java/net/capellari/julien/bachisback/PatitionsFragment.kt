package net.capellari.julien.bachisback

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_pistes.view.*
import kotlinx.android.synthetic.main.item_partition.view.*
import net.capellari.julien.bachisback.db.Partition

class PatitionsFragment: Fragment() {
    // Attributs
    lateinit var model: PatitionsModel
    var adapter = PartitionsAdapter()

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Recup Model
        model = ViewModelProviders.of(requireActivity())[PatitionsModel::class.java]
        model.getPartitions().observe(this, Observer<Array<Partition>> { partitions -> adapter.partitions = partitions })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup !
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pistes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.partitions.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_partitions, menu)
    }

    // Classes
    class PartitionsAdapter : RecyclerView.Adapter<PartitionHolder>() {
        // Attributs
        var partitions: Array<Partition> = arrayOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        // Méthodes
        override fun getItemCount() = partitions.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartitionHolder {
            return PartitionHolder(parent.inflate(R.layout.item_partition, false))
        }

        override fun onBindViewHolder(holder: PartitionHolder, position: Int) {
            holder.bind(partitions[position])
        }
    }

    class PartitionHolder(val view: View) : RecyclerView.ViewHolder(view) {
        // Attributs
        var partition: Partition? = null

        // Méthodes
        fun bind(partition: Partition) {
            this.partition = partition

            // Affichage
            view.nom.text = partition.nom
        }
    }
}