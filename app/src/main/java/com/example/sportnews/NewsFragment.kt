package com.example.sportnews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sportnews.databinding.FragmentNewsBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class NewsFragment : Fragment(R.layout.fragment_news) {

    private val binding by viewBinding(FragmentNewsBinding::bind)

    private var adapter: NewsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    private fun initAdapter() {
        adapter = NewsAdapter()
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
                    adapter?.titles?.add(newData.getString(TITLE))
                    adapter?.descriptions?.add(newData.getString(DESCRIPTION))
                    adapter?.images?.add(newData.getString(URL_TO_IMAGE))
                    adapter?.links?.add(newData.getString(URL))
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

    private companion object {
        private const val DESCRIPTION = "description"
        private const val FILENAME = "news.json"
        private const val FORMAT = "UTF-8"
        private const val NAME = "news"
        private const val TITLE = "title"
        private const val URL = "url"
        private const val URL_TO_IMAGE = "urlToImage"
    }
}