package com.kinship.mobile.app.ux.main.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetMessageUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository
) {


    private val apiMessageUserList = MutableStateFlow<List<MessageTabResponse>>(emptyList())
    private val noDataFound = MutableStateFlow(false)

    private var currentPage = 1

    private val showLoader = MutableStateFlow(false)
    private val noChatAvailable = MutableStateFlow(false)

    private val isAllEventsRefreshing = MutableStateFlow(false)

    private val isLoader = MutableStateFlow(false)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MessageUiState {
        //callMessageListAPI(coroutineScope = coroutineScope, context = context)
        return MessageUiState(

            noDataFoundText = noDataFound,
            onNoDataFound = { noDataFound.value = it },
            onNavigateToNewMessage = {
                navigateToNewMessage(
                    context,
                    navigate,
                    Constants.ContainerScreens.NEW_MESSAGE
                )
            },
            messageListAPICall = {
                callMessageListAPI(coroutineScope, context)
            },
            navigateToSingleUserChat = { messageTabResponse ->
                navigateToSingleUserChat(
                    context,
                    navigate,
                    Constants.ContainerScreens.SINGLE_USER_CHAT_SCREEN,
                    messageTabResponse

                )
            },
            apiUserMessageList = apiMessageUserList,
            showLoader = showLoader,
            noChatAvailable = noChatAvailable,
            isLoading = isLoader,
            isAllEventsRefreshing = isAllEventsRefreshing


        )
    }
    private fun callMessageListAPI(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            apiRepository.userGroupList(page = currentPage).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        isLoader.value=false
                        noChatAvailable.value=false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()

                    }
                    is NetworkResult.Loading -> {
                        if (currentPage == 1) showLoader.value = true else isLoader.value = true
                        noChatAvailable.value=false
                    }

                    is NetworkResult.Success -> {
                        if (it.data?.data.isNullOrEmpty()){
                            noChatAvailable.value=true
                            showLoader.value = false
                            isLoader.value=false
                        }else{
                            apiMessageUserList.value = it.data?.data ?: emptyList()
                            showLoader.value = false
                            isLoader.value=false
                            noChatAvailable.value=false
                        }
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        isLoader.value=false
                        noChatAvailable.value=false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
            currentPage++
        }
    }

    private fun navigateToNewMessage(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun navigateToSingleUserChat(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        messageTabResponse: MessageTabResponse
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.IS_COME_FOR, Constants.AppScreen.MESSAGE_SCREEN)
        bundle.putString(Constants.BundleKey.MESSAGE_RESPONSE, Gson().toJson(messageTabResponse))
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
}