package exercise.okebet.app.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import exercise.okebet.app.R
import exercise.okebet.app.databinding.DialogNewBinding

class NewDialog : DialogFragment(R.layout.dialog_new) {

    private lateinit var binding: DialogNewBinding

    private val title by lazy { arguments?.getString(TITLE) }
    private val description by lazy { arguments?.getString(DESCRIPTION) }
    private val imageUrl by lazy { arguments?.getString(IMAGE_URL) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewBinding.inflate(layoutInflater)

        initNew()

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_new)
    }

    private fun initNew() {
        binding.apply {
            tvTitle.text = title
            tvDescription.text = description
            Glide.with(binding.root).load(imageUrl).into(ivNew)
        }
    }

    companion object {
        const val TITLE = "TITLE"
        const val DESCRIPTION = "DESCRIPTION"
        const val IMAGE_URL = "IMAGE_URL"
    }
}