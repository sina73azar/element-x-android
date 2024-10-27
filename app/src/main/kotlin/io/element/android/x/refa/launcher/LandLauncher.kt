/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.refa.launcher

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.refahland.navigation.AppBottomBar
import com.drp.refahland.navigation.MainScreens
import com.drp.refahland.navigation.Navigation
import com.drp.refahland.ui.main.MainViewModel
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.SnackBarCompose
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandLauncher : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 26)
            enableEdgeToEdge()

        setContent {
            var navigationVisibility by remember {
                mutableStateOf(false)
            }
            val context = LocalContext.current
            /*        OpenShahkarLogin.openShahkarLogin(context,
                        object : OpenShahkarLogin.ExposeShahkarLoginState {
                            override fun onLoading() {
                            }

                            override fun onSuccess(shahkarUserData: ShahkarUserData) {
                                navigationVisibility = true
                                viewModel.getWalletBalance()
                            }

                            override fun onError(message: String) {
                            }

                            override fun onFail() {
                            }
                        })*/
            ApplicationTheme {
                val navHostController = rememberNavController()
                val snackBarHostState: SnackbarHostState = remember {
                    SnackbarHostState()
                }
                val curSnackType by remember {
                    mutableStateOf(SnackBarType.FAIL)
                }
                val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute by remember {
                    derivedStateOf {
                        currentBackStackEntry?.destination?.route ?: "Home"
                    }
                }
                val mainViewModel: MainViewModel = hiltViewModel()

                var showBottomBar by rememberSaveable {
                    mutableStateOf(false)
                }
                showBottomBar = when (currentRoute) {
                    MainScreens.HomeScreen.route,
                    MainScreens.BillScreen.route,
                    MainScreens.CardScreen.route,
                    MainScreens.ChatBotScreen.route,
                        /*MainScreens.MessengerScreen.route*/
                    -> {
                        true
                    }

                    else -> {
                        false
                    }
                }
                if (navigationVisibility)
                    Scaffold(
                        snackbarHost = {
                            SnackBarCompose(
                                snackbarHostState = snackBarHostState,
                                snackBarType = curSnackType
                            )
                        },
                        bottomBar = {
                            if (showBottomBar)
                                AppBottomBar(
                                    navController = navHostController,
                                    viewModel = mainViewModel
                                )
                        }
                    ) {
                        it
                        Navigation(
                            navController = navHostController,
                            mainViewModel = mainViewModel
                        ) { finish() }

                    }
            }
        }
    }
}

