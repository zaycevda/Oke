package exercise.okebet.app.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import exercise.okebet.app.R
import exercise.okebet.app.databinding.FragmentNewsBinding
import exercise.okebet.app.models.New
import exercise.okebet.app.utils.ThemeSharedPrefs
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class NewsFragment : Fragment(R.layout.fragment_news) {

    private val binding by viewBinding(FragmentNewsBinding::bind)

    private var adapter: NewsAdapter? = null

    private val themeSharedPrefs by lazy { ThemeSharedPrefs(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        changeTheme()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    private fun initAdapter() {
        adapter = NewsAdapter(
            onClick = { new ->
                findNavController().navigate(
                    R.id.action_newsFragment_to_newDialog,
                    bundleOf(
                        NewDialog.TITLE to new.title,
                        NewDialog.DESCRIPTION to new.description,
                        NewDialog.IMAGE_URL to new.imageUrl
                    )
                )
            }
        )
        jsonObjectsToAdapter()
        binding.rvNews.adapter = adapter
    }

    private fun jsonObjectsToAdapter() {
        try {
            jsonDataFromAsset()?.let {
                val jsonObject = JSONObject(it)
                val jsonArray = jsonObject.getJSONArray(NAME)
                for (i in 0 until jsonArray.length()) {
                    val newData = jsonArray.getJSONObject(i)

                    val new =
                        New(
                            title = newData.getString(TITLE),
                            description = newData.getString(DESCRIPTION),
                            imageUrl = newData.getString(IMAGE_URL)
                        )

                    adapter?.news?.add(new)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun jsonDataFromAsset() =
        try {
            val inputStream = requireActivity().assets.open(FILENAME)
            val sizeOfFile = inputStream.available()
            val bufferData = ByteArray(sizeOfFile)
            inputStream.read(bufferData)
            inputStream.close()
            String(bufferData, Charset.forName(FORMAT))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    private fun changeTheme() {
        binding.ivChangeTheme.setOnClickListener {
            if (themeSharedPrefs.isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                themeSharedPrefs.isDarkTheme = false
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                themeSharedPrefs.isDarkTheme = true
            }
        }
    }

    private companion object {
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val IMAGE_URL = "imageUrl"
        private const val FILENAME = "news.json"
        private const val FORMAT = "UTF-8"
        private const val NAME = "news"
    }
}