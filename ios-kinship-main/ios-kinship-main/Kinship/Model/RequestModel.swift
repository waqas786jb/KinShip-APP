//
//  RequestModel.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation
//import AlamofireObjectMapper
import ObjectMapper

//MARK: - LOGIN
class LoginRequest: Mappable {
    
    var email: String?
    var password: String?
    
    init(email: String, password: String) {
        self.email = email
        self.password = password
    }
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        email <- map["email"]
        password <- map["password"]
    }
    
}

//MARK: - REGISTE
class RegisterRequest: Mappable {
    var email: String?
    var password: String?
    var conformPassword: String?
    
    init(email: String, password: String, conformPassword: String) {
        self.email = email
        self.password = password
        self.conformPassword = conformPassword
    }
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        email <- map["email"]
        password <- map["password"]
        conformPassword <- map["confirmPassword"]
    }
}

//MARK: - FORGOT PASSWORD
class ForgotPasswordRequest: Mappable{
    
    var email: String?
    
    init(email: String) {
        self.email = email
    }
    
    required init?(map: ObjectMapper.Map) {
    }
    
    func mapping(map: ObjectMapper.Map) {
        email <- map["email"]
    }
}

//MARK: - OTP VERIFICATION

class OTPVerificationRequest: Mappable {
    var email: String?
    var otp:  String?
    var type: String?
    var oldEmail: String?
    
    init(email: String? = nil, otp: String? = nil, type: String? = nil, oldEmail: String? = nil){
        self.email = email
        self.otp = otp
        self.type = type
        self.oldEmail = oldEmail
    }
    required init?(map: ObjectMapper.Map) {
    }
    
    func mapping(map: ObjectMapper.Map) {
        email <- map["email"]
        otp <- map["otp"]
        type <- map["type"]
        oldEmail <- map["oldEmail"]
    }
}

//MARK: - RESEND OTP VARIFICATION

class ResendOtpVarification: Mappable {
    var email: String?
    
    init(email: String){
        self.email = email
    }
    
    required init?(map: ObjectMapper.Map) {
    }
    
    func mapping(map: ObjectMapper.Map) {
        email <- map["email"]
    }
}

//MARK: - PROFILE

class ProfileRequest: Mappable {

    var step: Int?
    var kinshipReason: Int?
    var howLongYouAreTrying: Int?
    var howYouTrying: Int?
    var whenIsYourDueDate: String?
    var singleOrMultiplePregnancy: Int?
    var singleGender: Int?
    var multipleGender: Int?
    var singleOrMultipleBirth: Int?
    var babyBornDate: [String]?
    var childHasSpecialNeed: Int?
    var firstTimeMom: Int?
    var firstName: String?
    var lastName: String?
    var dateOfBirth: String?
    var countrycode: String?
    var phoneNumber: String?
    var zipcode: Int?
    var lat: String?
    var long: String?
    var city: String?
    var hobbies: [String]?

