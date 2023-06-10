package exercise.okebet.app.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import exercise.okebet.app.R
import exercise.okebet.app.databinding.FragmentWebViewBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private val binding by viewBinding(FragmentWebViewBinding::bind)

    private var cameraPhotoPath: String? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFileChooserListener()

        initWebView(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    private fun initFileChooserListener() {
        fileChooserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data = result.data
                    val results =
                        data?.let { intent ->
                            intent.dataString?.let { arrayOf(Uri.parse(it)) }
                        } ?: cameraPhotoPath?.let { arrayOf(Uri.parse(it)) }

                    filePathCallback?.onReceiveValue(results)
                    filePathCallback = null
                }
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(savedInstanceState: Bundle?) {
        val url = arguments?.getString(URL_KEY)!!

        savedInstanceState?.let {
            binding.webView.restoreState(it)
        } ?: binding.webView.loadUrl(url)

        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = ChromeClient()

        with(binding.webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
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

    inner class ChromeClient : WebChromeClient() {
        @SuppressLint("QueryPermissionsNeeded")
        override fun onShowFileChooser(
            view: WebView,
            filePath: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            if (filePathCallback != null) filePathCallback!!.onReceiveValue(null)

            filePathCallback = filePath

            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent!!.resolveActivity(requireActivity().packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent.putExtra(PHOTO_PATH, cameraPhotoPath)
                } catch (ex: IOException) {
                    Log.e(TAG, ERROR_MSG, ex)
                }

                if (photoFile != null) {
                    cameraPhotoPath = CAMERA_PHOTO_PATH_PREFIX + photoFile.absolutePath
                    takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile)
                    )
                } else takePictureIntent = null
            }

            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = CONTENT_SELECTION_INTENT_TYPE

            val intentArray: Array<Intent?> =
                takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)

            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, IMAGE_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

            fileChooserLauncher.launch(chooserIntent)
            return true
        }

        @SuppressLint("SimpleDateFormat")
        @Throws(IOException::class)
        private fun createImageFile(): File {

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = IMAGE_FILE_NAME_PREFIX + timeStamp + UNDERSCORE
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            return File.createTempFile(
                imageFileName,
                SUFFIX,
                storageDir
            )
        }
    }

    companion object {
        private const val CAMERA_PHOTO_PATH_PREFIX = "file:"
        private const val CONTENT_SELECTION_INTENT_TYPE = "image/*"
        private const val ERROR_MSG = "Unable to create Image File"
        private const val IMAGE_CHOOSER = "Image Chooser"
        private const val IMAGE_FILE_NAME_PREFIX = "PhotoPath"
        private const val PHOTO_PATH = "PhotoPath"
        private const val SUFFIX = ".jpg"
        private const val TAG = "WebViewFragmentTag"
        private const val UNDERSCORE = "_"
        const val URL_KEY = "URL_KEY"
    }
}