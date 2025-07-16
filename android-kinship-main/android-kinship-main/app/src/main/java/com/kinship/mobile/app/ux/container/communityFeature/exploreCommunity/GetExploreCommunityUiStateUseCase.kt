package com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity
import android.content.Context
import android.widget.Toast
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.socket.OnSocketEventsListener
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.searchCommunity.SearchCommunityRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetExploreCommunityUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) : OnSocketEventsListener {
    private val exploreCommunitiesList = MutableStateFlow<List<MyCommunitiesResponse>>(emptyList())


    private val showLoader = MutableStateFlow(false)
    private val noDataFoundText = MutableStateFlow(false)


    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ExploreCommunityUiState {

        return ExploreCommunityUiState(
            showLoader = showLoader,
            noDataFoundText = noDataFoundText,
            exploreCommunitiesList = exploreCommunitiesList,
            onClickSearchCommunity = {
                navigate(NavigationAction.Navigate(SearchCommunityRoute.createRoute(Constants.Community.EXPLORE_NEW_COMMUNITY)))
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            navigateToCommunityPost = {communityId,communityName->
                navigate(NavigationAction.Navigate(CommunityPostRoute.createRoute(communityId=communityId,communityName=communityName, joinCommunity = false, screenName = Constants.AppScreen.EXPLORE_COMMUNITY_SCREEN,notificationType = 0)))
            },
            exploreCommunityAPICall = {
                getExploreCommunities(coroutineScope = coroutineScope, context = context)
            }
        )
    }

    private fun getExploreCommunities(coroutineScope: CoroutineScope, context: Context) {
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
                                    exploreCommunitiesList.value = list
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

                        else -> {
                            showLoader.value = false
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