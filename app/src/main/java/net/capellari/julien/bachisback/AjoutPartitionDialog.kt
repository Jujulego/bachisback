package net.capellari.julien.bachisback

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.dialog_ajout_partition.view.*

class AjoutPartitionDialog : DialogFragment() {
    // Attributs
    private lateinit var model: PartitionsModel

    // Events
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Récup model
        model = ViewModelProviders.of(requireActivity())[PartitionsModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater

        // Inflate layout
        val layout = inflater.inflate(R.layout.dialog_ajout_partition, null)

        // Dialog
        builder.setTitle(R.string.label_ajout_partition)

        builder.setView(layout)
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            model.addPartition(layout.nom.text.toString())
        }

        return builder.create()
    }
}