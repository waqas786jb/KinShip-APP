package com.kinship.mobile.app.ux.container.rsvp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.evenrName.EventNameData
import com.kinship.mobile.app.navigation.NavigationAction
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetRsvpUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val apiEventNameYesList = MutableStateFlow<List<EventNameData>>(emptyList())
    private val apiEventNameNoList = MutableStateFlow<List<EventNameData>>(emptyList())
    private val apiEventNameMaybeList = MutableStateFlow<List<EventNameData>>(emptyList())
    private val showLoader = MutableStateFlow(false)
    private val showRSVPNoFoundText = MutableStateFlow(false)
    private val showRSVPYesFoundText = MutableStateFlow(false)
    private val showRSVPMaybeFoundText = MutableStateFlow(false)
    private val screen = MutableStateFlow("")
    private val eventId = MutableStateFlow("")


    private val type1 = MutableStateFlow(0)
    private val type2 = MutableStateFlow(0)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): RsvpUiState {
        Log.d("TAG", "screen: ${screen.value}")

        return RsvpUiState(
            onYesClick = {
                type1.value = 1
            },
            onNoClick = {
                type2.value = 2
            },
            showRSVPNoFoundText = showRSVPNoFoundText,
            showRSVPYesFoundText = showRSVPYesFoundText,
            showRSVPMaybeFoundText = showRSVPMaybeFoundText,
            onBackClick = {
              //  callMyEventsAPI(coroutineScope)
                navigate(NavigationAction.PopIntent)
            },
            screen = {
                screen.value = it
            },
            eventId = {
                eventId.value = it
                callEventNameYesAPI(coroutineScope, eventId = eventId.value, type = 1, context)
                callEventNameNoAPI(coroutineScope, eventId = eventId.value, type = 2, context)
                callEventNameMaybeAPI(coroutineScope, eventId = eventId.value, type = 3, context)
            },
            showLoader = showLoader,
            apiEventNameYesList = apiEventNameYesList,
            apiEventNameNoList = apiEventNameNoList,
            apiEventNameMaybeList = apiEventNameMaybeList,
            onMaybeClick = {
                type2.value = 3
            }
        )
    }


    private fun callEventNameYesAPI(
        coroutineScope: CoroutineScope,
        eventId: String,
        type: Int,
        context: Context
    ) {
        coroutineScope.launch {
            apiRepository.getEventName(eventId = eventId, type = type).collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                        if (it.message?.contains("Unable to resolve host") == true) {
                            callEventNameYesAPI(coroutineScope, eventId, type, context)
                        }
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        if (it.data?.data.isNullOrEmpty()) {
                            showRSVPYesFoundText.value = true
                        } else {
                            showLoader.value = false
                            showRSVPYesFoundText.value = false
                            apiEventNameYesList.value = it.data?.data ?: emptyList()

                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                }
            }
        }
    }

    private fun callEventNameNoAPI(
        coroutineScope: CoroutineScope,
        eventId: String,
        type: Int,
        context: Context
    ) {
        coroutineScope.launch {
            apiRepository.getEventName(eventId = eventId, type = type).collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        if (it.data?.data.isNullOrEmpty()) {
                            showRSVPNoFoundText.value = true
                        } else {
                            showRSVPNoFoundText.value = false
                            apiEventNameNoList.value = it.data?.data ?: emptyList()
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                }
            }
        }
    }

    private fun callEventNameMaybeAPI(
        coroutineScope: CoroutineScope,
        eventId: String,
        type: Int,
        context: Context
    ) {
        coroutineScope.launch {
            apiRepository.getEventName(eventId = eventId, type = type).collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        if (it.data?.data.isNullOrEmpty()) {
                            showRSVPMaybeFoundText.value = true
                        } else {
                            showRSVPMaybeFoundText.value = false
                            apiEventNameMaybeList.value = it.data?.data ?: emptyList()
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                }
            }
        }
    }
}