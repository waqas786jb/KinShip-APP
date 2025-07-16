package com.kinship.mobile.app.ux.container.setting.eventDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.container.rsvpActivity.RsvpActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class EventDetailsUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val eventId = MutableStateFlow("")
    private val apiEventList = MutableStateFlow<List<MyEventsData>>(emptyList())
    private val apiUComingList = MutableStateFlow<List<MyEventsData>>(emptyList())
    private val showLoader = MutableStateFlow(false)
    private val showUpcomingLoader = MutableStateFlow(false)
    private val showNoEventText = MutableStateFlow(false)
    private val showNoUpComingText = MutableStateFlow(false)
    private val openEventDialog = MutableStateFlow(false)
    private val screenName = MutableStateFlow("")
    private val isAllEventsRefreshing = MutableStateFlow(false)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): EventDetailsUiState {
        return EventDetailsUiState(
            apiEventList = apiEventList,
            onApiSuccess = {},
            showNoDataFoundText = showNoEventText,
            apiUpComingList = apiUComingList,
            showLoader = showLoader,
            eventId = {
                eventId.value = it
                Log.d("TAG", "invoke: ${eventId.value}")
            },
            sendScreenName = {
                screenName.value = it
            },
            myEventAndEventApiCall = {
                callMyEventsAPI(coroutineScope, context)
                callUpcomingEventsAPI(coroutineScope, context)
            },
            showUpComingLoader = showUpcomingLoader,
            showUpComingNoFoundText = showNoUpComingText,
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            navigateToRsvpScreen = {
                navigateToRSVPScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.RSVP_SCREEN,
                    it
                )
            },
            eventDeleteAPICall = {
                callEventDeleteAPICall(coroutineScope, context, it)
            },
            navigateToCreateEvent = {
                navigateToSingleUserChat(
                    context,
                    navigate,
                    Constants.ContainerScreens.CREATE_EVENT_EDIT,
                    it

                )
            },
            isAllEventsRefreshing = isAllEventsRefreshing
        )
    }

    private fun navigateToRSVPScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        eventId: String
    ) {
        val bundle = Bundle()
        val intent = Intent(context, RsvpActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)
        bundle.putString(Constants.BundleKey.EVENT_ID,eventId)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun navigateToSingleUserChat(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        myEventData: MyEventsData
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.IS_COME_FOR, Constants.AppScreen.CREATE_EVENT)
        bundle.putString(Constants.BundleKey.MY_EVENT_RESPONSE, Gson().toJson(myEventData))
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun callEventDeleteAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        eventId: String
    ) {
        coroutineScope.launch {
            apiRepository.eventDelete(eventId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        openEventDialog.value = false
                        showLoader.value = false
                        openEventDialog.value = false
                        removeEventFromList(eventId)
                        if (apiEventList.value.isEmpty()) {
                            showNoEventText.value = true
                        }
                        Toasty.success(
                            context,
                            it.data?.message.toString(),
                            Toast.LENGTH_SHORT,
                            false
                        ).show()
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                }
            }
        }
    }
    private fun removeEventFromList(eventId: String) {
        val updatedList = apiEventList.value.filter { it.id != eventId }
        apiEventList.value = updatedList
    }
    private fun callMyEventsAPI(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            apiRepository.getMyEvents().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        if (it.data?.data.isNullOrEmpty()) {
                            showNoEventText.value = true
                        } else {
                            showNoEventText.value = false
                            apiEventList.value = it.data?.data ?: emptyList()
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }
    private fun callUpcomingEventsAPI(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            apiRepository.getUpcomingEvents().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        if (it.data?.data.isNullOrEmpty()) {
                            showNoUpComingText.value = true
                        } else {
                            showNoUpComingText.value = false
                            apiUComingList.value = it.data?.data ?: emptyList()
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }
}