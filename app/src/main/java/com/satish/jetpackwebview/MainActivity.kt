package com.satish.jetpackwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.satish.jetpackwebview.ui.theme.JetPackWebViewTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            JetPackWebViewTheme {
                WebViewScreen()
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    private fun WebViewScreen() {

        val url by remember { mutableStateOf("https://amh.anshumemorial.in") }
        var webView by remember { mutableStateOf<WebView?>(null) }
//        var canGoBack by remember { mutableStateOf(false) }
        val loaderDialogScreen = remember { mutableStateOf(false) }

        var showDialog by remember { mutableStateOf(false) }
        var confirmMessage by remember { mutableStateOf("") }
        var jsResult by remember { mutableStateOf<JsResult?>(null) } // To hold JsResult reference for confirm/cancel

        // For exit confirmation dialog
        var showExitDialog by remember { mutableStateOf(false) }


        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = false },
                title = { Text("Confirm") },
                text = { Text(confirmMessage) },
                confirmButton = {
                    Button(onClick = {
                        // Handle "OK" pressed
                        jsResult?.confirm() // Confirm the JavaScript dialog
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        // Handle "Cancel" pressed
                        jsResult?.cancel() // Cancel the JavaScript dialog
                        showDialog = false
                    }) {
                        Text("Cancel")
                    }
                })
        }


        // Show the exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(onDismissRequest = { showExitDialog = false },
                title = { Text("Exit") },
                text = { Text("Are you sure you want to exit?") },
                confirmButton = {
                    Button(onClick = {
                        showExitDialog = false
                        // Exit the app or finish activity
                        (webView?.context as? Activity)?.finish() // Exit app
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showExitDialog = false // Just dismiss the dialog
                    }) {
                        Text("No")
                    }
                })
        }


        if (loaderDialogScreen.value) {
            Dialog(
                onDismissRequest = { loaderDialogScreen.value = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        AndroidView(modifier = Modifier.fillMaxSize(), factory = {

            WebView(it).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                val ua = System.getProperty("http.agent")
                val appVersion = context.packageName
                settings.userAgentString = "$ua $appVersion Android-WebView"

                webChromeClient = object : WebChromeClient() {
                    override fun onJsConfirm(
                        view: WebView?, url: String?, message: String?, result: JsResult?
                    ): Boolean {
                        confirmMessage = message ?: ""
                        jsResult = result // Store JsResult to handle confirm/cancel
                        showDialog = true
                        return true // Return true to indicate that we've handled the confirm dialog

                    }
                }
                webViewClient = object : WebViewClient() {

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                        canGoBack = view?.canGoBack() ?: false
                        loaderDialogScreen.value = true
//                        return super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
//                        canGoBack = view.canGoBack()
                        loaderDialogScreen.value = false
                    }

                    override fun onReceivedError(
                        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        loaderDialogScreen.value = false
                        // Load local HTML file in case of error
                        view?.loadUrl("file:///android_asset/404.html")
                    }
                }
                loadUrl(url)
                webView = this

            }
        }, update = {
            it.loadUrl(url)
        })


        BackHandler(enabled = true) {
            webView?.let {
                if (it.canGoBack()) {
                    it.goBack() // Navigate back within the WebView
                } else {
                    // Show exit confirmation dialog
                    showExitDialog = true
                }
            }
        }

    }
}
