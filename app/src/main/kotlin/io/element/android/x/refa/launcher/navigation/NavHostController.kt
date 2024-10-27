/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.refa.launcher.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.drp.card_facilities.presentation.app_source_card_handler.InternetPackageTypeScreenModel
import com.drp.card_facilities.presentation.balance.BalanceScreen
import com.drp.card_facilities.presentation.balance.BalanceScreenViewModel
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillInquiryScreen
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillViewModel
import com.drp.card_facilities.presentation.bill.inquiry.unified.BillInquiryScreen
import com.drp.card_facilities.presentation.bill.inquiry.unified.BillViewModel
import com.drp.card_facilities.presentation.card_to_card.compose.CardToCardViewModel
import com.drp.card_facilities.presentation.card_to_card.compose.inquiry.CardToCardInquiryScreen
import com.drp.card_facilities.presentation.history.HistoryScreen
import com.drp.card_facilities.presentation.history.HistoryViewModel
import com.drp.card_facilities.presentation.iban_convertor.IbanConvertorScreen
import com.drp.card_facilities.presentation.iban_convertor.IbanConvertorViewModel
import com.drp.card_facilities.presentation.insurance.InsuranceViewModel
import com.drp.card_facilities.presentation.insurance.inquiry.InsuranceInquiryScreen
import com.drp.card_facilities.presentation.internet_package.compose.inquiry.InternetPackageInquiryScreen
import com.drp.card_facilities.presentation.internet_package.compose.inquiry.InternetPackageInquiryViewModel
import com.drp.card_facilities.presentation.internet_package.compose.package_type.InternetPackageTypeScreen
import com.drp.card_facilities.presentation.internet_package.compose.package_type.InternetPackageTypeViewModel
import com.drp.card_facilities.presentation.last_ten_statement.LastTenStatementScreen
import com.drp.card_facilities.presentation.last_ten_statement.LastTenStatementScreenViewModel
import com.drp.card_facilities.presentation.last_ten_statement.result.LastTenStatementResultScreen
import com.drp.card_facilities.presentation.licence_negative_score.LicenceNegativeScoreInquiryScreen
import com.drp.card_facilities.presentation.licence_negative_score.LicenceNegativeScoreViewModel
import com.drp.card_facilities.presentation.motor_violation.MotorViolationScreen
import com.drp.card_facilities.presentation.motor_violation.MotorViolationViewModel
import com.drp.card_facilities.presentation.qrcode.AnalyzerType
import com.drp.card_facilities.presentation.qrcode.CameraScreen
import com.drp.card_facilities.presentation.topup.compose.TopUpViewModel
import com.drp.card_facilities.presentation.topup.compose.inquiry.TopUpInquiryScreen
import com.drp.card_facilities.presentation.tracking_post.TrackingPostScreen
import com.drp.card_facilities.presentation.tracking_post.TrackingPostViewModel
import com.drp.card_facilities.presentation.vehicle_violation.VehicleViolationScreen
import com.drp.card_facilities.presentation.vehicle_violation.VehicleViolationViewModel
import com.drp.card_facilities.presentation.wallet_add.WalletAddScreen
import com.drp.card_facilities.presentation.wallet_minus.WalletMinusScreen
import com.drp.card_facilities.presentation.wallet_minus.WalletMinusViewModel
import com.drp.card_facilities.presentation.wallet_to_wallet.WalletToWalletScreen
import com.drp.card_facilities.presentation.wallet_to_wallet.WalletToWalletViewModel
import com.drp.card_facilities.presentation.web_page.HomeItemWebUrlType
import com.drp.card_facilities.presentation.web_page.WebPageScreen
import com.drp.data.enums.BillType
import com.drp.data.model.last_ten_statement.BankStatement
import com.drp.refahland.navigation.MainScreens
import com.drp.refahland.ui.main.CardScreen
import com.drp.refahland.ui.main.ChatBotScreen
import com.drp.refahland.ui.main.HomeScreen
import com.drp.refahland.ui.main.MainViewModel
import com.drp.refahland.ui.main.MessengerScreen
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.naviagtion.Screens
import io.element.android.x.refa.card_facilities.wallet_add.WalletAddViewModel
import io.element.android.x.refa.di.DependencyProvider
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
    val depProvider = DependencyProvider(context.applicationContext)

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
            val walletAddViewModel = remember {
                WalletAddViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                )
            }
            WalletAddScreen(navController = navController, viewModel = walletAddViewModel)
        }

        composable(route = Screens.WalletMinusScreen.route) {
            val walletMinusViewModel = remember {
                WalletMinusViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                )
            }
            WalletMinusScreen(navController = navController, viewModel = walletMinusViewModel)
        }

        composable(route = Screens.WalletToWalletScreen.route) {
            val walletToWalletViewModel = remember {
                WalletToWalletViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilityRepository
                )
            }
            WalletToWalletScreen(
                navController = navController,
                viewModel = walletToWalletViewModel,
                scannedWalletId = scannedWalletId,
                resetScannedWalletIdToDefault = { scannedWalletId = "" }
            )
        }

        /** Setting Screens */
        /*composable(
            route = Screens.SettingScreen.route
        ) {
            SettingScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                finishActivity = finishActivity
            )
        }*/

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
            val cardToCardViewModel = remember {
                CardToCardViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilitiesTransactionRepository
                )
            }
            CardToCardInquiryScreen(
                navController = navController,
                viewModel = cardToCardViewModel,
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
            val balanceViewModel = remember {
                BalanceScreenViewModel(depProvider.dispatcher, depProvider.cardFacilitiesUserRepository, depProvider.cardFacilityRepository)
            }
            BalanceScreen(
                navController = navController,
                viewModel = balanceViewModel,
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
            val lastTenStatementViewModel = remember {
                LastTenStatementScreenViewModel(depProvider.dispatcher, depProvider.cardFacilitiesUserRepository, depProvider.cardFacilityRepository)
            }
            LastTenStatementScreen(
                navController = navController,
                viewModel = lastTenStatementViewModel,
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
            val billInquiryViewModel = remember {
                BillViewModel(
                    depProvider.cardFacilitiesBillRepository,
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilitiesTransactionRepository
                )
            }
            BillInquiryScreen(
                viewModel = billInquiryViewModel,
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
            val viewModel = remember {
                SeparatedBillViewModel(
                    depProvider.cardFacilitiesBillRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesTransactionRepository,
                    depProvider.dispatcher
                )
            }
            billType?.let {
                SeparatedBillInquiryScreen(
                    viewModel = viewModel,
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
            val viewModel = remember {
                TopUpViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilitiesTransactionRepository
                )
            }
            TopUpInquiryScreen(
                navController = navController,
                viewModel = viewModel,
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
            val viewModel = remember {
                InternetPackageInquiryViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilitiesTransactionRepository
                )
            }
            InternetPackageInquiryScreen(
                navController = navController,
                viewModel = viewModel,
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
            val viewModel = remember {
                InternetPackageTypeViewModel(
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilitiesTransactionRepository,
                    depProvider.dispatcher
                )
            }
            InternetPackageTypeScreen(
                navController = navController,
                internetPackageTypeScreenModel = netPackTypeScreenModel,
                viewModel = viewModel
            )
        }

        /*composable(
            route = Screens.TransactionHistoryScreen.route
        ) {
            TransactionHistoryScreen(navController = navController, viewModel = hiltViewModel())
        }*/

        composable(
            route = Screens.HistoryScreen.route
        ) {
            val viewModel = remember {
                HistoryViewModel(
                    depProvider.cardFacilitiesTransactionRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilityRepository,
                    depProvider.dispatcher
                )
            }
            HistoryScreen(navController = navController, viewModel = viewModel)
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
            val viewModel = remember {
                InsuranceViewModel(
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilitiesBillRepository,
                    depProvider.cardFacilitiesTransactionRepository,
                    depProvider.dispatcher
                )
            }
            InsuranceInquiryScreen(
                navController = navController,
                viewModel = viewModel,
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
            val viewModel = remember {
                LicenceNegativeScoreViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilityRepository
                )
            }
            LicenceNegativeScoreInquiryScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screens.TrackingPostScreen.route) {
            val viewModel = remember {
                TrackingPostViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository
                )
            }
            TrackingPostScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screens.IbanConvertorScreen.route) {
            val viewModel = remember {
                IbanConvertorViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilitiesUserRepository,
                    depProvider.cardFacilityRepository
                )
            }
            IbanConvertorScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screens.VehicleViolationScreen.route) {
            val viewModel = remember {
                VehicleViolationViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository
                )
            }
            VehicleViolationScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screens.MotorViolationScreen.route) {
            val viewModel = remember {
                MotorViolationViewModel(
                    depProvider.dispatcher,
                    depProvider.cardFacilityRepository,
                    depProvider.cardFacilitiesUserRepository
                )
            }
            MotorViolationScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

    }
}
