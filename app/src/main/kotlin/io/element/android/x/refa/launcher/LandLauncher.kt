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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.refahland.navigation.AppBottomBar
import com.drp.refahland.navigation.MainScreens
import io.element.android.x.refa.launcher.navigation.Navigation
import com.drp.refahland.ui.main.MainViewModel
import com.drp.refahland.ui.main.MainViewModelFactory
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.ElementXApplication
import kotlinx.coroutines.Dispatchers

class LandLauncher : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 26)
            enableEdgeToEdge()

        val depProvider = (application as ElementXApplication).dependencyProvider
        setContent {
            var navigationVisibility by remember {
                mutableStateOf(false)
            }
            val context = LocalContext.current

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
                val mainViewModelFactory = MainViewModelFactory(
                    dispatcher = Dispatchers.IO,
                    cardFacilitiesRepository = depProvider.cardFacilityRepository,
                    cardFacilitiesTransactionRepository = depProvider.cardFacilitiesTransactionRepository,
                    cardFacilitiesUserRepository = depProvider.userRepository
                )
                val viewModel: MainViewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]

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
                                    viewModel = viewModel
                                )
                        }
                    ) {
                        it
                        Navigation(
                            navController = navHostController,
                            mainViewModel = viewModel,
                            viewModelStoreOwner = this
                        ) { finish() }

                    }
            }
        }
    }
}

