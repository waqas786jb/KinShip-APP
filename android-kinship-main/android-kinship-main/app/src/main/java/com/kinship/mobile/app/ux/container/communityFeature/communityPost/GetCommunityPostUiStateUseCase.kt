package com.kinship.mobile.app.ux.container.communityFeature.communityPost


import android.content.Context
import android.util.Log
import android.widget.Toast
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.communityFeature.addNewPost.AddNewPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.comment.CommentRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetCommunityPostUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val showLoader = MutableStateFlow(false)
    private val _communityName = MutableStateFlow("")
    private val communityName: StateFlow<String> get() = _communityName

    private val _joinCommunity = MutableStateFlow(false)
    private val joinCommunity: StateFlow<Boolean> get() = _joinCommunity

    private val _screenName = MutableStateFlow("")
    private val screenName: StateFlow<String> get() = _screenName

    private val _communityId = MutableStateFlow("")
    private val communityId: StateFlow<String> get() = _communityId
    private val noDataFoundText = MutableStateFlow(false)

    private val isAllEventsRefreshing = MutableStateFlow(false)

    private val showLikeDislike = MutableStateFlow(false)
    private val showLikeCount = MutableStateFlow(false)
    private val communityPostList = MutableStateFlow<List<CommunityPostResponse>>(emptyList())
    private val openPickImgDialog = MutableStateFlow(false)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        communityId: String,
        communityName: String,
        joinCommunity: Boolean,
        screen:String,
        notificationType:Int,
        navigate: (NavigationAction) -> Unit,
    ): CommunityPostUiState {
        this._communityName.value = communityName
        this._joinCommunity.value = joinCommunity
        this._screenName.value = screen
        this._communityId.value=communityId
        return CommunityPostUiState(
            showLoader = showLoader,
            communityPostList = communityPostList,
            noDataFoundText = noDataFoundText,
            communityName = this.communityName,
            joinCommunity = this.joinCommunity,
            screenName = this.screenName,
            navigateToAddNewPost = {
                navigate(NavigationAction.Navigate(AddNewPostRoute.createRoute(communityId)))
            },
            communityPostAPICall = {
                if (screen==Constants.AppScreen.NOTIFICATION_SCREEN){
                    callUserNotificationRedirectAPICall(coroutineScope = coroutineScope, context = context, communityId = communityId, notificationType = notificationType)
                }else{
                    callCommunityPostAPICall(
                        coroutineScope = coroutineScope,
                        context = context,
                        communityId = communityId
                    )
                }
                /*  callCommunityPostAPICall(
                    coroutineScope = coroutineScope,
                    context = context,
                    communityId = communityId
                )*/

            },
            onJoinCommunityClick = {
                callJoinCommunityAPICall(
                    coroutineScope = coroutineScope,
                    context = context,
                    communityId = communityId
                )
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            navigateToCommentScreen={
                val data = Gson().toJson(it)
                navigate(NavigationAction.Navigate(CommentRoute.createRoute(data, screenName = Constants.AppScreen.COMMUNITY_POST_SCREEN)))
            },
            openPickImgDialog = openPickImgDialog,
            onOpenORDismissDialog = {
                openPickImgDialog.value=it
            },
            leaveCommunityAPICall = {
                callLeaveCommunityAPICall(coroutineScope = coroutineScope, context = context, communityId = communityId,navigate)
            },
            communityPostLikeDislike={postId,like->
                Log.d("TAG", "likePostId: $postId,$like")
                callCommunityLikeDislikeAPI(post =postId, like =like, coroutineScope = coroutineScope, context = context)
            },
            showLikeDislike = showLikeDislike,
            onShowLikeDislike = {
                showLikeDislike.value=it
            },
            showLikeCountDislike = showLikeCount,
            onShowLikeCountDislike = { showLikeCount.value=it },
            communityId = this.communityId,
            isAllEventsRefreshing = isAllEventsRefreshing

        )
    }

    private fun callCommunityLikeDislikeAPI(
        post: String,
        like:Boolean,
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        coroutineScope.launch {
            apiRepository.communityPostLikeDislike(postId = post, like = like).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.data?.message ?: "Something went wrong!"
                        )
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value=true

                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.data?.message ?: "UnAuthenticated"
                        )
                    }

                }
            }
        }
    }

    private fun callCommunityPostAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        communityId: String
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.communityPost(communityId = communityId, page = 1).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        noDataFoundText.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "Something went wrong!"
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
                                communityPostList.value = list
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
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }


                }
            }
        }
    }

    private fun callLeaveCommunityAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        communityId: String,
        navigate: (NavigationAction) -> Unit
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.leaveCommunity(communityId = communityId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "Something went wrong!"
                        )
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                        showSuccessMessage(context,it.data?.message?:"")
                        navigate(NavigationAction.Pop())
                        openPickImgDialog.value=false
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }

                }
            }
        }
    }

    private fun callUserNotificationRedirectAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        communityId: String,
        notificationType:Int,
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.userNotificationRedirect(mainId = communityId, subId = "", type = notificationType).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "Something went wrong!"
                        )
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                        communityPostList.value=it.data?.data?: emptyList()
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }

                }
            }
        }
    }

    private fun callJoinCommunityAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        communityId: String
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.joinCommunity(communityId = communityId).collect {
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
                        showSuccessMessage(context = context, errorMsg = it.data?.message.toString())
                        _joinCommunity.value=true
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        noDataFoundText.value = false
                        showWarningMessage(
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

    private fun showWarningMessage(context: Context, errorMsg: String) {
        Toasty.warning(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }
    private fun showSuccessMessage(context: Context, errorMsg: String) {
        Toasty.success(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }
}