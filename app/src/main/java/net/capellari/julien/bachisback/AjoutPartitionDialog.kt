package net.capellari.julien.bachisback

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        // Inflate layout
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_ajout_partition, null)

        // Dialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.label_ajout_partition)

        builder.setView(layout)
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            model.addPartition(layout.nom.text.toString())
        }

        val dialog = builder.create()

        // Events
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }

        layout.nom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return dialog
    }
}