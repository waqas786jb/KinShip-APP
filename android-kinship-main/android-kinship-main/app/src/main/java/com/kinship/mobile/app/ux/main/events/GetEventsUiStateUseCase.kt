package com.kinship.mobile.app.ux.main.events

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.EventStatusRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.container.rsvpActivity.RsvpActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetEventsUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val apiEventResultFlow =
        MutableStateFlow<NetworkResult<ApiResponseNew<MyEventsData>>?>(null)
    private val apiChangeEventStatusResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)
    private val showDialog = MutableStateFlow(false)
    private val showLoader = MutableStateFlow(false)
    private val apiEventList = MutableStateFlow<List<MyEventsData>>(emptyList())
    private val showEventNoFoundText = MutableStateFlow(false)
    private val apiMyEventList = MutableStateFlow<List<MyEventsData>>(emptyList())
    private val noDataFoundAllEvent = MutableStateFlow(false)
    private val openEventDialog = MutableStateFlow(false)
    private val apiUComingList = MutableStateFlow<List<MyEventsData>>(emptyList())
    private val showNoUpComingText = MutableStateFlow(false)
    private val isAllEventsRefreshing = MutableStateFlow(false)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): EventsUiState {
        return EventsUiState(
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            apiEventStatusResultFlow = apiChangeEventStatusResultFlow,
            onStatusClicked = { status, id ->
                callEventsStatusAPI(
                    coroutineScope = coroutineScope,
                    eventId = id,
                    status = status,
                    context
                )
            },
            showEventNoFoundText = showEventNoFoundText,
            apiEventList = apiEventList,
            showDialog = showDialog,
            onShowDialog = {
                showDialog.value = it

            },
            showLoader = showLoader,
            navigateToCreateEvent = {
                if (getCreateGroupData(coroutineScope)?.isGroupCreated == true) {
                    navigateToCreateEvent(
                        context,
                        navigate,
                        Constants.ContainerScreens.CREATE_EVENT_SCREEN
                    )
                } else {
                    showDialog.value = true
                }
            },
            onStatusSuccess = { callEventsListAPI(coroutineScope, context) },
            noDataFoundMyEventText = noDataFoundAllEvent,
            apiMyEventList = apiMyEventList,
            navigateToMyEventEvent = {
                navigateToSingleUserChat(
                    context,
                    navigate,
                    Constants.ContainerScreens.CREATE_EVENT_EDIT,
                    it
                )
            },
            eventDeleteAPICall = {
                callEventDeleteAPICall(coroutineScope, context, it)
            },
            myEventAndEventApiCall = {
                callEventsListAPI(coroutineScope, context)
                callMyEventsAPI(coroutineScope, context)
                callUpcomingEventsAPI(coroutineScope = coroutineScope,context)
            },
            navigateToRsvpScreen = {
                navigateToRSVPScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.RSVP_SCREEN,
                    it
                )
            },
            apiUpComingList = apiUComingList,
            showUpComingNoFoundText = showNoUpComingText,
            isAllEventsRefreshing = isAllEventsRefreshing,
            onBackClick={
                navigateToCreateEvent(
                    context,
                    navigate,
                    Constants.ContainerScreens.NOTIFICATION_LISTING_SCREEN
                )
            }
        )
    }
    private fun callEventsListAPI(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            apiRepository.getEvents().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = false
                    }

                    is NetworkResult.Success -> {
                        if (it.data?.data.isNullOrEmpty()) {
                            showEventNoFoundText.value = true
                            showLoader.value = false
                        } else {
                            showEventNoFoundText.value = false
                            showLoader.value = false
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
                        if (apiMyEventList.value.isEmpty()) {
                            noDataFoundAllEvent.value = true
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
    private fun removeEventFromList(eventId: String) {
        val updatedList = apiMyEventList.value.filter { it.id != eventId }
        apiMyEventList.value = updatedList

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
        bundle.putString(Constants.BundleKey.EVENT_ID, eventId)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
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
                            noDataFoundAllEvent.value = true
                        } else {
                            noDataFoundAllEvent.value = false
                            apiMyEventList.value = it.data?.data ?: emptyList()
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
    private fun callEventsStatusAPI(
        coroutineScope: CoroutineScope,
        eventId: String,
        status: Int,
        context: Context
    ) {
        val request = EventStatusRequest(
            eventId = eventId,
            status = status
        )
        coroutineScope.launch {
            apiRepository.changeEventStatus(request).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                        callEventsListAPI(coroutineScope, context)
                        apiEventList.value = emptyList()
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.success(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()

                    }
                }
            }
        }
    }
    private fun navigateToCreateEvent(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun clearAllAPIResultFlow() {
        apiEventResultFlow.value = null
        apiChangeEventStatusResultFlow.value = null
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }
}