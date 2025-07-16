package com.kinship.mobile.app.ux.container.communityFeature.comment

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.SoftwareKeyboardController
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.community.commentRequest.AddCommentRequest
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationListResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity.ExploreCommunityRoute
import com.kinship.mobile.app.ux.container.communityFeature.searchCommunity.SearchCommunityRoute
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionRoute
import com.kinship.mobile.app.ux.container.setting.notification.notificationListing.NotificationListingUiState
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetCommentUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val commentList = MutableStateFlow<List<CommunityPostResponse>>(emptyList())
    private val showLoader = MutableStateFlow(false)

    private val _commentData = MutableStateFlow(CommunityPostResponse())
    private val commentData: StateFlow<CommunityPostResponse> get() = _commentData

    private val _notificationData = MutableStateFlow(NotificationListResponse())
    private val notificationData: StateFlow<NotificationListResponse> get() = _notificationData

    private val notificationCommentData = MutableStateFlow(CommunityPostResponse())

    private val noDataFound = MutableStateFlow(false)
    private val commentFlow = MutableStateFlow("")

    private val _screenName = MutableStateFlow("")
    private val screenName: StateFlow<String> get() = _screenName

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        commentData: String?,
        screenName: String,
        navigate: (NavigationAction) -> Unit,
    ): CommentUiState {


        if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN){
            val data: NotificationListResponse =
                Gson().fromJson(commentData, NotificationListResponse::class.java)
            _notificationData.value = data
        }else{
            val data: CommunityPostResponse =
                Gson().fromJson(commentData, CommunityPostResponse::class.java)
            _commentData.value = data
        }
            _screenName.value = screenName

        if (screenName == Constants.AppScreen.NOTIFICATION_SCREEN) {
            callUserNotificationRedirectAPICall(
                coroutineScope = coroutineScope,
                context = context,
                communityId = notificationData.value.mainId ?: "",
                navigate = navigate
            )
        } else {
            callPostCommentAPICall(
                coroutineScope = coroutineScope,
                context = context,
                postId = this.commentData.value.id ?: ""
            )
        }

        return CommentUiState(
            showLoader = showLoader,
            commentList = commentList,
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            onClickOfNewSuggestion = {
                navigate(NavigationAction.Navigate(NewSuggestionRoute.createRoute()))
            },
            onClickOfExploreCommunity = {
                navigate(NavigationAction.Navigate(ExploreCommunityRoute.createRoute()))
            },
            noDataFoundText = noDataFound,
            onClickSearchCommunity = {
                navigate(NavigationAction.Navigate(SearchCommunityRoute.createRoute(Constants.Community.MY_COMMUNITY_SCREEN)))
            },
            commentData = if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN) this.commentData else this.commentData,
            commentFlow = commentFlow,
            onCommentValueChange = {
                commentFlow.value = it
            },
            onCommentSend = { keyboard ->
                if (commentFlow.value.isNotEmpty()) {
                    makeAddCommentInReq(
                        coroutineScope = coroutineScope,
                        post = this.commentData.value.id ?: "",
                        message = commentFlow.value,
                        context = context,
                        keyboard = keyboard
                    )
                } else {
                    showWarningMessage(context = context, errorMsg = "Please add comment text")
                }

            },
            communityPostLikeDislike = { postId, like ->
                Log.d("TAG", "likePostId: $postId,$like")
                callCommunityLikeDislikeAPI(
                    post = postId,
                    like = like,
                    coroutineScope = coroutineScope,
                    context = context
                )
            },
            commentPostLikeDislike = { commentId, like ->
                callCommentLikeDislikeAPI(
                    commentId = commentId,
                    like = like,
                    coroutineScope = coroutineScope,
                    context = context
                )
            },
            screenName = this.screenName

        )
    }
    private fun callUserNotificationRedirectAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        communityId: String,
        navigate: (NavigationAction) -> Unit
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.userCommentRedirect(mainId = communityId, subId = "", type = 7).collect {
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
                        _commentData.value = it.data?.post ?: CommunityPostResponse()
                        commentList.value = it.data?.comments?: emptyList()
                        _commentData.value.commentCount = it.data?.comments?.size
                        Log.d("TAG", "IsLike:api ${it.data?.post?.like}")
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }
                    else -> {
                        showLoader.value = false
                    }
                }
            }
        }
    }

    private fun callCommunityLikeDislikeAPI(
        post: String,
        like: Boolean,
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
                        showLoader.value = true

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

                    else -> {
                        showLoader.value = false
                    }
                }
            }
        }
    }

    private fun callCommentLikeDislikeAPI(
        commentId: String,
        like: Boolean,
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        coroutineScope.launch {
            apiRepository.commentPostLikeDislike(commentId = commentId, like = like).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.data?.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true

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

                    else -> {
                        showLoader.value = false
                    }
                }
            }
        }
    }

    private fun callPostCommentAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        postId: String,
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.postComment(postId = postId, page = 1).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        noDataFound.value = false

                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "Something went wrong!"
                        )
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                        noDataFound.value = false

                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                        val fetchedComments = it.data?.data
                        if (!fetchedComments.isNullOrEmpty()) {
                            // Update `commentList` only if the list changes
                            val updatedList = fetchedComments.map { comment ->
                                comment.copy(
                                    message = comment.message ?: comment.text,
                                    profileImage = comment.profileImage,
                                    createdAt = comment.createdAt,
                                    like = comment.like,
                                    isLiked = comment.isLiked
                                )
                            }
                            commentList.value = updatedList
                        } else {
                            noDataFound.value = true
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        noDataFound.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }
                }
            }
        }
    }

    private fun makeAddCommentInReq(
        coroutineScope: CoroutineScope,
        post: String,
        message: String,
        context: Context,
        keyboard: SoftwareKeyboardController?
    ) {
        coroutineScope.launch {
            val addComment = AddCommentRequest(
                postId = post,
                message = message
            )
            callAddCommentAPI(addComment, coroutineScope, context = context, keyboard = keyboard)
        }
    }

    private fun callAddCommentAPI(
        userProfileRequest: AddCommentRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        keyboard: SoftwareKeyboardController?
    ) {
        coroutineScope.launch {
            apiRepository.addComment(userProfileRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false

                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        val newComment = it.data?.data
                        newComment?.let { comment ->
                            // Add the new comment dynamically
                            val updatedComments = commentList.value.toMutableList()
                            updatedComments.add(0, comment) // Add at the beginning
                            commentList.value = updatedComments
                            _commentData.value.commentCount = updatedComments.size
                            noDataFound.value = updatedComments.isEmpty()


                        }
                        commentFlow.value = ""
                        keyboard?.hide()
                        showSuccessMessage(context = context, message = it.data?.message ?: "")
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.data?.message ?: "UnAuthenticated"
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

    private fun showSuccessMessage(context: Context, message: String) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, false).show()
    }

    private fun showWarningMessage(context: Context, errorMsg: String) {
        Toasty.warning(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }


}