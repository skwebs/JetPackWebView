package com.satish.jetpackwebview

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.MutableState

class CustomWebViewClient(
    private val loaderDialogScreen: MutableState<Boolean>,
    private val url: String
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val webUrl = request?.url.toString()
        return !webUrl.startsWith(url) // Block URLs that are not the target URL
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        loaderDialogScreen.value = true
    }

    override fun onPageFinished(view: WebView, url: String) {
        loaderDialogScreen.value = false
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        loaderDialogScreen.value = false
        view?.loadUrl("file:///android_asset/error.html") // Custom error page
    }
}
