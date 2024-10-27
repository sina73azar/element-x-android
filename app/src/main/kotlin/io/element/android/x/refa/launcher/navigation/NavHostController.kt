/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package com.drp.refahland.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.drp.card_facilities.presentation.app_source_card_handler.InternetPackageTypeScreenModel
import com.drp.card_facilities.presentation.balance.BalanceScreen
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillInquiryScreen
import com.drp.card_facilities.presentation.bill.inquiry.unified.BillInquiryScreen
import com.drp.card_facilities.presentation.card_to_card.compose.inquiry.CardToCardInquiryScreen
import com.drp.card_facilities.presentation.history.HistoryScreen
import com.drp.card_facilities.presentation.iban_convertor.IbanConvertorScreen
import com.drp.card_facilities.presentation.insurance.inquiry.InsuranceInquiryScreen
import com.drp.card_facilities.presentation.internet_package.compose.inquiry.InternetPackageInquiryScreen
import com.drp.card_facilities.presentation.internet_package.compose.package_type.InternetPackageTypeScreen
import com.drp.card_facilities.presentation.last_ten_statement.LastTenStatementScreen
import com.drp.card_facilities.presentation.last_ten_statement.result.LastTenStatementResultScreen
import com.drp.card_facilities.presentation.licence_negative_score.LicenceNegativeScoreInquiryScreen
import com.drp.card_facilities.presentation.motor_violation.MotorViolationScreen
import com.drp.card_facilities.presentation.qrcode.AnalyzerType
import com.drp.card_facilities.presentation.qrcode.CameraScreen
import com.drp.card_facilities.presentation.setting.SettingScreen
import com.drp.card_facilities.presentation.topup.compose.inquiry.TopUpInquiryScreen
import com.drp.card_facilities.presentation.tracking_post.TrackingPostScreen
import com.drp.card_facilities.presentation.transaction_history.TransactionHistoryScreen
import com.drp.card_facilities.presentation.vehicle_violation.VehicleViolationScreen
import com.drp.card_facilities.presentation.wallet_add.WalletAddScreen
import com.drp.card_facilities.presentation.wallet_minus.WalletMinusScreen
import com.drp.card_facilities.presentation.wallet_to_wallet.WalletToWalletScreen
import com.drp.card_facilities.presentation.web_page.HomeItemWebUrlType
import com.drp.card_facilities.presentation.web_page.WebPageScreen
import com.drp.data.enums.BillType
import com.drp.data.model.last_ten_statement.BankStatement
import com.drp.refahland.ui.main.CardScreen
import com.drp.refahland.ui.main.ChatBotScreen
import com.drp.refahland.ui.main.HomeScreen
import com.drp.refahland.ui.main.MainViewModel
import com.drp.refahland.ui.main.MessengerScreen
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.naviagtion.Screens
import kotlinx.serialization.json.Json
import ui.main.BillScreen

