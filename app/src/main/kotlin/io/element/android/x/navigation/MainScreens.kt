package io.element.android.x.navigation


sealed class MainScreens(val route: String) {
    data object HomeScreen : MainScreens(route = HomeScreen::class.java.name)
    data object BillScreen : MainScreens(route = BillScreen::class.java.name)
    data object CardScreen : MainScreens(route = CardScreen::class.java.name)
    data object MessengerScreen : MainScreens(route = MessengerScreen::class.java.name)

    data object ChatBotScreen : MainScreens(route = ChatBotScreen::class.java.name)

}
