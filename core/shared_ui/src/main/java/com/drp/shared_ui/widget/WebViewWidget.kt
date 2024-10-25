package com.drp.shared_ui.widget

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    url: String,
    webView: WebView?,
    changeGoBack: (Boolean) -> Unit,
    changeWebView: (WebView) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                settings.domStorageEnabled=true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)

                changeWebView(this)
            }
        },
        update = {
            webView?.loadUrl(url)
            webView?.setOnKeyListener { _, _, _ ->
                // Update back navigation availability
                changeGoBack(webView.canGoBack())
                false
            }
        }
    )
}