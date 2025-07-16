package com.kinship.mobile.app.ux.main.bottombar
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kinship.mobile.app.ui.theme.BlackLite
import com.kinship.mobile.app.ui.theme.AppThemeColor

val LocalSelectedItem = compositionLocalOf<Enum<*>> { error("No selected item found!") }
@Composable
fun <T : Enum<T>> RowScope.AppBottomNavigationItem(
    navBarItem: T,
    unselectedIconDrawableId: Int,
    selectedIconDrawableId: Int,
    selectedBarItem: Boolean,
    @StringRes textResId: Int? = null,
    text: String? = null,
    onNavItemClicked: (T) -> Unit
) {
    NavigationBarItem(
        icon = {
            if (selectedBarItem) {
                Image(
                    painter = painterResource(id = selectedIconDrawableId),
                    contentDescription = null
                )
            } else {
                Image(
                    painter = painterResource(id = unselectedIconDrawableId),
                    contentDescription = null
                )
            }
        },
        label = {
            when {
                text != null -> Text(
                    text,
                    color = if (selectedBarItem) AppThemeColor else BlackLite, // Change text color based on selection
                    maxLines = 1
                )
                textResId != null -> Text(
                    stringResource(textResId),
                    color = if (selectedBarItem) AppThemeColor else BlackLite, // Change text color based on selection
                    maxLines = 1
                )
            }
        },

        selected = false,
        onClick = {
            onNavItemClicked(navBarItem)
        }
    )
}