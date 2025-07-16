package com.kinship.mobile.app.ux.container.newMessage
import android.content.Context
import android.widget.Toast
import androidx.paging.PagingData
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.userGroup.AddGroupMemberRequest
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetNewMessageUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val apiUserGroupList =
        MutableStateFlow<PagingData<MessageTabResponse>>(
            PagingData.empty()
        )
    private val showLoader = MutableStateFlow(false)
    private var selectedUsers = mutableListOf<String>()
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MessageNewMessageUiState {

        return MessageNewMessageUiState(
            apiUserGroupListResultFlow = apiUserGroupList,
            memberList = getCreateGroupData(coroutineScope)?.groupMembers?.toList() ?: emptyList(),
            onBackClick =  {
                popBackToLoginScreen(navigate)
                           /*navigateToBottomNavigationScreen(
                context,
                navigate,
                Constants.ContainerScreens.MESSAGE_SCREEN
            )*/},
            showLoader = showLoader,
            addMemberGroupAPICall = {
                if (selectedUsers.isEmpty()) {
                    Toasty.warning(
                        context,
                        "Please select at least one user",
                        Toast.LENGTH_SHORT,
                        false
                    ).show()

                } else {
                    makeAddGroupMemberInReq(context, coroutineScope, navigate)
                }
            },
            userId = getUserData(coroutineScope)?._id?:"",

            onUserList = { list ->
                if (list.isAdd) {
                    selectedUsers.add(list.id)
                } else {
                    selectedUsers.remove(list.id)
                }
            }
        )
    }
    private fun popBackToLoginScreen(navigate: (NavigationAction) -> Unit) {
        navigate(NavigationAction.PopIntent)
    }

    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }

    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }
    private fun makeAddGroupMemberInReq(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val addGroupMember = AddGroupMemberRequest(
                userId = selectedUsers
            )
            callAddMemberAPI(addGroupMember, coroutineScope, context, navigate)
        }
    }
    private fun callAddMemberAPI(
        addGroupMember: AddGroupMemberRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.addGroupMemberRequest(addGroupMember).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                        if (it.data?.data?.userId?.size == 2) {
                            Toasty.success(
                                context,
                                context.getString(R.string.member_added_successfully),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        } else {
                            Toasty.success(
                                context,
                                context.getString(R.string.group_created_successfully),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                        popBackToLoginScreen(navigate)
                       /* navigateToBottomNavigationScreen(
                            context,
                            navigate,
                            Constants.ContainerScreens.MESSAGE_SCREEN
                        )*/
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                }
            }
        }
    }
}