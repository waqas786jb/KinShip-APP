package com.kinship.mobile.app.ux.main.home.profile
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
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.ux.container.ContainerActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetProfileUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {


    private var selectedUsers = mutableListOf<String>()
    private val showLoader = MutableStateFlow(false)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ProfileUiState {
        return ProfileUiState(
            navigateToSingleGroupChat = {
                if (getCreateGroupData(coroutineScope)?.chatGroupId.isNullOrEmpty()){
                    getCreateGroupData(coroutineScope)?.let {
                        makeAddGroupMemberInReq(context, coroutineScope, navigate,
                            it
                        )
                    }
                }else{
                    getCreateGroupData(coroutineScope)?.let {
                        navigateToSingleGroupChat(
                            context,
                            navigate,
                            Constants.ContainerScreens.SINGLE_USER_CHAT_SCREEN,
                            it,
                            null
                        )
                    }
                }
            },

            memberUserId = getCreateGroupData(coroutineScope)?.userId?:"",
            userId = getUserData(coroutineScope)?._id?:"",
            showLoader = showLoader,
            memberName = getCreateGroupData(coroutineScope)?.firstName?.plus(" ").plus(getCreateGroupData(coroutineScope)?.lastName),
            memberProfile =getCreateGroupData(coroutineScope)?.profileImage?:"" ,
            memberCite = getCreateGroupData(coroutineScope)?.city?:"",
            memberBirthDay = AppUtils.convertMemberDateFormat(getCreateGroupData(coroutineScope)?.dateOfBirth?.toLong() ?: 0),
            memberBio =getCreateGroupData(coroutineScope)?.bio?:"" ,
            onBackClick = {
                navigate(NavigationAction.Pop())
            }
        )
    }
    private fun makeAddGroupMemberInReq(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        groupMember: GroupMember,

        ) {
        coroutineScope.launch {
            selectedUsers.add(groupMember.userId)
            val addGroupMember = AddGroupMemberRequest(
                userId =selectedUsers
            )
            Log.d("TAG", "makeAddGroupMemberInReq: $selectedUsers")
            callAddMemberAPI(addGroupMember, coroutineScope, context, navigate,groupMember)
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
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
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
                            it.data?.data?.id
                        )
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
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
        chatId: String?
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.IS_COME_FOR, Constants.AppScreen.MEMBER_SCREEN)
        bundle.putString(Constants.BundleKey.MEMBER_RESPONSE, Gson().toJson(groupMember))
        bundle.putString(Constants.BundleKey.CHAT_ID,chatId )
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }




    private fun getCreateGroupData(coroutineScope: CoroutineScope): GroupMember? {
        var data: GroupMember? = null
        coroutineScope.launch { data = appPreferenceDataStore.getProfileData() }
        return data
    }
    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }

}