@Composable
fun Navigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    finishActivity: () -> Unit
) {
    var scannedWalletId by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val startDest =
        if (mainViewModel.getShahkarUserData().walletId != null) MainScreens.HomeScreen.route else Screens.ShahkarLoginScreen.route
    NavHost(navController = navController, startDestination = startDest) {

        /** Home Screens */

        composable(route = MainScreens.HomeScreen.route) {
            HomeScreen(navController = navController, viewModel = mainViewModel)
        }

        composable(route = MainScreens.BillScreen.route) {
            BillScreen(navController = navController, viewModel = mainViewModel)
        }

        composable(route = MainScreens.CardScreen.route) {
            CardScreen(navController = navController, viewModel = mainViewModel)
        }

        composable(route = MainScreens.MessengerScreen.route) {
            MessengerScreen(navController = navController, viewModel = mainViewModel)
        }

        composable(route = MainScreens.ChatBotScreen.route) {
            ChatBotScreen(navController = navController, viewModel = mainViewModel)
        }

        /** Wallet Screens */

        composable(route = Screens.WalletAddScreen.route) {
            WalletAddScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = Screens.WalletMinusScreen.route) {
            WalletMinusScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = Screens.WalletToWalletScreen.route) {
            WalletToWalletScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                scannedWalletId = scannedWalletId,
                resetScannedWalletIdToDefault = { scannedWalletId = "" }
            )
        }

        /** Setting Screens */
        composable(
            route = Screens.SettingScreen.route
        ) {
            SettingScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                finishActivity = finishActivity
            )
        }

        composable(
            route = Screens.AuthenticationScreen.route
        ) {
//            AuthenticationScreen(navController = navController, viewModel = hiltViewModel())
        }

        /** Home Screen Items - with Card */
        composable(
            route = Screens.CardToCardScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            CardToCardInquiryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                selectedCard = selectedCard
            )
        }
        composable(
            route = Screens.BalanceScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            BalanceScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                selectedCard = selectedCard
            )
        }
        composable(
            route = Screens.LastTenStatementScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            LastTenStatementScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                selectedCard = selectedCard
            )
        }

        /** Home Screen Items - with Card and Wallet */

        composable(
            route = Screens.BillInquiryScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            BillInquiryScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                selectedCard = selectedCard
            )
        }

        composable(
            route = Screens.SeparatedBillInquiryScreen.route + "/{billType}?billId={billId}",
            arguments = listOf(
                navArgument("billType") {
                    type = NavType.StringType
                    defaultValue = BillType.FIXEDLINE.name
                },
                navArgument("billId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navBackStackEntry ->
            val billType = navBackStackEntry.arguments?.getString("billType")?.let {
                BillType.valueOf(it)
            }
            val billId = navBackStackEntry.arguments?.getString("billId")
            billType?.let {
                SeparatedBillInquiryScreen(
                    viewModel = hiltViewModel(),
                    billType = it,
                    navController = navController,
                    billId = billId
                )
            }
        }
        composable(
            route = Screens.LastTenStatementResultScreen.route + "?statementList={statementList}",
            arguments = listOf(
                navArgument("statementList") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->

            val statementList = navBackStackEntry.arguments?.getString("statementList")?.let {
                Json.decodeFromString<ArrayList<BankStatement>>(it)
            }
            LastTenStatementResultScreen(
                navController = navController,
                inquiryResponse = statementList
            )
        }
        composable(
            route = Screens.TopUpScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            TopUpInquiryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                selectedCard = selectedCard
            )
        }

        composable(
            route = Screens.InternetPackageInquiryScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            InternetPackageInquiryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                selectedCard = selectedCard
            )
        }

        composable(
            route = Screens.InternetPackageTypeScreen.route + "?netPackTypeScreenModel={netPackTypeScreenModel}",
            arguments = listOf(
                navArgument("netPackTypeScreenModel") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->

            val netPackTypeScreenModel =
                navBackStackEntry.arguments?.getString("netPackTypeScreenModel")?.let {
                    Json.decodeFromString<InternetPackageTypeScreenModel>(it)
                }
            InternetPackageTypeScreen(
                navController = navController,
                internetPackageTypeScreenModel = netPackTypeScreenModel,
                viewModel = hiltViewModel()
            )
        }

        composable(
            route = Screens.TransactionHistoryScreen.route
        ) {
            TransactionHistoryScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable(
            route = Screens.HistoryScreen.route
        ) {
            HistoryScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable(
            route = Screens.InsuranceInquiryScreen.route + "?selectedCard={selectedCard}",
            arguments = listOf(
                navArgument("selectedCard") {
                    type = NavType.StringType
                    nullable = true
                })
        ) { navBackStackEntry ->
            val selectedCard = navBackStackEntry.arguments?.getString("selectedCard")?.let {
                Json.decodeFromString<CardShotItemInfo>(it)
            }
            InsuranceInquiryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                selectedCard = selectedCard
            )
        }

        composable(
            route = Screens.WebPageScreen.route + "/{homeItemWebUrl}",
            arguments = listOf(
                navArgument("homeItemWebUrl") {
                    type = NavType.StringType
                    defaultValue = HomeItemWebUrlType.SAYADI.name
                })
        ) { navBackStackEntry ->
            val webUrl = navBackStackEntry.arguments?.getString("homeItemWebUrl")?.let {
                HomeItemWebUrlType.valueOf(it)
            }
            WebPageScreen(navController = navController, homeItemWebUrlType = webUrl)
        }

        composable(route = Screens.CameraScreen.route) {
            CameraScreen(
                navController = navController,
                analyzerType = AnalyzerType.BARCODE,
                onScannedWalletIdChange = {
                    if (it != scannedWalletId) {
                        scannedWalletId = it
                        navController.popBackStack()
                    }
                })
        }

        composable(route = Screens.LicenceNegativeScoreInquiryScreen.route) {
            LicenceNegativeScoreInquiryScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(route = Screens.TrackingPostScreen.route) {
            TrackingPostScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(route = Screens.IbanConvertorScreen.route) {
            IbanConvertorScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(route = Screens.VehicleViolationScreen.route) {
            VehicleViolationScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(route = Screens.MotorViolationScreen.route) {
            MotorViolationScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

    }
}
