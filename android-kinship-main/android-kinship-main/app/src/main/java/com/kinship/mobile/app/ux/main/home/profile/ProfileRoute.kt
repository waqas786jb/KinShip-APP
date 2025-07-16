package com.kinship.mobile.app.ux.main.home.profile

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition

object ProfileRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "profile"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

    fun createRoute(): NavRoute {
        return ProfileRoute.ROUTE_BASE.asNavRoute()
    }


    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
}