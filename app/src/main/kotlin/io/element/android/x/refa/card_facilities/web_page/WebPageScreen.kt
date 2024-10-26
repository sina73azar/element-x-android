package com.drp.card_facilities.presentation.web_page

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.shared_ui.widget.CustomTopAppBar
import io.element.android.x.R

@Composable
fun WebPageScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeItemWebUrlType: HomeItemWebUrlType? = HomeItemWebUrlType.SAYADI
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }

    // Composable that hosts WebView
    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (homeItemWebUrlType != HomeItemWebUrlType.SAYADI) 64.dp else 0.dp)
                .background(Color.White),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)

                    webView = this // Reference to WebView
                }
            },
            update = {
                webView?.loadUrl(homeItemWebUrlType?.url ?: HomeItemWebUrlType.SAYADI.url)
                webView?.setOnKeyListener { _, _, _ ->
                    // Update back navigation availability
                    canGoBack = webView?.canGoBack() == true
                    false
                }
            }
        )
        CustomTopAppBar(
            headerTxt = homeItemWebUrlType?.title ?: stringResource(id = R.string.sayadi_cheque),
            onBackClick = {
                if (webView?.canGoBack() == true)
                    webView?.goBack()
                else
                    navController.popBackStack()
            }
        )
    }

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }
}
