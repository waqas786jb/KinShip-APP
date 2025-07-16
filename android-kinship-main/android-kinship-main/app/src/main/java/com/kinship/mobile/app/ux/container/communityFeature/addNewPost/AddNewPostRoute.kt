package com.kinship.mobile.app.ux.container.communityFeature.addNewPost

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil


object AddNewPostRoute: NavComposeRoute(){
    private const val ROUTE_BASE = "addNewPost"
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.COMMUNITY_ID)}".asNavRouteDefinition()

    fun createRoute(communityId:String): NavRoute {
        return "${ROUTE_BASE}/$communityId".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val COMMUNITY_ID = "communityId"
    }
}


