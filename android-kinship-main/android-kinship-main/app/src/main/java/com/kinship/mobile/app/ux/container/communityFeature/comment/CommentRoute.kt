package com.kinship.mobile.app.ux.container.communityFeature.comment

import android.net.Uri
import android.util.Log
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil

object CommentRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "communityComment"
    override val routeDefinition: NavRouteDefinition = "$ROUTE_BASE/{${Arg.COMMUNITY_DATA}}/{${Arg.SCREEN_NAME}}".asNavRouteDefinition()

    fun createRoute(communityPost: String,screenName:String): NavRoute {
        val enCodedCommunityData = Uri.encode(communityPost.toString())
        Log.d("TAG", "createRoute: $enCodedCommunityData")
        return "$ROUTE_BASE/$enCodedCommunityData/$screenName".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.COMMUNITY_DATA) { type = NavType.StringType },
            navArgument(Arg.SCREEN_NAME) { type = NavType.StringType }

        )
    }

    object Arg {
        const val COMMUNITY_DATA = "communityData"
        const val SCREEN_NAME = "screenName"
    }
}
