package com.kinship.mobile.app.ux.container.setting.changePassword

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil

object ChangePasswordRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "changePassword"
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.NEW_PASSWORD_KEY)}".asNavRouteDefinition()

    fun createRoute(boolean:String): NavRoute {
        return "${ROUTE_BASE}/$boolean".asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val NEW_PASSWORD_KEY = "newPasswordKey"
    }
}