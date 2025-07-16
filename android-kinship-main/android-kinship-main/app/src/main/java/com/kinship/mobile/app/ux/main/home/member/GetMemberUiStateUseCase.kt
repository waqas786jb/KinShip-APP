package com.kinship.mobile.app.ux.main.home.member

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.userGroup.AddGroupMemberRequest
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.main.home.profile.ProfileRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetMemberUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val showLoader = MutableStateFlow(false)
    private var selectedUsers = mutableListOf<String>()
    private val groupMemberList = MutableStateFlow<List<GroupMember>>(emptyList())
    private var roomId: String = ""
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MemberUiState {
        Log.d("TAG", "invoke: $roomId")
        return MemberUiState(
            memberList = getCreateGroupData(coroutineScope)?.groupMembers?.toList() ?: emptyList(),
            kinshipName = getCreateGroupData(coroutineScope)?.groupName.toString(),
            memberCount = getCreateGroupData(coroutineScope)?.count.toString(),
            onProfileClickButton = {
                storeResponseToDataStore(
                    context = context,
                    coroutineScope = coroutineScope,
                    navigate = navigate,
                    userAuthResponseData = it
                )
            },
            groupMemberList = groupMemberList,
            userId = getUserData(coroutineScope)?._id ?: "",
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            groupMemberAPICall = {
                callGetGroupInAPI(coroutineScope = coroutineScope, context = context)
            },
            showLoader = showLoader,
            navigateToSingleGroupChat = {
                if (it.chatGroupId.isNullOrEmpty()) {
                    makeAddGroupMemberInReq(context, coroutineScope, navigate, it)
                } else {
                    navigateToSingleGroupChat(
                        context,
                        navigate,
                        Constants.ContainerScreens.SINGLE_USER_CHAT_SCREEN,
                        it,
                        id = null
                    )
                }
            }
        )
    }
    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }
    private fun callGetGroupInAPI(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            apiRepository.getCreateGroup().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                        showLoader.value = false
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        groupMemberList.value = it.data?.data?.groupMembers ?: emptyList()
                        showLoader.value = false
                    }

                    is NetworkResult.UnAuthenticated -> {
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                        showLoader.value = false
                    }
                }
            }
        }
    }

    private fun storeResponseToDataStore(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: GroupMember?
    ) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.saveProfileData(it)
                navigate(NavigationAction.Navigate(ProfileRoute.createRoute()))
            }
        }
    }

    private fun makeAddGroupMemberInReq(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        groupMember: GroupMember,
    ) {
        coroutineScope.launch {
            // Fetch the current user's ID (self user)
            val currentUserId = getUserData(coroutineScope)?._id ?: ""
            // Ensure the group member's ID is not the self user's ID
            if (groupMember.userId != currentUserId) {
                selectedUsers.add(groupMember.userId)
            }
            val addGroupMember = AddGroupMemberRequest(
                userId = selectedUsers.distinct() // Ensure only unique user IDs are sent
            )
            callAddMemberAPI(addGroupMember, coroutineScope, context, navigate, groupMember)
        }
    }

    private fun callAddMemberAPI(
        addGroupMember: AddGroupMemberRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        groupMember: GroupMember
    ) {
        coroutineScope.launch {
            apiRepository.addGroupMemberRequest(addGroupMember).collect {
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
                        navigateToSingleGroupChat(
                            context,
                            navigate,
                            Constants.ContainerScreens.SINGLE_USER_CHAT_SCREEN,
                            groupMember,
                            it.data?.data?.id.toString()
                        )
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }

    private fun navigateToSingleGroupChat(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        groupMember: GroupMember,
        id: String?,
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.IS_COME_FOR, Constants.AppScreen.MEMBER_SCREEN)
        bundle.putString(Constants.BundleKey.MEMBER_RESPONSE, Gson().toJson(groupMember))
        bundle.putString(Constants.BundleKey.CHAT_ID, id)
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }
}