package exercise.okebet.app.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import exercise.okebet.app.R
import exercise.okebet.app.databinding.ItemNewShortBinding
import exercise.okebet.app.models.New

private typealias OnClick = (new: New) -> Unit

class NewsAdapter(
    private val onClick: OnClick
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    val news = mutableListOf<New>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding by viewBinding(ItemNewShortBinding::bind)

        fun bind(new: New) {
            binding.tvTitle.text = new.title
            Glide.with(binding.root)
                .load(new.imageUrl)
                .into(binding.ivNew)

            itemView.setOnClickListener { onClick(new) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_new_short, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = news.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(news[position])
    }
}