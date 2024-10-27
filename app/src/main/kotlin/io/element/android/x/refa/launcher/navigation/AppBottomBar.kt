/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package com.drp.refahland.navigation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.drp.refahland.ui.main.BottomBarItemData
import com.drp.refahland.ui.main.MainScreenState
import com.drp.refahland.ui.main.MainViewModel
import io.element.android.x.R

@Composable
fun AppBottomBar(navController: NavHostController, viewModel: MainViewModel? = null) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: MainScreenState()
    val context = LocalContext.current
    val bottomBarItems = listOf(
        BottomBarItemData(
            id = 1,
            title = stringResource(id = R.string.bills_st),
            selectedIcon = R.drawable.ic_bill_list_filled,
            unselectedIcon = R.drawable.ic_bill_list_outlined,
            onClick = {
                navController.navigate(route = MainScreens.BillScreen.route) {
                    popUpTo(route = MainScreens.HomeScreen.route) {
                        inclusive = false
                    }
                }
            }
        ),
        BottomBarItemData(
            id = 2,
            title = stringResource(id = R.string.cards_st),
            selectedIcon = R.drawable.ic_card_list_filled,
            unselectedIcon = R.drawable.ic_card_list_outlined,
            onClick = {
                navController.navigate(route = MainScreens.CardScreen.route) {
                    popUpTo(route = MainScreens.HomeScreen.route) {
                        inclusive = false
                    }
                }
            }
        ),
        BottomBarItemData(
            id = 3,
            title = "",
            selectedIcon = R.drawable.ic_home_filled,
            unselectedIcon = R.drawable.ic_home_outlined,
            onClick = {
                navController.popBackStack(
                    route = MainScreens.HomeScreen.route,
                    inclusive = false
                )
            }
        ),
        BottomBarItemData(
            id = 4,
            title = stringResource(id = R.string.messenger_st),
            selectedIcon = R.drawable.ic_messenger_filled,
            unselectedIcon = R.drawable.ic_messenger_outlined,
            onClick = {
                val intent = Intent(context, io.element.android.x.MainActivity::class.java)
                context.startActivity(intent)
            }
        ),
        BottomBarItemData(
            id = 5,
            title = stringResource(id = R.string.chatbot_st),
            selectedIcon = R.drawable.ic_chatbot_filled,
            unselectedIcon = R.drawable.ic_chatbot_outlined,
            onClick = {
                navController.navigate(route = MainScreens.ChatBotScreen.route) {
                    popUpTo(route = MainScreens.HomeScreen.route) {
                        inclusive = false
                    }
                }
            }
        )
    )
    NavigationBar(modifier = Modifier.height(84.dp), containerColor = Color.Transparent) {
        Box(modifier = Modifier.fillMaxSize()) {
            /** background color */
            Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.8f)
                )
                Box(
                    modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .background(
                                    color = MaterialTheme.colorScheme.tertiary.copy(
                                            alpha = 0.1f
                                    )
                            )
                )
            }

            /** items row */
            Row(modifier = Modifier.fillMaxSize()) {
                bottomBarItems.forEach { bottomBarItem ->
                    if (bottomBarItem.id != 3)
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                            selected = uiState.selectedBottomBarItemId == bottomBarItem.id,
                            onClick = {
                                viewModel?.setSelectedBottomBarId(bottomBarItem.id)
                                bottomBarItem.onClick()
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = if (uiState.selectedBottomBarItemId == bottomBarItem.id) bottomBarItem.selectedIcon else bottomBarItem.unselectedIcon),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            label = {
                                Text(
                                    text = bottomBarItem.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            })
                    else
                        Card(
                            modifier = Modifier
                                    .padding(
                                            horizontal = dimensionResource(
                                                    id = R.dimen.medium_padding
                                            )
                                    )
                                    .size(64.dp),
                            shape = RoundedCornerShape(percent = 50),
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = dimensionResource(
                                    id = R.dimen.small_elevation
                                )
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (bottomBarItem.id == uiState.selectedBottomBarItemId) Color(
                                    0xFFcecece
                                ) else MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            viewModel?.setSelectedBottomBarId(bottomBarItem.id)
                                            bottomBarItem.onClick()
                                        },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = if (uiState.selectedBottomBarItemId == bottomBarItem.id) bottomBarItem.selectedIcon else bottomBarItem.unselectedIcon),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                }
            }
        }
    }
}
