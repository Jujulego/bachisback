package net.capellari.julien.bachisback

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import net.capellari.julien.bachisback.db.Partition

class PartitionFragment : Fragment() {
    // Attributs
    lateinit var model: PartitionsModel
    private var partition: Partition? = null

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Recup Model
        model = ViewModelProviders.of(requireActivity())[PartitionsModel::class.java]
        model.getPartition(arguments!!.getInt("partition_id"))
            .observe(this, Observer<Partition> { partition = it })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_partition, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_partition, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_camera -> {
                Log.d("PartitionFragment", "bcusvnk")
                findNavController().navigate(
                    R.id.action_partition_to_camera,
                    bundleOf("partition_id" to partition?.id)
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}