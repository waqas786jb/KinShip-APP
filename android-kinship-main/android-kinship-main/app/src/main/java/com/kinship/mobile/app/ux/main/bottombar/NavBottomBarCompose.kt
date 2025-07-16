package com.kinship.mobile.app.ux.main.bottombar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.ux.main.home.HomeRoute
import com.kinship.mobile.app.ux.main.home.link.LinksRoute
import com.kinship.mobile.app.ux.main.home.member.MemberRoute
import com.kinship.mobile.app.ux.main.home.profile.ProfileRoute

@Composable
fun AppNavigationBar(
    currDestination: NavDestination?,
    onNavItemClicked: (NavBarItem) -> Unit,

    ) {
    Column {
        NavigationBar(
            containerColor = White,
            modifier = Modifier.shadow(elevation = 5.dp)
        ) {
            NavBarItem.entries.forEach { item ->
                val isSelected = isSelected(currDestination, item)
                AppBottomNavigationItem(
                    navBarItem = item,
                    unselectedIconDrawableId = item.unselectedIconDrawableId,
                    selectedIconDrawableId = item.selectedIconDrawableId,
                    selectedBarItem = isSelected,
                    textResId = item.textResId
                ) { onNavItemClicked(it) }
            }
        }
    }
}

private fun isSelected(currDestination: NavDestination?, navBarItem: NavBarItem): Boolean {
    return if (navBarItem == NavBarItem.HOME) {
        currDestination?.hierarchy?.any {
            it.route in setOf(
                HomeRoute.routeDefinition.value,
                MemberRoute.routeDefinition.value,
                ProfileRoute.routeDefinition.value,
                LinksRoute.routeDefinition.value
            )
        } == true
    } else {
        currDestination?.hierarchy?.any { it.route == navBarItem.route.value } == true
    }
}