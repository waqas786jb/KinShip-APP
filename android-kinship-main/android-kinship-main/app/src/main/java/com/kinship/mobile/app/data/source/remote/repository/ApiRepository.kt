package com.kinship.mobile.app.data.source.remote.repository

import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.paging.ApiCallback

import com.kinship.mobile.app.model.domain.request.adoptedRequest.AdoptedRequest
import com.kinship.mobile.app.model.domain.request.babyRequest.BabyRequest
import com.kinship.mobile.app.model.domain.request.community.NewSuggestionReq
import com.kinship.mobile.app.model.domain.request.community.commentRequest.AddCommentRequest
import com.kinship.mobile.app.model.domain.request.conceiveRequest.ConceiveRequest
import com.kinship.mobile.app.model.domain.request.contactSupport.ContactSupportRequest
import com.kinship.mobile.app.model.domain.request.kinshipName.KinshipNameEditRequest
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpUpdateVerificationRequest
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpVerificationRequest

import com.kinship.mobile.app.model.domain.request.pregnantRequest.PregnantRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.ChangePasswordRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.EventStatusRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.IsLikeDislikeRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.LogOutRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.RefreshTokenRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.RegisterForPushRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.ResendOtpRequest
import com.kinship.mobile.app.model.domain.request.signUp.SignInRequest
import com.kinship.mobile.app.model.domain.request.signUp.logIn.LogInRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.NewPasswordOtpVerificationReq
import com.kinship.mobile.app.model.domain.request.userForgetPassword.NewPasswordRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.UserForgetPasswordRequest
import com.kinship.mobile.app.model.domain.request.userGroup.AddGroupMemberRequest
import com.kinship.mobile.app.model.domain.request.userProfileRequest.UserProfileRequest
import com.kinship.mobile.app.model.domain.request.versionRequest.KinshipVersionRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike.IsLikeDislikeResponse
import com.kinship.mobile.app.model.domain.response.chat.sendMessage.SendMessage
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.AddGroupMember
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.groupMember.UserGroupMember
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.model.domain.response.communities.communityPost.commentRedirect.CommentRedirectResponse
import com.kinship.mobile.app.model.domain.response.createEvent.CreateEventResponse
import com.kinship.mobile.app.model.domain.response.evenrName.EventNameData
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.hobbiesData.HobbiesData
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationListResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationSettingResponse
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiRepository {
    fun register(signInRequest: SignInRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    fun logIn(loginInRequest: LogInRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    fun otpVerification(otpVerificationRequest: OtpVerificationRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    fun otpUpdateContactVerification(otpVerificationRequest: OtpUpdateVerificationRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    fun resendOtp(resendOtpRequest: ResendOtpRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun forgetPassword(resendOtpRequest: ResendOtpRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun refreshToken(request: RefreshTokenRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>
    fun changePassword(changePasswordRequest: ChangePasswordRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun leaveKinship(reason: String): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun deleteAccount(reason: String): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun logout(logoutRequest: LogOutRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun conceiveRequest(conceiveRequest: ConceiveRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>
    fun pregnantRequest(conceiveRequest: PregnantRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    fun babyRequest(conceiveRequest: BabyRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>
    fun adoptedRequest(conceiveRequest: AdoptedRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>
    fun userProfileRequest(userProfileRequest: UserProfileRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    fun getHobbiesList(): Flow<NetworkResult<ApiResponseNew<HobbiesData>>>
    fun editProfile(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<ProfileData>>>
    fun editProfileWithImage(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<ProfileData>>>

    // fun editProfile(data : HashMap<String, RequestBody>, profileImage: MultipartBody.Part?, apiCallBack : ApiCallback<Response<ApiResponse<UserBean>>>)


    fun getCreateGroup(): Flow<NetworkResult<ApiResponse<CreateGroup>>>

    fun createWithImageEvent(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<CreateEventResponse>>>

    fun createEvent(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<CreateEventResponse>>>

    fun getMyEvents(): Flow<NetworkResult<ApiResponseNew<MyEventsData>>>

    fun getUpcomingEvents(): Flow<NetworkResult<ApiResponseNew<MyEventsData>>>

    fun getEvents(): Flow<NetworkResult<ApiResponseNew<MyEventsData>>>

    fun registerForPush(request: RegisterForPushRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun changeEventStatus(request: EventStatusRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun getNotificationList(): Flow<PagingData<NotificationListResponse>>

   // fun getContactList(accountId: String,searchContact: String): Flow<PagingData<DuoSearchCustomerResponse.ResponseData.DuoSearchCustomerV2.CustomerData>>

    fun notificationSetting(request: HashMap<String, String>): Flow<NetworkResult<ApiResponse<NotificationSettingResponse>>>


    // fun getKinshipGroupChatList(id: String, type: Int, imageLinkType: Int, search: String): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>>
    /*fun getKinshipGroupChatList(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String,
        page:Int,
        apiCallback: ApiCallback?
    ): Flow<KinshipGroupChatListData>*/
    fun getKinshipGroupChatList(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String,
        page: Int,
    ): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>>

    fun getKinshipGroupChatListWithPaging(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String,
        apiCallback: ApiCallback?
    ): Flow<PagingData<KinshipGroupChatListData>>

    fun sendMessage(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<SendMessage>>>

    fun isLikeDislike(isLikeDislike: IsLikeDislikeRequest): Flow<NetworkResult<ApiResponse<IsLikeDislikeResponse>>>
    fun getKinshipDataChatList(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String
    ): Flow<PagingData<KinshipGroupChatListData>>

    fun userFlag(): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun userGroupList(page:Int): Flow<NetworkResult<ApiResponseNew<MessageTabResponse>>>

    fun addGroupMemberRequest(addGroupMemberRequest: AddGroupMemberRequest): Flow<NetworkResult<ApiResponse<AddGroupMember>>>
    fun searchChat(
        id: String,
        groupId: String
    ): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>>

    fun groupMemberList(groupId: String): Flow<NetworkResult<ApiResponseNew<UserGroupMember>>>
    fun kinshipNameEdit(kinshipNameEditRequest: KinshipNameEditRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun getEventName(eventId: String, type: Int): Flow<NetworkResult<ApiResponseNew<EventNameData>>>
    fun eventDelete(eventId: String): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun editEvent(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<CreateEventResponse>>>

    fun editEventWithImage(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<CreateEventResponse>>>

    fun kinshipVersion(kinshipVersionRequest: KinshipVersionRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun contactSupport(contactSupportRequest: ContactSupportRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun userForgetPassword(userForgetPasswordRequest: UserForgetPasswordRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>
    fun newPassword(newPasswordRequest: NewPasswordRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun otpNewPasswordVerification(newPasswordOtpVerificationReq: NewPasswordOtpVerificationReq): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>>

    //communities
    fun myCommunities(search: String?): Flow<NetworkResult<ApiResponseNew<MyCommunitiesResponse>>>
    fun submitNewSuggestion(newSuggestionReq: NewSuggestionReq): Flow<NetworkResult<ApiResponse<Any>>>
    fun exploreCommunities(type:Int?,search: String?): Flow<NetworkResult<ApiResponseNew<MyCommunitiesResponse>>>
    fun communityPost(communityId:String,page:Int): Flow<NetworkResult<ApiResponseNew<CommunityPostResponse>>>

    fun addNewPostCommunity(
        req: Map<String, RequestBody>,
        communityPost: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<Any>>>

    fun addNewPostCommunityWithOutImage(
        req: Map<String, RequestBody>,
    ): Flow<NetworkResult<ApiResponse<Any>>>

    fun joinCommunity(communityId: String?): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun leaveCommunity(communityId: String?): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun postComment(postId: String?,page:Int): Flow<NetworkResult<ApiResponseNew<CommunityPostResponse>>>

    fun addComment(addCommentRequest: AddCommentRequest): Flow<NetworkResult<ApiResponse<CommunityPostResponse>>>

    fun communityPostLikeDislike(postId: String?,like:Boolean?): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun commentPostLikeDislike(commentId: String?,like:Boolean?): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun termAndCondition(): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun userNotificationRedirect(mainId:String,subId:String,type:Int): Flow<NetworkResult<ApiResponseNew<CommunityPostResponse>>>

    fun userCommentRedirect(mainId:String,subId:String,type:Int): Flow<NetworkResult<ApiResponse<CommentRedirectResponse>>>
    fun userKinshipGroupNotificationRedirect(mainId:String,subId:String,type:Int): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>>




}