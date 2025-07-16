package com.kinship.mobile.app.ux.container.rsvp

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil

object RsvpRoute: NavComposeRoute(){
    private const val ROUTE_BASE = "rsvp"
    override val routeDefinition: NavRouteDefinition= "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.EVENT_ID)}".asNavRouteDefinition()

    fun createRoute(eventId:String): NavRoute {
        return "${ROUTE_BASE}/$eventId".asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val EVENT_ID = "eventId"
    }

}