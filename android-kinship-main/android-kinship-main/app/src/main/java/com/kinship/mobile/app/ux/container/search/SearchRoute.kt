package com.kinship.mobile.app.ux.container.search

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition

object SearchRoute:  NavComposeRoute(){
    private const val ROUTE_BASE = "search"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

    fun createRoute(): NavRoute {
        return ROUTE_BASE.asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }

}