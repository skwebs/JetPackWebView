package com.satish.jetpackwebview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        var canGoBack by remember { mutableStateOf(false) }
        val loaderDialogScreen = remember { mutableStateOf(false) }

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
                settings.userAgentString = "$ua /|$appVersion"


                webViewClient = object : WebViewClient() {

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        canGoBack = view?.canGoBack() ?: false
                        loaderDialogScreen.value = true
//                        return super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        canGoBack = view.canGoBack()
                        loaderDialogScreen.value = false
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
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
            Log.d("TAG", "canGoBack: $canGoBack")
            if (canGoBack) {
                webView?.goBack()
            } else {
                // Show a confirmation dialog to exit
                Toast.makeText(this, "Are you sure you want to exit?", Toast.LENGTH_SHORT).show()
                finish()
                //                AlertDialog(
//                    onDismissRequest = {},
//                    title = { Text(text = "Exit?") },
//                    text = { Text("Are you sure you want to exit?") },
//                    confirmButton = {
//                        Button(onClick = { finish() }) {
//                            Text("Yes")
//                        }
//                    },
//                    dismissButton = {
//                        Button(onClick = {}) {
//                            Text("No")
//                        }
//                    }
//                )
            }
        }
    }
}
