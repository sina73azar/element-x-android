/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package com.drp.refahland.ui.main

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.WebViewScreen
import io.element.android.x.R

@Composable
fun ChatBotScreen(
    navController: NavController,
    viewModel: MainViewModel? = null
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        CustomTopAppBar(
            headerTxt = stringResource(id = R.string.chatbot_st),
            backBtnVisible = false
        )
        WebViewScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            url = "https://chatbot.daneshrefah.ir/UI/RefaLand",
            webView = webView,
            changeGoBack = { canGoBack = it },
            changeWebView = { webView = it }
        )

    }
    BackHandler(/*enabled = canGoBack*/) {
        /*if (webView?.canGoBack() == true)
            webView?.goBack()
        else {*/
        viewModel?.setSelectedBottomBarId(3)
        navController.popBackStack(
            route = Screens.HomeScreen.route,
            inclusive = false
        )
//        }
    }
}

@Preview
@Composable
private fun ChatBotScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ChatBotScreen(navController = rememberNavController())
        }
    }
}
