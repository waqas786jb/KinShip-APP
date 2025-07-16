package com.kinship.mobile.app.ux.container.communityFeature.myCommunities

import android.content.Context
import android.widget.Toast
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity.ExploreCommunityRoute
import com.kinship.mobile.app.ux.container.communityFeature.searchCommunity.SearchCommunityRoute
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetMyCommunitiesUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val communitiesList = MutableStateFlow<List<MyCommunitiesResponse>>(emptyList())
    private val showLoader = MutableStateFlow(false)
    private val noDataFound = MutableStateFlow(false)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MyCommunitiesUiState {
//        groupId = getCreateGroupData(coroutineScope)?.chatGroupId.toString()

        return MyCommunitiesUiState(
            showLoader = showLoader,
            communitiesList = communitiesList,
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            onClickOfNewSuggestion = {
                navigate(NavigationAction.Navigate(NewSuggestionRoute.createRoute()))
            },
            onClickOfExploreCommunity = {
                navigate(NavigationAction.Navigate(ExploreCommunityRoute.createRoute()))
            },
            noDataFoundText = noDataFound,
            onClickSearchCommunity={
                navigate(NavigationAction.Navigate(SearchCommunityRoute.createRoute(Constants.Community.MY_COMMUNITY_SCREEN)))
            },
            navigateToCommunityPost = {communityId,communityName->
                navigate(NavigationAction.Navigate(CommunityPostRoute.createRoute(communityId=communityId,communityName=communityName,joinCommunity=true, screenName = Constants.AppScreen.MY_COMMUNITY_SCREEN,notificationType = 0)))
            },
            myCommunityAPICall = {
                getCommunities(coroutineScope = coroutineScope, context = context)
            }
        )
    }


    private fun getCommunities(coroutineScope: CoroutineScope, context: Context) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.myCommunities(null).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        noDataFound.value=false
                        showErrorMessage(context = context, errorMsg = it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                        noDataFound.value=false
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        it.data?.data.let { list ->
                            if (!list.isNullOrEmpty()) {
                                communitiesList.value = list
                            }else{
                                noDataFound.value=true
                            }
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        noDataFound.value=false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }


                }
            }
        }
    }

    private fun showErrorMessage(context: Context, errorMsg: String) {
        Toasty.error(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }
}