    init(step: Int? = nil, kinshipReason: Int? = nil, howLongYouAreTrying: Int? = nil, howYouTrying: Int? = nil, whenIsYourDueDate: String? = nil, singleOrMultiplePregnancy: Int? = nil, singleGender: Int? = nil, multipleGender: Int? = nil, singleOrMultipleBirth: Int? = nil, babyBornDate: [String]? = nil, childHasSpecialNeed: Int? = nil, firstTimeMom: Int? = nil, firstName: String? = nil, lastName: String? = nil, dateOfBirth: String? = nil, countrycode: String? = nil, phoneNumber: String? = nil, zipcode: Int? = nil, lat: String? = nil, long: String? = nil,city: String? = nil, hobbies: [String]? = nil) {
        
        self.step = step
        self.kinshipReason = kinshipReason
        self.howLongYouAreTrying = howLongYouAreTrying
        self.howYouTrying = howYouTrying
        self.whenIsYourDueDate = whenIsYourDueDate
        self.singleOrMultiplePregnancy = singleOrMultiplePregnancy
        self.singleGender = singleGender
        self.multipleGender = multipleGender
        self.singleOrMultipleBirth = singleOrMultipleBirth
        self.babyBornDate = babyBornDate
        self.childHasSpecialNeed = childHasSpecialNeed
        self.firstTimeMom = firstTimeMom
        self.firstName = firstName
        self.lastName = lastName
        self.dateOfBirth = dateOfBirth
        self.countrycode = countrycode
        self.phoneNumber = phoneNumber
        self.zipcode = zipcode
        self.lat = lat
        self.long = long
        self.city = city
        self.hobbies = hobbies
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        step <- map["step"]
        kinshipReason <- map["kinshipReason"]
        howLongYouAreTrying <- map["howLongYouAreTrying"]
        howYouTrying <- map["howYouTrying"]
        whenIsYourDueDate <- map["whenIsYourDueDate"]
        singleOrMultiplePregnancy <- map["singleOrMultiplePregnancy"]
        singleGender <- map["singleGender"]
        multipleGender <- map["multipleGender"]
        singleOrMultipleBirth <- map["singleOrMultipleBirth"]
        babyBornDate <- map["babyBornDate"]
        childHasSpecialNeed <- map["childHasSpecialNeed"]
        firstTimeMom <- map["firstTimeMom"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        dateOfBirth <- map["dateOfBirth"]
        countrycode <- map["countrycode"]
        phoneNumber <- map["phoneNumber"]
        zipcode <- map["zipcode"]
        lat <- map["lat"]
        long <- map["long"]
        city <- map["city"]
        hobbies <- map["hobbies"]
    }
}

// MARK: - LOGOUT REQUEST
class LogOutRequest: Mappable {

    var deviceId: String?
//    var step: String?
    
    init(deviceId: String){
        self.deviceId = deviceId
//        self.step = step
    }
    required init?(map: Map){
    }

    func mapping(map: Map) {
        deviceId <- map["deviceId"]
    }
}

//  MARK: - EDIT PROFILE
class EditProfileRequest: Mappable {
    
    var firstName: String?
    var lastName: String?
    var city: String?
    var childBirthday: String?
    var bio: String?
    var email: String?
    var countryCode: String?
    var mobileNo: String?
    
    init(firstName: String? = nil, lastName: String? = nil, city: String? = nil, childBirthday: String? = nil, bio: String? = nil, email: String? = nil, mobileNo: String? = nil, countryCode: String? = nil) {
        self.firstName = firstName
        self.lastName = lastName
        self.city = city
        self.childBirthday = childBirthday
        self.bio = bio
        self.email = email
        self.countryCode = countryCode
        self.mobileNo = mobileNo
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        city <- map["city"]
        childBirthday <- map["editBabyBornDate"]
        bio <- map["bio"]
        email <- map["email"]
        mobileNo <- map["phoneNumber"]
        countryCode <- map["countrycode"]
    }
}

//MARK: - CHANGE PASSWORD
class ChangePasswordRequest: Mappable {
    var oldPassword: String?
    var password: String?
    var confirmPassword: String?
    
    init(oldPassword: String?, password: String?, conformPassword: String?) {
        self.oldPassword = oldPassword
        self.password = password
        self.confirmPassword = conformPassword
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        oldPassword <- map["oldPassword"]
        password <- map["password"]
        confirmPassword <- map["confirmPassword"]
    }
}


// MARK: - LEAVE KINSHIP
class LeaveKinshipRequest: Mappable {
    var reason: String?
    
    init(reason: String?){
        self.reason = reason
    }
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        reason <- map["reason"]
    }
}


class NotificationRequest: Mappable {
    var allNewPosts: Bool?
    var directMessage: Bool?
    var newEvents: Bool?
    
    init(allNewPosts: Bool, directMessage: Bool, newEvents: Bool){
        self.allNewPosts = allNewPosts
        self.directMessage = directMessage
        self.newEvents = newEvents
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        allNewPosts <- map["allNewPosts"]
        directMessage <- map["directMessage"]
        newEvents <- map["newEvents"]
    }
}

