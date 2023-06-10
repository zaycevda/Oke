package exercise.okebet.app.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import exercise.okebet.app.R
import exercise.okebet.app.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private val binding by viewBinding(FragmentWebViewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(savedInstanceState: Bundle?) {
        val url = arguments?.getString(URL_KEY)!!

        savedInstanceState?.let {
            binding.webView.restoreState(it)
        } ?: binding.webView.loadUrl(url)

        binding.webView.webViewClient = WebViewClient()

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
        const val URL_KEY = "URL_KEY"
    }
}