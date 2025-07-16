package com.kinship.mobile.app.ux.container.singleUserChat

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil
import com.kinship.mobile.app.ux.main.events.createEvent.CreateEventRoute

object SingleGroupChatRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "singleUserChat"

   // override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.MAIN_ID)}/${RouteUtil.defineArg(Arg.SUB_ID)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}}".asNavRouteDefinition()
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.MAIN_ID)}/${RouteUtil.defineArg(Arg.SUB_ID)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}/${RouteUtil.defineArg(Arg.SINGLE)}".asNavRouteDefinition()

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }

    fun createRoute(mainId: String, subId: String,screenName:String,single:Boolean): NavRoute {
        val isSingle = single.toString()
        return "${ROUTE_BASE}/$mainId/$subId/$screenName/$isSingle".asNavRoute()
    }
    object Arg {
        const val MAIN_ID = "mainID"
        const val SUB_ID = "subId"
        const val SCREEN_NAME = "screenName"
        const val SINGLE = "single"


    }
}
/*private const val ROUTE_BASE = "groupChat"
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
}*/

