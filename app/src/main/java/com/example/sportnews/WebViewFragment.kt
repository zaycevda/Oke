package com.example.sportnews

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sportnews.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private val binding by viewBinding(FragmentWebViewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val url = arguments?.getString(URL)

        binding.webView.webViewClient = WebViewClient()

        url?.let { binding.webView.loadUrl(it) }

        with(binding.webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            databaseEnabled = true
            setSupportZoom(false)
            allowFileAccess = true
            allowContentAccess = true
        }

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        onBackPressedCallback()
    }

    private fun onBackPressedCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) binding.webView.goBack()
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                requireActivity(),
                onBackPressedCallback
            )
    }

    companion object {
        const val URL = "URL"
    }
}