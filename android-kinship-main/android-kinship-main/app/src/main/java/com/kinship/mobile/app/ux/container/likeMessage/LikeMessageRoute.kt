package com.kinship.mobile.app.ux.container.likeMessage

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition

object LikeMessageRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "likeMessage"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    fun createRoute(): NavRoute {
        return ROUTE_BASE.asNavRoute()
    }

}


