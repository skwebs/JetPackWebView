package com.satish.jetpackwebview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        var loaderDialogScreen = remember { mutableStateOf(false) }

        if (loaderDialogScreen.value){
            Dialog(
                onDismissRequest = {
                    loaderDialogScreen.value = false
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) { }
        }

        AndroidView(modifier = Modifier.fillMaxSize(), factory = {

            WebView(it).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.userAgentString = System.getProperty("http.agent")
                webViewClient = object : WebViewClient() {

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        canGoBack = view?.canGoBack() ?: false
                        loaderDialogScreen.value = true
                        return super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        canGoBack = view.canGoBack()
                        loaderDialogScreen.value = false
                    }
                }
                loadUrl(url)
                webView = this

            }
        }, update = {
            it.loadUrl(url)
        })

//        if can go back then go back else finish the activity

        BackHandler(enabled = true){
            if (canGoBack) {
                webView?.goBack()
            }
            else {
                finish()
            }

        }
    }

}

//how to exit from app in android using jetpack compose ?
