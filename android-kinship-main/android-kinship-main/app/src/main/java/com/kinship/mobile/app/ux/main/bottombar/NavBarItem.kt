package com.kinship.mobile.app.ux.main.bottombar

import androidx.annotation.StringRes

import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.ux.main.events.EventsRoute

import com.kinship.mobile.app.ux.main.home.HomeRoute
import com.kinship.mobile.app.ux.main.message.MessageRoute
import com.kinship.mobile.app.ux.container.setting.SettingRoute


enum class NavBarItem(
    val unselectedIconDrawableId: Int,
    val selectedIconDrawableId: Int,
    val route: NavRoute,
    @StringRes val textResId: Int? = null,
) {
    HOME(unselectedIconDrawableId = R.drawable.ic_unselected_home, selectedIconDrawableId = R.drawable.ic_selected_home, route = HomeRoute.createRoute(), textResId = R.string.home),
    EVENT(unselectedIconDrawableId = R.drawable.ic_unselected_event, selectedIconDrawableId = R.drawable.ic_selected_event, route = EventsRoute.createRoute(), textResId = R.string.events),
    MESSAGE(unselectedIconDrawableId = R.drawable.ic_unselected_chat, selectedIconDrawableId = R.drawable.ic_selected_chat, route = MessageRoute.createRoute(), textResId = R.string.messages),
    SETTING(unselectedIconDrawableId = R.drawable.ic_unselected_setting, selectedIconDrawableId = R.drawable.ic_selected_setting, route = SettingRoute.createRoute(), textResId = R.string.settings);

    companion object {
        fun getNavBarItemRouteMap(): Map<NavBarItem, NavRoute> {
            return entries.associateWith { item -> item.route }

        }
    }
}

