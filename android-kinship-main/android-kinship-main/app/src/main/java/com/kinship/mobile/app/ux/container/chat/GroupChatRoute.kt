package com.kinship.mobile.app.ux.container.chat

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil

object GroupChatRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "groupChat"
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.MESSAGE_ID)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}/${RouteUtil.defineArg(Arg.SUB_ID)}".asNavRouteDefinition()
    fun createRoute(messageId:String,screenName:String,subId:String): NavRoute {
        return "${ROUTE_BASE}/$messageId/$screenName/$subId".asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val MESSAGE_ID = "messageId"
        const val SUB_ID = "subId"
        const val SCREEN_NAME = "screenName"
    }
}