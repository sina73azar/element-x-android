package com.drp.shared_ui.navigation.fragment

sealed class NavigationFragment(val path: String) {
    data object AccountFragment :
        NavigationFragment("com.drp.refah.mobile_bank.ui.main.account.AccountFragment")

    data object ActionFragment :
        NavigationFragment("com.drp.refah.mobile_bank.ui.main.action.ActionFragment")
    data object OtherFragment :
        NavigationFragment("com.drp.refah.mobile_bank.ui.main.other.OtherFragment")

    data object CardListFragment :
        NavigationFragment("com.drp.refah.mobile_bank.ui.main.card.CardFragment")

    data object HomeFragment :
        NavigationFragment("com.drp.refah.mobile_bank.ui.main.home.HomeFragment")
}