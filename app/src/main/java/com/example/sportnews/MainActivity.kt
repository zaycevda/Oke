package com.example.sportnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sportnews.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private var adapter: NewsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAdapter()

        jsonObjectsToAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
    }

    private fun initAdapter() {
        adapter = NewsAdapter()
        binding.rvNews.adapter = adapter
    }

    private fun jsonObjectsToAdapter() {
        try {
            jsonDataFromAsset()?.let {
                val jsonObject = JSONObject(it)
                val jsonArray = jsonObject.getJSONArray(NAME)
                for (i in 0 until jsonArray.length()) {
                    val newData = jsonArray.getJSONObject(i)
                    adapter?.titles?.add(newData.getString("title"))
                    adapter?.descriptions?.add(newData.getString("description"))
                    adapter?.images?.add(newData.getString("urlToImage"))
                    adapter?.links?.add(newData.getString("url"))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun jsonDataFromAsset() =
        try {
            val inputStream = assets.open(FILENAME)
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
        private const val FILENAME = "news.json"
        private const val NAME = "news"
        private const val FORMAT = "UTF-8"
    }
}