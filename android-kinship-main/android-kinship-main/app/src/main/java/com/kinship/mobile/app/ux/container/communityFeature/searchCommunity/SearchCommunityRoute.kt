package com.kinship.mobile.app.ux.container.communityFeature.searchCommunity

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil


object SearchCommunityRoute: NavComposeRoute(){
    private const val ROUTE_BASE = "searchCommunity"
    override val routeDefinition: NavRouteDefinition = "$ROUTE_BASE/${RouteUtil.defineArg(Arg.SEARCH_KEY)}".asNavRouteDefinition()

    fun createRoute(searchKey:String): NavRoute {
        return "${ROUTE_BASE}/$searchKey".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val SEARCH_KEY = "searchKey"
    }

}