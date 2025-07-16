package com.kinship.mobile.app.data.source.remote.repository

import com.kinship.mobile.app.data.source.remote.EndPoints
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface ApiServices {
    @POST(EndPoints.Auth.SIGN_UP)
    suspend fun doSignIn(
        @Body signInRequest: SignInRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.Auth.LOG_IN)
    suspend fun logInAsync(
        @Body logInRequest: LogInRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.Auth.OTP_VERIFICATION)
    suspend fun otpVerificationAsync(
        @Body logInRequest: OtpVerificationRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.Auth.OTP_VERIFICATION)
    suspend fun otpUpdateContactVerificationAsync(
        @Body logInRequest: OtpUpdateVerificationRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.Auth.RESET_OTP)
    suspend fun resendOtpAsync(
        @Body resendOtpRequest: ResendOtpRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.User.FORGET_PASSWORD)
    suspend fun forgetPasswordAsync(
        @Body resendOtpRequest: ResendOtpRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.Auth.REFRESH_TOKEN)
    suspend fun refreshTokenAsync(
        @Body refreshToken: RefreshTokenRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.User.CHANGE_PASSWORD)
    suspend fun changePasswordAsync(
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.User.LEAVE_KINSHIP)
    suspend fun leaveKinshipAsync(
        @Query("reason") reason: String
    ): Response<ApiResponse<MessageResponse>>

    @DELETE(EndPoints.User.DELETE_KINSHIP_USER)
    suspend fun deleteKinshipAsync(
        @Query("reason") reason: String
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.Auth.LOGOUT)
    suspend fun logoutAsync(
        @Body logoutRequest: LogOutRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.User.USER_PROFILE)
    suspend fun conceiveQuestionFlowAsync(
        @Body conceiveRequest: ConceiveRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.User.USER_PROFILE)
    suspend fun pregnantQuestionFlowAsync(
        @Body pregnantRequest: PregnantRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.User.USER_PROFILE)
    suspend fun babyQuestionFlowAsync(
        @Body babyRequest: BabyRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.User.USER_PROFILE)
    suspend fun adoptedQuestionFlowAsync(
        @Body adoptedRequest: AdoptedRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @POST(EndPoints.User.USER_PROFILE)
    suspend fun userProfileAsync(
        @Body userProfileRequest: UserProfileRequest
    ): Response<ApiResponse<UserAuthResponseData>>

    @GET(EndPoints.Auth.GET_HOBBIES)
    suspend fun getHobbiesAsync(): Response<ApiResponseNew<HobbiesData>>

    @PUT(EndPoints.User.EDIT_USER_PROFILE)
    @Multipart
    suspend fun editProfileAsync(@PartMap req: Map<String, @JvmSuppressWildcards RequestBody>): Response<ApiResponse<ProfileData>>

    @PUT(EndPoints.User.EDIT_USER_PROFILE)
    @Multipart
    suspend fun editProfileWithImageAsync(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<ProfileData>>


    @GET(EndPoints.Auth.CREATE_GROUP)
    suspend fun getGroup(): Response<ApiResponse<CreateGroup>>

    @POST(EndPoints.Event.CREATE_EVENT)
    @Multipart
    suspend fun createEventWithImageAsync(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<CreateEventResponse>>

    @POST(EndPoints.Event.CREATE_EVENT)
    @Multipart
    suspend fun createEventAsync(@PartMap req: Map<String, @JvmSuppressWildcards RequestBody>): Response<ApiResponse<CreateEventResponse>>

    @GET(EndPoints.Event.MY_EVENTS)
    suspend fun getMyEvents(): Response<ApiResponseNew<MyEventsData>>

    @GET(EndPoints.Event.UPCOMING_EVENTS)
    suspend fun getUpcomingEvents(): Response<ApiResponseNew<MyEventsData>>

    @GET(EndPoints.Event.EVENTS)
    suspend fun getEvents(): Response<ApiResponseNew<MyEventsData>>

    @POST(EndPoints.User.REGISTER_FOR_PUSH)
    suspend fun registerForPush(@Body request: RegisterForPushRequest): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.Event.EVENTS_STATUS)
    suspend fun changeEventStatus(@Body request: EventStatusRequest): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.User.NOTIFICATION_LISTING)
    suspend fun getNotificationListing(
        @Query("page") page: Int?,
        @Query("perPage") perPage: Int?
    ): Response<ApiResponseNew<NotificationListResponse>>

    @POST(EndPoints.User.NOTIFICATION_SETTINGS)
    suspend fun notificationSettings(@Body request: HashMap<String, String>): Response<ApiResponse<NotificationSettingResponse>>

    @GET(EndPoints.Chat.CHAT_LIST_FOR_KINSHIP_GROUP)
    suspend fun getChatListForKinshipGroup(
        @Query("id") id: String,
        @Query("type") type: Int,
        @Query("imageLinkType") imageLinkType: Int,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<KinshipGroupChatListData>>

    @POST(EndPoints.Chat.SEND_MESSAGE)
    @Multipart
    suspend fun sendMessage(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<SendMessage>>

    @POST(EndPoints.Chat.IS_LIKE_DISLIKE)
    suspend fun isLikeDislike(
        @Body isLikeDislikeRequest: IsLikeDislikeRequest
    ): Response<ApiResponse<IsLikeDislikeResponse>>


    @GET(EndPoints.Chat.GET_KINSHIP_DATA_CHAT_LIST)
    suspend fun getKinshipChatListData(
        @Query("id") id: String,
        @Query("type") type: Int,
        @Query("imageLinkType") imageLinkType: Int,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<KinshipGroupChatListData>>

    @GET(EndPoints.Chat.USER_FLAG)
    suspend fun userFlag(
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Chat.CHAT_USER_GROUP_LIST)
    suspend fun getUserGroupList(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<MessageTabResponse>>

    @POST(EndPoints.Chat.ADD_GROUP_MEMBER)
    suspend fun addGroupMember(
        @Body groupRequest: AddGroupMemberRequest
    ): Response<ApiResponse<AddGroupMember>>

    @GET(EndPoints.Chat.SEARCH_CHAT)
    suspend fun getSearchChat(
        @Query("id") id: String,
        @Query("groupId") groupId: String
    ): Response<ApiResponseNew<KinshipGroupChatListData>>

    @GET(EndPoints.Chat.GROUP_MEMBER)
    suspend fun userGroupMember(
        @Query("groupId") groupId: String
    ): Response<ApiResponseNew<UserGroupMember>>

    @POST(EndPoints.User.KINSHIP_NAME_EDIT)
    suspend fun kinshipNameEdit(
        @Body kinshipNameEditRequest: KinshipNameEditRequest
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Event.EVENT_NAME)
    suspend fun eventName(
        @Query("eventId") eventId: String,
        @Query("type") type: Int,
    ): Response<ApiResponseNew<EventNameData>>

    @DELETE(EndPoints.Event.EVENT_DELETE)
    //delete api request body pass
    // @HTTP(method = "DELETE", path = EndPoints.Event.EVENT_DELETE, hasBody = true)
    suspend fun eventDelete(
        @Query("eventId") eventId: String
    ): Response<ApiResponse<MessageResponse>>

    @PUT(EndPoints.Event.EDIT_EVENT)
    @Multipart
    suspend fun editEventWithImage(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<CreateEventResponse>>

    @PUT(EndPoints.Event.EDIT_EVENT)
    @Multipart
    suspend fun editEventAsync(@PartMap req: Map<String, @JvmSuppressWildcards RequestBody>): Response<ApiResponse<CreateEventResponse>>

    @POST(EndPoints.Version.VERSION)
    suspend fun kinshipVersion(
        @Body versionRequest: KinshipVersionRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.User.USER_CONTACT_SUPPORT)
    suspend fun contactSupport(
        @Body contactSupportRequest: ContactSupportRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.User.FORGET_PASSWORD_USER)
    suspend fun userForgetPassword(
        @Body userForgetPasswordRequest: UserForgetPasswordRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.User.FORGET_PASSWORD_USER)
    suspend fun newPassword(
        @Body newPassword: NewPasswordRequest
    ): Response<ApiResponse<MessageResponse>>

    @POST(EndPoints.Auth.OTP_VERIFICATION)
    suspend fun newPasswordOtpVerification(
        @Body newPasswordOtpVerification: NewPasswordOtpVerificationReq
    ): Response<ApiResponse<UserAuthResponseData>>

    /**communities flow apis*/
    @GET(EndPoints.Communities.MY_COMMUNITIES)
    suspend fun getCommunitiesList(
        @Query("search") search: String?
    ): Response<ApiResponseNew<MyCommunitiesResponse>>

    @POST(EndPoints.Communities.NEW_SUGGESTION)
    suspend fun submitNewSuggestion(
        @Body newSuggestionReq: NewSuggestionReq
    ): Response<ApiResponse<Any>>

    @GET(EndPoints.Communities.EXPLORE_COMMUNITY)
    suspend fun getExploreCommunitiesList(
        @Query("type") type: Int?,
        @Query("search") search: String?
    ): Response<ApiResponseNew<MyCommunitiesResponse>>

    @GET(EndPoints.Communities.COMMUNITY_POST_LIST)
    suspend fun getCommunityPost(
        @Query("communityId") communityId: String?,
        @Query("page") page: Int?,
        @Query("perPage") perPage: Int?
    ): Response<ApiResponseNew<CommunityPostResponse>>

    @POST(EndPoints.Communities.ADD_NEW_POST)
    @Multipart
    suspend fun addNewPostCommunity(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part communityPost: MultipartBody.Part?
    ): Response<ApiResponse<Any>>

    @POST(EndPoints.Communities.ADD_NEW_POST)
    @Multipart
    suspend fun addNewPostCommunityWithOutImage(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<ApiResponse<Any>>

    @GET(EndPoints.Communities.JOIN_COMMUNITY)
    suspend fun joinCommunity(
        @Query("communityId") communityId: String?
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Communities.LEAVE_COMMUNITY)
    suspend fun leaveCommunity(
        @Query("communityId") communityId: String?
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Communities.POST_COMMENT)
    suspend fun postComment(
        @Query("postId") postId: String?,
        @Query("page") page: Int?,
        @Query("perPage") perPage: Int?,
    ): Response<ApiResponseNew<CommunityPostResponse>>

    @POST(EndPoints.Communities.ADD_COMMENT)
    suspend fun addComment(
        @Body addCommentRequest: AddCommentRequest
    ): Response<ApiResponse<CommunityPostResponse>>

    @GET(EndPoints.Communities.COMMUNITY_POST_LIKE_DISLIKE)
    suspend fun communityPostLikeDislike(
        @Query("postId") postId: String?,
        @Query("like") like: Boolean?
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Communities.COMMENT_POST_LIKE_DISLIKE)
    suspend fun commentPostLikeDislike(
        @Query("commentId") commentId: String?,
        @Query("like") like: Boolean?
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Communities.TERM_AND_CONDITION)
    suspend fun termAndCondition(
    ): Response<ApiResponse<MessageResponse>>

    @GET(EndPoints.Communities.USER_NOTIFICATION_NOTIFICATION_REDIRECT)
    suspend fun userNotificationRedirect(
        @Query("mainId") mainId: String?,
        @Query("subId") subId: String?,
        @Query("type") type: Int
    ): Response<ApiResponseNew<CommunityPostResponse>>

    @GET(EndPoints.Communities.USER_NOTIFICATION_NOTIFICATION_REDIRECT)
    suspend fun userCommentRedirect(
        @Query("mainId") mainId: String?,
        @Query("subId") subId: String?,
        @Query("type") type: Int
    ): Response<ApiResponse<CommentRedirectResponse>>

    @GET(EndPoints.Communities.USER_NOTIFICATION_NOTIFICATION_REDIRECT)
    suspend fun userKinshipGroupNotificationRedirect(
        @Query("mainId") mainId: String?,
        @Query("subId") subId: String?,
        @Query("type") type: Int
    ): Response<ApiResponseNew<KinshipGroupChatListData>>




}