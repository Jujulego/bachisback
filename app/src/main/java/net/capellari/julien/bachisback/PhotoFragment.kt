package net.capellari.julien.bachisback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_photo.view.*
import net.capellari.julien.bachisback.db.Photo

class PhotoFragment : Fragment() {
    // Attributs
    lateinit var model: PartitionsModel

    private var imageView: ImageView? = null
    private var photo: Photo? = null

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Recup Model
        model = ViewModelProviders.of(requireActivity())[PartitionsModel::class.java]
        model.getPhoto(arguments!!.getInt("photo_id"))
            .observe(this, Observer<Photo> {
                photo = it
                showImage()
            })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = view.photo
        showImage()
    }

    // Méthodes
    fun showImage() {
        imageView?.let { view ->
            photo?.getFile(requireContext())?.let {
                val img = BitmapFactory.decodeFile(it.absolutePath)
                view.setImageBitmap(img)
            }
        }
    }
}