class CreateEventRequest :Mappable{
    var eventId: String?
    var eventName : String?
    var eventDate: String?
    var startTime : String?
    var endTime : String?
    var isAllDay : String?
    var link : String?
    var location : String?
    var lat : String?
    var long : String?
    var eventDescription : String?
    
    init(eventId : String? = nil, eventName : String? = nil, eventDate: String? = nil, startTime : String? = nil, endTime : String? = nil, isAllDay : String? = nil, link : String? = nil, location : String? = nil, eventDescription : String? = nil,lat : String? = nil, long : String? = nil){
        self.eventId = eventId
        self.eventName = eventName
        self.eventDate = eventDate
        self.startTime = startTime
        self.endTime = endTime
        self.isAllDay = isAllDay
        self.link = link
        self.location = location
        self.lat = lat
        self.long = long
        self.eventDescription = eventDescription
    }
    
    required init?(map: ObjectMapper.Map) {
        
    }
    
    func mapping(map: ObjectMapper.Map) {
        eventId <- map["eventId"]
        eventName <- map["eventName"]
        eventDate <- map["eventDate"]
        startTime <- map["startTime"]
        endTime <- map["endTime"]
        isAllDay <- map["isAllDay"]
        link <- map["link"]
        location <- map["location"]
        lat <- map["lat"]
        long <- map["long"]
        eventDescription <- map["eventDescription"]
    }
}

// MARK: - EVENT STATUS UPDATE
class StatusUpdateRequest: Mappable {
   
    var eventID: String?
    var status: Int?
    
    init(eventID: String? = nil, status: Int? = nil){
        self.eventID = eventID
        self.status = status
    }
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        eventID <- map["eventId"]
        status <- map["status"]
    }
}

//MARK: - CHT IMAGE LINK MESSAGE LIST

class ChatImageLinkMessageList: Mappable{
  
    var id: String?
    var type: Int?
    var imageLinkType: Int?
    var search: String?
    var page: String?
    var prePage: String?
    
    init(id: String?, type: Int?, imageLinkType: Int?, search: String? = nil, page: String? = nil, perPage: String? = nil){
        self.id = id
        self.type = type
        self.imageLinkType = imageLinkType
        self.search = search
        self.page = page
        self.prePage = perPage
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        id <- map["id"]
        type <- map["type"]
        imageLinkType <- map["imageLinkType"]
        search <- map["search"]
        page <- map["page"]
        prePage <- map["perPage"]
    }
}

// MARK: - PHOTO SEND
class photosSendRequest: Mappable{
    
    var groupId: String?
    var receiverId: String?
    var type: String?
    var message: String?
    
    init(groupId: String? = nil, receiverId: String? = nil, type: String? = nil, message: String? = nil){
        self.groupId = groupId
        self.receiverId = receiverId
        self.type = type
        self.message = message
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        groupId <- map["groupId"]
        receiverId <- map["receiverId"]
        type <- map["type"]
        message <- map["message"]
    }
}

//  MARK: - LIKE MESSAGE RESPONSE
class LikeMessageRequest: Mappable {
    
    var messageId: String?
    
    init(messageId: String?) {
        self.messageId = messageId
    }
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        messageId <- map["messageId"]
    }
}

//MARK: - LIKE MESSAGE LIST RESPONSE
class LikeMessageLinkResponse: Mappable{
  
    var id: String?
    var type: Int?
    var imageLinkType: Int?
    var search: String?
    var page: String?
    var prePage: String?
    
    init(id: String?, type: Int?, imageLinkType: Int?, search: String? = nil, page: String? = nil, perPage: String? = nil){
        self.id = id
        self.type = type
        self.imageLinkType = imageLinkType
        self.search = search
        self.page = page
        self.prePage = perPage
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        id <- map["id"]
        type <- map["type"]
        imageLinkType <- map["imageLinkType"]
        search <- map["search"]
        page <- map["page"]
        prePage <- map["perPage"]
    }
}

//MARK: - CREATE SUBGROUP RESPONSE

class CreateSubgroupResponse: Mappable {
    
    var userId: [String]?
    
    init(userId: [String]) {
        self.userId = userId
    }
    
