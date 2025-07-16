package com.kinship.mobile.app.ux.startup.onboarding.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.versionRequest.KinshipVersionRequest
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.main.MainActivity
import com.kinship.mobile.app.ux.startup.auth.login.LogInRoute
import com.kinship.mobile.app.ux.startup.auth.questionFlow.CommonQuestionRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DUPLICATE_LABEL_IN_WHEN")
class GetSplashUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    var bundle = ""

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SplashUiState {
        return SplashUiState(
            notificationKey = {
                if (it.isEmpty) {
                } else {
                    managePushIntent(it, context, navigate)
                }
            },
            restartAppKey = {
                if (it == Constants.BundleKey.RESTART_APP) {
                    navigate(NavigationAction.PopAndNavigate(LogInRoute.createRoute()))
                } else {
                    makeKinshipVersionInReq(coroutineScope, context, navigate)
                }
            }
        )
    }
    private fun navigateToNextScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            delay(3000)
            if (appPreferenceDataStore.getUserData() != null) {
                // navigate(NavigationAction.PopAndNavigate(LogInRoute.createRoute()))
                if (appPreferenceDataStore.getUserData()?.isVerify == true) {
                    if (appPreferenceDataStore.getUserData()?.isProfileCompleted == true) {
                        val intent = Intent(context, MainActivity::class.java)
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    } else {
                        navigate(NavigationAction.Navigate(CommonQuestionRoute.createRoute()))
                    }
                } else {
                    navigate(NavigationAction.PopAndNavigate(LogInRoute.createRoute()))
                }
            } else {
                navigate(NavigationAction.PopAndNavigate(LogInRoute.createRoute()))
            }
        }
    }

    private fun makeKinshipVersionInReq(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val kinshipVersionRequest = KinshipVersionRequest(
            type = Constants.VersionType.VERSION_TYPE,
            version = com.kinship.mobile.app.BuildConfig.VERSION_NAME
        )
        callKinshipNameEdit(kinshipVersionRequest, coroutineScope, context, navigate)
    }

    private fun managePushIntent(
        bundle: Bundle,
        context: Context,
        navigate: (NavigationAction) -> Unit,
    ): Intent {
        Log.e("TAG", "managePushIntent:${bundle.getString(Constants.NotificationPush.TYPE)} ")
        if (bundle.getString(Constants.NotificationPush.TYPE).toString().isNotEmpty()) {
            when (bundle.getString(Constants.NotificationPush.TYPE)?.toInt()) {
                Constants.NotificationPush.PUSH_KINSHIP_GROUP_TYPE -> {
                    val intent = Intent(context, ContainerActivity::class.java)
                    intent.putExtra(
                        Constants.IntentData.SCREEN_NAME,
                        Constants.ContainerScreens.CHAT_SCREEN
                    )
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }

                Constants.NotificationPush.PUSH_EVENT_CREATE -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(
                        Constants.IntentData.SCREEN_NAME,
                        Constants.AppScreen.EVENT_SCREEN
                    )
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }

                Constants.NotificationPush.PUSH_SINGLE_SUBGROUP_CHAT -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(
                        Constants.IntentData.SCREEN_NAME,
                        Constants.AppScreen.MESSAGE_SCREEN
                    )
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }
                Constants.NotificationPush.COMMUNITY_POST_TYPE -> {
                    val intent = Intent(context, ContainerActivity::class.java)
                    intent.putExtra(
                        Constants.IntentData.SCREEN_NAME,
                        Constants.ContainerScreens.COMMUNITY_POST_SCREEN
                    )
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }
                Constants.NotificationPush.COMMUNITY_POST_TYPE -> {
                    val intent = Intent(context, ContainerActivity::class.java)
                    intent.putExtra(
                        Constants.IntentData.SCREEN_NAME,
                        Constants.ContainerScreens.MY_COMMUNITIES_SCREEN
                    )
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }

                else -> {
                    return MainActivity.newIntent(context)
                }
            }
        } else {
            return Intent(context, MainActivity::class.java)
        }
    }

    private fun callKinshipNameEdit(
        kinshipVersionRequest: KinshipVersionRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.kinshipVersion(kinshipVersionRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        navigateToNextScreen(
                            context = context,
                            navigate = navigate,
                            coroutineScope = coroutineScope
                        )
                    }

                    is NetworkResult.UnAuthenticated -> {
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }
}