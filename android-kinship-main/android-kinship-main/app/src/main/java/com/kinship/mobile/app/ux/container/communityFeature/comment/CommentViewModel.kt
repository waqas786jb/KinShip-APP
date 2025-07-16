package com.kinship.mobile.app.ux.container.communityFeature.comment

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.myCommunities.GetMyCommunitiesUiStateUseCase
import com.kinship.mobile.app.ux.container.communityFeature.myCommunities.MyCommunitiesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class CommentViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getCommentUiStateUseCase: GetCommentUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val commentData: String =
        savedStateHandle.get<String>(CommentRoute.Arg.COMMUNITY_DATA) ?: ""
    private val screenName: String =
        savedStateHandle.get<String>(CommentRoute.Arg.SCREEN_NAME) ?: ""
    val uiState: CommentUiState = getCommentUiStateUseCase(context = context, coroutineScope = viewModelScope, commentData = commentData, screenName = screenName) { navigate(it)

    }
}