package com.kinship.mobile.app.ux.container.communityFeature.communityPost

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil


object CommunityPostRoute: NavComposeRoute(){
    private const val ROUTE_BASE = "communityPost"
    override val routeDefinition: NavRouteDefinition = "$ROUTE_BASE/${RouteUtil.defineArg(Arg.COMMUNITY_ID)}/${RouteUtil.defineArg(Arg.COMMUNITY_NAME)}/${RouteUtil.defineArg(Arg.JOIN_COMMUNITY)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}/${RouteUtil.defineArg(Arg.NOTIFICATION_TYPE)}".asNavRouteDefinition()
    //override val routeDefinition: NavRouteDefinition = "$ROUTE_BASE/{${Arg.COMMUNITY_ID}}/{${Arg.COMMUNITY_NAME}}/{${Arg.JOIN_COMMUNITY}}/{${Arg.SCREEN_NAME}}/{${Arg.NOTIFICATION_TYPE}}".asNavRouteDefinition()


    fun createRoute(communityId: String, communityName: String, joinCommunity: Boolean, screenName: String, notificationType: Int?): NavRoute {
        val encodedCommunityId = Uri.encode(communityId)
        val encodedCommunityName = Uri.encode(communityName)
        val isJoinCommunity = joinCommunity.toString()
        val notificationTypeSegment = notificationType?.toString() ?: ""
        return "$ROUTE_BASE/$encodedCommunityId/$encodedCommunityName/$isJoinCommunity/$screenName/$notificationTypeSegment".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.COMMUNITY_ID) { type = NavType.StringType },
            navArgument(Arg.COMMUNITY_NAME) { type = NavType.StringType },
            navArgument(Arg.JOIN_COMMUNITY) { type = NavType.BoolType },
            navArgument(Arg.SCREEN_NAME) { type = NavType.StringType },
            navArgument(Arg.NOTIFICATION_TYPE) { type = NavType.IntType }
        )
    }
    object Arg {
        const val COMMUNITY_ID = "communityId"
        const val COMMUNITY_NAME = "communityName"
        const val JOIN_COMMUNITY = "joinCommunity"
        const val SCREEN_NAME = "screenName"
        const val NOTIFICATION_TYPE = "notificationType"
    }
}

//override val routeDefinition: NavRouteDefinition = "$ROUTE_BASE/${RouteUtil.defineArg(Arg.MAIN_ID)}/${RouteUtil.defineArg(Arg.SUB_ID)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}".asNavRouteDefinition()



