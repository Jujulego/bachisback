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

        adapter = PartitionsAdapter(model)
        model.getPartitions(true).observe(this, adapter.observer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        inflater.inflate(R.menu.toolbar_partitions, menu)
    }
}