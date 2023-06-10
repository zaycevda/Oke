package exercise.okebet.app.presentation

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import exercise.okebet.app.R
import exercise.okebet.app.databinding.ItemNewBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    val titles = mutableListOf<String>()
    val descriptions = mutableListOf<String>()
    val images = mutableListOf<String>()
    val links = mutableListOf<String>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding by viewBinding(ItemNewBinding::bind)

        fun bind(position: Int) {
            binding.tvTitle.text = titles[position]
            binding.tvDescription.text = descriptions[position]
            Glide.with(binding.root)
                .load(images[position])
                .into(binding.ivNew)

            itemView.setOnClickListener {
                openLink(links[position])
            }
        }

        private fun openLink(link: String) {
            val uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val context = itemView.context
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_new, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = titles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}