package io.element.android.x.refa.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class FacilityServiceItem(
    var id: Int,
    var title: String,
    @ColorRes var backgroundColor: Color = Color.Transparent,
    @DrawableRes var iconResource: Int ,
    var authenticationEnabled: Boolean = false,
    var route: String = "",
    var pageType: PageType?= PageType.COMPOSE
)
enum class PageType{
    FRAGMENT,
    SHEET,
    ACTIVITY,
    COMPOSE
}
