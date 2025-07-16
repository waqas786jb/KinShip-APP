package com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification

import androidx.navigation.NamedNavArgument
import com.kinship.mobile.app.navigation.NavComposeRoute
import com.kinship.mobile.app.navigation.NavRoute
import com.kinship.mobile.app.navigation.NavRouteDefinition
import com.kinship.mobile.app.navigation.asNavRoute
import com.kinship.mobile.app.navigation.asNavRouteDefinition
import com.kinship.mobile.app.utils.RouteUtil

object OtpUpdateVerificationRoute:NavComposeRoute() {
    private const val ROUTE_BASE = "otpUpdateVerification"
    override val routeDefinition: NavRouteDefinition = "$ROUTE_BASE/${RouteUtil.defineArg(Arg.EMAIL)}".asNavRouteDefinition()
    fun createRoute(email: String):NavRoute{
        return "$ROUTE_BASE/$email".asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val EMAIL = "email"

    }

}