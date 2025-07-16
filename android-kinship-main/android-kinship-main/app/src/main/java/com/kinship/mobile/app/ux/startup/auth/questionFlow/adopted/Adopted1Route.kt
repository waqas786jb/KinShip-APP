package com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition

object Adopted1Route:NavComposeRoute() {
    private const val ROUTE_BASE = "adopted1Screen"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()
    fun createRoute(): NavRoute {
        return ROUTE_BASE.asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }

}