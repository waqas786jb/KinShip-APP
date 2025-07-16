package com.kinship.mobile.app.ux.container.communityFeature.searchCommunity

import android.content.Context
import android.widget.Toast
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.socket.OnSocketEventsListener
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSearchCommunityUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) : OnSocketEventsListener {

    private val searchList = MutableStateFlow<List<MyCommunitiesResponse>>(emptyList())
    private val searchMessageFlow = MutableStateFlow("")

    private val showLoader = MutableStateFlow(false)
    private val noDataFoundText = MutableStateFlow(false)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        searchKey: String,
        navigate: (NavigationAction) -> Unit,
    ): SearchCommunityUiState {

        return SearchCommunityUiState(
            searchMessageFlow = searchMessageFlow,
            onSearchMessageValueChange = {
                searchMessageFlow.value = it
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            showLoader = showLoader,
            searchList = searchList,
            navigateToCommunityPost = { communityId, communityName ->
                if (searchKey == Constants.Community.MY_COMMUNITY_SCREEN) {
                    navigate(
                        NavigationAction.Navigate(
                            CommunityPostRoute.createRoute(
                                communityId = communityId,
                                communityName = communityName,
                                joinCommunity = true,
                                screenName = Constants.AppScreen.SEARCH_SCREEN,
                                notificationType = 0
                            )
                        )
                    )
                } else {
                    navigate(
                        NavigationAction.Navigate(
                            CommunityPostRoute.createRoute(
                                communityId = communityId,
                                communityName = communityName,
                                joinCommunity = false,
                                screenName = Constants.AppScreen.SEARCH_SCREEN,
                                notificationType = 0
                            )
                        )
                    )
                }
            },
            myCommunitySearchAPICall = {
                if (searchKey == Constants.Community.MY_COMMUNITY_SCREEN) {
                    myCommunitySearch(coroutineScope = coroutineScope, context = context)
                } else {
                    exploreCommunitySearch(coroutineScope = coroutineScope, context = context)
                }
            }

        )
    }

    private fun exploreCommunitySearch(coroutineScope: CoroutineScope, context: Context) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.exploreCommunities(type = Constants.Community.TYPE, search = null)
                .collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showLoader.value = false
                            noDataFoundText.value = false
                            showErrorMessage(
                                context = context,
                                errorMsg = it.data?.message ?: "Something went wrong!"
                            )
                        }

                        is NetworkResult.Loading -> {
                            showLoader.value = true
                            noDataFoundText.value = false
                        }

                        is NetworkResult.Success -> {
                            showLoader.value = false
                            it.data?.data.let { list ->
                                if (!list.isNullOrEmpty()) {
                                    searchList.value = list

                                } else {
                                    noDataFoundText.value = true
                                }
                            }
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showLoader.value = false
                            noDataFoundText.value = false
                            showWarningMessage(
                                context = context,
                                errorMsg = it.data?.message ?: "Something went wrong!"
                            )
                        }

                    }
                }
        }
    }

    private fun myCommunitySearch(coroutineScope: CoroutineScope, context: Context) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.myCommunities(search = null)
                .collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showLoader.value = false
                            noDataFoundText.value = false
                            showErrorMessage(
                                context = context,
                                errorMsg = it.data?.message ?: "Something went wrong!"
                            )
                        }

                        is NetworkResult.Loading -> {
                            showLoader.value = true
                            noDataFoundText.value = false
                        }

                        is NetworkResult.Success -> {
                            showLoader.value = false
                            it.data?.data.let { list ->
                                if (!list.isNullOrEmpty()) {
                                    searchList.value = list
                                } else {
                                    noDataFoundText.value = true
                                }
                            }
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showLoader.value = false
                            noDataFoundText.value = false
                            showWarningMessage(
                                context = context,
                                errorMsg = it.data?.message ?: "Something went wrong!"
                            )
                        }

                    }
                }
        }
    }

    private fun showErrorMessage(context: Context, errorMsg: String) {
        Toasty.error(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun showWarningMessage(context: Context, errorMsg: String) {
        Toasty.warning(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }
}