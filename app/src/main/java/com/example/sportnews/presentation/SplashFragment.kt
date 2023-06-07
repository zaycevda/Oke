package com.example.sportnews.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sportnews.R
import com.example.sportnews.utils.SharedPrefs
import com.google.firebase.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import java.util.Locale
import kotlin.Exception

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val sharedPrefs by lazy { SharedPrefs(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigate()
    }

    private fun navigate() {
        if (sharedPrefs.url == null) {
            val url =
                try {
                    Firebase.remoteConfig.getString(URL)
                } catch (e: Exception) {
                    findNavController().navigate(R.id.action_splashFragment_to_errorFragment)
                    Log.e(TAG, "navigate: ${e.message}")
                    return
                }
            return if (url.isEmpty() || checkIsEmu())
                findNavController().navigate(R.id.action_splashFragment_to_newsFragment)
            else {
                sharedPrefs.url = url
                findNavController().navigate(
                    R.id.action_splashFragment_to_webViewFragment,
                    bundleOf(WebViewFragment.URL_KEY to sharedPrefs.url)
                )
            }
        }

        if (isInternetAvailable(requireContext()))
            findNavController().navigate(
                R.id.action_splashFragment_to_webViewFragment,
                bundleOf(WebViewFragment.URL_KEY to sharedPrefs.url)
            )
        else
            findNavController().navigate(R.id.action_splashFragment_to_errorFragment)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false

        val board = Build.BOARD
        val bootloader = Build.BOOTLOADER
        val brand = Build.BRAND
        val buildProduct = Build.PRODUCT
        val device = Build.DEVICE
        val fingerprint = Build.FINGERPRINT
        val hardware = Build.HARDWARE
        val manufacturer = Build.MANUFACTURER
        val phoneModel = Build.MODEL

        var result = (board.lowercase(Locale.getDefault()).contains(NOX)
                || bootloader.lowercase(Locale.getDefault()).contains(NOX)
                || brand.contains(GOOGLE)
                || buildProduct == GOOGLE_SDK
                || buildProduct == SDK
                || buildProduct == SDK_X86
                || buildProduct == VBOX86P
                || buildProduct.lowercase(Locale.getDefault()).contains(NOX)
                || fingerprint.startsWith(GENERIC)
                || hardware == GOLDFISH
                || hardware == VBOX86P
                || hardware.lowercase(Locale.getDefault()).contains(NOX)
                || manufacturer.contains(GENYMOTION)
                || phoneModel.contains(ANDROID_SDK_BUILT_FOR_X86)
                || phoneModel.contains(EMULATOR)
                || phoneModel.contains(GOOGLE_SDK)
                || phoneModel.lowercase(Locale.getDefault()).contains(DROID4X))

        if (result) return true

        result = result or (brand.startsWith(GENERIC) && device.startsWith(GENERIC))

        if (result) return true

        return false
    }

    private companion object {
        private const val ANDROID_SDK_BUILT_FOR_X86 = "Android SDK built for x86"
        private const val DROID4X = "droid4x"
        private const val EMULATOR = "Emulator"
        private const val GENYMOTION = "Genymotion"
        private const val GENERIC = "generic"
        private const val GOLDFISH = "goldfish"
        private const val GOOGLE = "google"
        private const val GOOGLE_SDK = "google_sdk"
        private const val NOX = "nox"
        private const val SDK = "sdk"
        private const val SDK_X86 = "sdk_x86"
        private const val TAG = "SplashFragmentLog"
        private const val URL = "url"
        private const val VBOX86P = "vbox86p"
    }
}