    required init?(map: ObjectMapper.Map) {}
    
    func mapping(map: ObjectMapper.Map) {
        userId <- map["userId"]
    }
}

//MARK: - DELETE EVENT
class deleteEventResponse: Mappable {
    var eventId: String?
    
    init(eventId: String?){
        self.eventId = eventId
    }
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        eventId <- map["eventId"]
    }
}

// MARK: - FOR SEARSH DISPLAY
class SearchDisplayResponse: Mappable{
    
    var id: String?
    var groupId: String?
    
    init(id: String? = nil, groupId: String? = nil) {
        self.id = id
        self.groupId = groupId
    }
    
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        id <- map["id"]
        groupId <- map["groupId"]
    }
    
}

//MARK: - KINSHIP NAME CHANGE
class KinshipNameChangeResponse: Mappable {
    required init?(map: ObjectMapper.Map) {}

    var groupId: String?
    var groupName: String?
    
    init(groupId: String? = nil, groupName: String? = nil) {
        self.groupId = groupId
        self.groupName = groupName
    }
    
    func mapping(map: ObjectMapper.Map) {
        groupId <- map["groupId"]
        groupName <- map["groupName"]
    }
}

// MARK: - GET RSVP NAME
class RSVPNameRequest: Mappable {
    required init?(map: ObjectMapper.Map) {}
    
    var eventId: String?
    var type: String?
    
    init(eventId: String? = nil, type: String? = nil) {
        self.eventId = eventId
        self.type = type
    }
    
    func mapping(map: ObjectMapper.Map) {
        eventId <- map["eventId"]
        type <- map["type"]
    }
}

//MARK: - SUBGROUP MEMBER LIST
class SubgroupMemberRequest: Mappable {
    required init?(map: ObjectMapper.Map) {}
    
    var groupId: String?
    
    init(groupId: String? = nil) {
        self.groupId = groupId
    }
    
    func mapping(map: ObjectMapper.Map) {
        groupId <- map["groupId"]
    }
}

//MARK: - APPLICAION VERSION CHECK API
class ApplicationVersion: Mappable{
    required init?(map: ObjectMapper.Map) { }

    var type: String?
    var version: String?
    
    init(type: String? = nil, version: String? = nil) {
        self.type = type
        self.version = version
    }
    
    func mapping(map: ObjectMapper.Map) {
        type <- map["type"]
        version <- map["version"]
    }
}

//MARK: - HELP
class HelpRequest: Mappable {

    var reason: String?
    var description: String?

    required init?(map: Map){
    }

    init(reason: String? = nil, description: String? = nil) {
        self.reason = reason
        self.description = description
    }
    func mapping(map: Map) {
        reason <- map["reason"]
        description <- map["description"]
    }
}

//MARK: - FORGOT PASSWORD AUTH
class forogtPasswordAuthRequest: Mappable {

    var step: String?
    var password: String?
    var confirmPassword: String?

    required init?(map: Map){
    }

    init(step: String? = nil, password: String? = nil, confirmPassword: String? = nil) {
        self.step = step
        self.password = password
        self.confirmPassword = confirmPassword
    }
    
    func mapping(map: Map) {
        step <- map["step"]
        password <- map["password"]
        confirmPassword <- map["confirmPassword"]
    }
}


// MARK: - FCM TOKEN
class fcmTokenRequest: Mappable {

    var token: String?
    var deviceId: String?
    var platform: String?
    
    init(token: String? = nil, deviceId: String? = nil, platform: String? = nil) {
        self.token = token
        self.deviceId = deviceId
        self.platform = platform
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        token <- map["token"]
        deviceId <- map["deviceId"]
        platform <- map["platform"]
    }
}

// MARK: - Send Community suggestion
class sendSuggestionRequest: Mappable {

    var name: String?
    var city: String?
    var idea: String?
    
    init(name: String? = nil, city: String? = nil, idea: String? = nil) {
        self.name = name
        self.city = city
        self.idea = idea
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        name <- map["name"]
        city <- map["city"]
        idea <- map["idea"]
    }
}

