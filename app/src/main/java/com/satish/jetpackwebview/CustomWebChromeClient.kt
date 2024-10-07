package com.satish.jetpackwebview

import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.runtime.MutableState

class CustomWebChromeClient(
    private val loaderDialogScreen: MutableState<Boolean>,
    private val showDialog: MutableState<Boolean>,
    private val confirmMessage: MutableState<String>,
    private val jsResult: MutableState<JsResult?>
) : WebChromeClient() {

    override fun onJsConfirm(
        view: WebView?, url: String?, message: String?, result: JsResult?
    ): Boolean {
        confirmMessage.value = message ?: ""
        jsResult.value = result // Store JsResult to handle confirm/cancel
        showDialog.value = true
        return true // We've handled the confirm dialog
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        if (newProgress == 100) {
            loaderDialogScreen.value = false // Hide loader when done
        } else {
            loaderDialogScreen.value = true // Show loader while loading
        }
    }
}
