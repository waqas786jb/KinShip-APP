package com.kinship.mobile.app.navigation.graph

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.navigation.SimpleNavComposeRoute
import com.kinship.mobile.app.ux.main.findingKinship.FindingKinshipRoute
import com.kinship.mobile.app.ux.main.findingKinship.FindingKinshipScreen
import com.kinship.mobile.app.ux.startup.auth.signUp.SignUpRoute
import com.kinship.mobile.app.ux.startup.auth.signUp.SignInScreen
import com.kinship.mobile.app.ux.startup.auth.forgetPassword.ForgetPasswordRoute
import com.kinship.mobile.app.ux.startup.auth.forgetPassword.ForgetPasswordScreen
import com.kinship.mobile.app.ux.startup.auth.login.LogInRoute
import com.kinship.mobile.app.ux.startup.auth.login.LogInScreen
import com.kinship.mobile.app.ux.startup.auth.otpVerfication.OtpVerificationRoute
import com.kinship.mobile.app.ux.startup.auth.otpVerfication.OtpVerificationScreen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.CommonQuestionRoute
import com.kinship.mobile.app.ux.startup.auth.questionFlow.CommonQuestionScreen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted.Adopted1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted.Adopted1Screen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow.Baby1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow.Baby1Screen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1.Conceive1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1.Conceive1Screen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2.Conceive2Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2.Conceive2Screen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1.Pregnant1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1.Pregnant1Screen
import com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails.UserProfileRoute
import com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails.UserProfileScreen
import com.kinship.mobile.app.ux.startup.onboarding.splash.SplashRoute
import com.kinship.mobile.app.ux.startup.onboarding.splash.SplashScreen

@Composable
fun AppStartUpGraph(
    navController: NavHostController,
    startDestination: String,
    bundle: Bundle?,
    restartApp:String
) {
    val appStartDestination = when (startDestination) {
        Constants.AppScreen.START_UP -> {
            SplashRoute.routeDefinition.value
        }
        Constants.AppScreen.LOG_IN -> {
            LogInRoute.routeDefinition.value
        }
        Constants.AppScreen.SIGN_UP -> {
            SignUpRoute.routeDefinition.value
        }
        Constants.AppScreen.OTP_VERIFICATION -> {
            OtpVerificationRoute.routeDefinition.value
        }
        Constants.AppScreen.COMMON_QUESTION_SCREEN -> {
            CommonQuestionRoute.routeDefinition.value
        }
        else -> {
            SplashRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = SplashRoute.routeDefinition.value) {
        (SplashRoute as SimpleNavComposeRoute).addNavigationRoute(this) { SplashScreen(navController,bundle, restartApp = restartApp) }
        LogInRoute.addNavigationRoute(this) { LogInScreen(navController) }
        SignUpRoute.addNavigationRoute(this) { SignInScreen(navController) }
        ForgetPasswordRoute.addNavigationRoute(this) { ForgetPasswordScreen(navController) }
        OtpVerificationRoute.addNavigationRoute(this) { OtpVerificationScreen(navController) }
        CommonQuestionRoute.addNavigationRoute(this) { CommonQuestionScreen(navController) }
        Conceive1Route.addNavigationRoute(this) { Conceive1Screen(navController) }
        Conceive2Route.addNavigationRoute(this) { Conceive2Screen(navController) }
        UserProfileRoute.addNavigationRoute(this) { UserProfileScreen(navController) }
        Pregnant1Route.addNavigationRoute(this) { Pregnant1Screen(navController) }
        Baby1Route.addNavigationRoute(this) { Baby1Screen(navController) }
        Adopted1Route.addNavigationRoute(this) { Adopted1Screen(navController) }
        FindingKinshipRoute.addNavigationRoute(this) { FindingKinshipScreen(navController) }
    }
}