// MARK: - Community listing
class communityGetPostRequest: Mappable {

    var communityId: String?
    var page: Int?
    var perPage: Int?
    
    init(communityId: String? = nil, page: Int? = nil, perPage: Int? = nil) {
        self.communityId = communityId
        self.page = page
        self.perPage = perPage
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        communityId <- map["communityId"]
        page <- map["page"]
        perPage <- map["perPage"]
    }
}

// MARK: - Add new post to community
class addNewPostRequest: Mappable {
    
    var communityId: String?
    var message: String?
    
    init(communityId: String? = nil, message: String? = nil) {
        self.communityId = communityId
        self.message = message
    }
    
    required init?(map: Map){
    }
    
    func mapping(map: Map) {
        communityId <- map["communityId"]
        message <- map["message"]
    }
}

// MARK: - Comment listing
class commentGetPostRequest: Mappable {

    var postId: String?
    var page: Int?
    var perPage: Int?
    
    init(postId: String? = nil, page: Int? = nil, perPage: Int? = nil) {
        self.postId = postId
        self.page = page
        self.perPage = perPage
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        postId <- map["postId"]
        page <- map["page"]
        perPage <- map["perPage"]
    }
}

// MARK: - Add New comment
class addCommentRequest: Mappable {

    var postId: String?
    var message: String?

    init(postId: String? = nil, message: String? = nil) {
        self.postId = postId
        self.message = message
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        postId <- map["postId"]
        message <- map["message"]
    }
}

// MARK: - Like Post
//class postLikeRequest: Mappable {
//
//    var postId: String?
//    var like: Bool?
//
//    init(postId: String? = nil, like: Bool? = nil) {
//        self.postId = postId
//        self.like = like
//    }
//    
//    required init?(map: Map){
//    }
//
//    func mapping(map: Map) {
//        postId <- map["postId"]
//        like <- map["like"]
//    }
//}
class postLikeRequest: Mappable {

    var postId: String?
    var like: String?

    init(postId: String? = nil, like: String? = nil) {
        self.postId = postId
        self.like = like
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        postId <- map["postId"]
        like <- map["like"]
    }
}

// MARK: - Like Comment
class commentLikeRequest: Mappable {

    var commentId: String?
    var like: Bool?

    init(commentId: String? = nil, like: Bool? = nil) {
        self.commentId = commentId
        self.like = like
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        commentId <- map["commentId"]
        like <- map["like"]
    }
}

// MARK: - Explore Community
class exploreCommunityRequest: Mappable {

    var type: Int?
    var search: String?

    init(type: Int? = nil, search: String? = nil) {
        self.type = type
        self.search = search
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        type <- map["type"]
        search <- map["search"]
    }
}

// MARK: - Leave community
class leaveCommunityRequest: Mappable {

    var communityId: String?

    init(communityId: String? = nil) {
        self.communityId = communityId
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        communityId <- map["communityId"]
    }
}

// MARK: - Join Community
class joinCommunityRequest: Mappable {
    
    var communityId: String?
    
    init(communityId: String? = nil) {
        self.communityId = communityId
    }
    
    required init?(map: Map){
    }
    
    func mapping(map: Map) {
        communityId <- map["communityId"]
    }
}

// MARK: - My community feature
class myCommunityRequest: Mappable {

    var search: String?

    init(search: String? = nil) {
        self.search = search
    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        search <- map["search"]
    }
}

// MARK: - Search community
class searchCommunityGetPostRequest: Mappable {

    var mainId: String?
    var type: Int?
    var subId: String?
    
    init(mainId: String? = nil, type: Int? = nil, subId: String? = nil) {
        self.mainId = mainId
        self.type = type
        self.subId = subId
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        mainId <- map["mainId"]
        type <- map["type"]
        subId <- map["subId"]
    }
}

class GetNotificationRequest: Mappable {

    var page: Int?
    var perPage: Int?
    
    init(page: Int? = nil, perPage: Int? = nil) {
        self.page = page
        self.perPage = perPage
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        page <- map["page"]
        perPage <- map["perPage"]
    }
}
