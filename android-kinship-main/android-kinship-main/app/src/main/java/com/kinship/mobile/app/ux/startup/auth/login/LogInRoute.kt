package com.kinship.mobile.app.ux.startup.auth.login

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition

object LogInRoute:NavComposeRoute() {
    private const val ROUTE_BASE = "logIn"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()
    fun createRoute():NavRoute{
        return ROUTE_BASE.asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }

}