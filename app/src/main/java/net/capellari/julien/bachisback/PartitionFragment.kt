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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_partition.view.*
import kotlinx.android.synthetic.main.item_photo.view.*
import net.capellari.julien.bachisback.db.Partition
import net.capellari.julien.bachisback.db.Photo

class PartitionFragment : Fragment() {
    // Attributs
    var adapter = PhotosAdapter()
    lateinit var model: PartitionsModel

    private var partition: Partition? = null
        set(value) {
            field = value

            // Récupération des photos
            if (value != null) {
                model.allPhotos(value)
                    .observe(this, Observer<Array<Photo>> {
                        adapter.photos = it
                    })
            }
        }

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Recup Model
        model = ViewModelProviders.of(requireActivity())[PartitionsModel::class.java]
        model.getPartition(arguments!!.getInt("partition_id"))
            .observe(this, Observer<Partition> {
                partition = it
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_partition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.photos.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
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

    // Classes
    inner class PhotosAdapter : RecyclerView.Adapter<PhotoHolder>() {
        // Attributs
        var photos: Array<Photo> = arrayOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        // Méthodes
        override fun getItemCount(): Int = photos.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            return PhotoHolder(parent.inflate(R.layout.item_photo, false))
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(photos[position])
        }
    }

    inner class PhotoHolder(val view: View) : RecyclerView.ViewHolder(view) {
        // Attributs
        private var photo: Photo? = null

        // Initialisation
        init {
            view.setOnClickListener {
                photo?.let {
                    findNavController()
                        .navigate(R.id.fragment_photo, bundleOf("photo_id" to it.id))
                }
            }
        }

        // Méthodes
        fun bind(photo: Photo) {
            this.photo = photo

            view.text.text = "Photo n°${photo.id}"
        }
    }
}