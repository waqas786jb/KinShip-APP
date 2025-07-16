//
//  Response.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation
import ObjectMapper

class Response: Mappable {
    
    var success: String?
    var error: String?
    var message: String?
    var logInResponse: LogInResponse?
    var profileResponse: Profile?
    //    var registerResponse: RegisterResponse?
    var forgotPasswordResponse: ForgotPasswordResponse?
    //    var otpVerificationResponse: OTPVerificationResponse?
    var resendOtpVarificationResponse: ResendOtpVarificationResponse?
    var addProfileResponse: AddProfileResponse?
    var aboutConceiveResponse: AboutConceiveResponse?
    var hobbiesResponse: [HobbiesResponse]?
    var logOutResponse: LogOutResponse?
    var homePageResponse: HomePageResponse?
    var getMyEventResponse: [GetMyEventResponse]?
    var getMyUpcomingEventResponse: [GetMyUpcomingEventResponse]?
    var createEventResponse: CreateEventResponse?
    var notificationResponse: [NotificationResponse]?
    var notificationMetaResponse: NotificationMetaResponse?
    var getNotificationSettingResponse: GetNotificationSettingResponse?
    var notificationSettingResponse: NotificationSettingResponse?
    var allEventsResponse: [AllEventResponse]?
    var userGroupListResponse: [UserGroupListResponse]?
    var userGroupListMetaResponse: UserGroupListMetaResponse?
    var chatImageLinkMessageResponse: [ChatImageLinkMessageResponse]?
    var chatImageLinkMessageMetaResponse: ChatImageLinkMessageMetaResponse?
    var photosSendResponse: PhotosSendResponse?
    var likeMessageResponse: LikeMessageResponse?
    var subgroupResponse: SubgroupResponse?
    var flag: Bool?
    var searchMessageDisplayResponse: [SearchMessageDisplayResponse]?
    var rsvpNameResponse: [RSVPNameResponse]?
    var subGroupMembersResponse: [SubGroupMembersResponse]?
    var subGroupCreateResponse: SubGroupCreateResponse?
    var versionCheckResponse: Int?
    var myCommunityResponse: [MyCommunityResponse]?
    var communityListingData: [CommunityListingData]?
    var communityListingMeta: CommunityListingMeta?
    var commentListingData: [CommentListingData]?
    var commentListingMeta: CommentListingMeta?
    var commentListingData2: CommentListingData2?
    var exploreCommunity: [ExploreCommunity]?
    var addPostResponse: AddPostResponse?
    var postResponse: CommunityListingData?
    var commentResponse: [CommentListingData]?
    var grpObj: GrpObj?
    var termsAndConditions: TermsAndConditions?
    
    required init?(map: ObjectMapper.Map) {
    }
    
    func mapping(map: ObjectMapper.Map) {
        message <- map["message"]
        success <- map["success"]
        error <- map["error"]
        logInResponse <- map["data"]
        //        registerResponse <- map["data"]
        forgotPasswordResponse <- map["data"]
        //        otpVerificationResponse <- map["data"]
        addProfileResponse <- map["data"]
        aboutConceiveResponse <- map["data"]
        hobbiesResponse <- map["data"]
        homePageResponse <- map["data"]
        profileResponse <- map["data"]
        logOutResponse <- map["data"]
        getMyEventResponse <- map["data"]
        getMyUpcomingEventResponse <- map["data"]
        createEventResponse <- map["data"]
        notificationResponse <- map["data"]
        notificationMetaResponse <- map["meta"]
        notificationSettingResponse <- map["data"]
        getNotificationSettingResponse <- map["data"]
        allEventsResponse <- map["data"]
        userGroupListResponse <- map["data"]
        userGroupListMetaResponse <- map["meta"]
        chatImageLinkMessageResponse <- map["data"]
        chatImageLinkMessageMetaResponse <- map["meta"]
        photosSendResponse <- map["data"]
        likeMessageResponse <- map["data"]
        flag <- map["flag"]
        searchMessageDisplayResponse <- map["data"]
        rsvpNameResponse <- map["data"]
        subGroupMembersResponse <- map["data"]
        subGroupCreateResponse <- map["data"]
        versionCheckResponse <- map["message"]
        myCommunityResponse <- map["data"]
        communityListingData <- map["data"]
        communityListingMeta <- map["meta"]
        commentListingData <- map["data"]
        commentListingData2 <- map["data"]
        commentListingMeta <- map["meta"]
        exploreCommunity <- map["data"]
        addPostResponse <- map["data"]
        postResponse <- map["post"]
        commentResponse <- map["comments"]
        grpObj <- map["grpObj"]
        termsAndConditions <- map["data"]
    }
    
}
