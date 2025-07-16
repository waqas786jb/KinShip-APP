//
//  ResponseModel.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation
import ObjectMapper

// MARK: - LOGIN RESPONSE
class LogInResponse: Mappable {

    var Id: String?
    var email: String?
    var isVerify: Bool?
    var isProfileCompleted: Bool?
    var profile: Profile?
    var auth: Auth?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        email <- map["email"]
        isVerify <- map["isVerify"]
        isProfileCompleted <- map["isProfileCompleted"]
        profile <- map["profile"]
        auth <- map["auth"]
    }
}

class Auth: Mappable {

    var tokenType: String?
    var accessToken: String?
    var refreshToken: String?
    var expiresAt: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        tokenType <- map["tokenType"]
        accessToken <- map["accessToken"]
        refreshToken <- map["refreshToken"]
        expiresAt <- map["expiresAt"]
    }
}

class Profile: Mappable {
        var Id: String?
        var userId: String?
        var kinshipReason: Int?
        var whenIsYourDueDate: String?
        var singleOrMultiplePregnancy: Int?
        var singleGender: Int?
        var firstTimeMom: Int?
        var babyBornDate: [String]?
        var hobbies: [String]?
        var step: Int?
        var editBabyBornDate: [Int]?
        var isActive: Bool?
        var createdAt: String?
        var updatedAt: String?
        var V: Int?
        var countrycode: String?
        var dateOfBirth: String?
        var firstName: String?
        var lastName: String?
        var lat: String?
        var long: String?
        var phoneNumber: String?
        var zipcode: Int?
        var groupId: String?
        var profileImage: String?
        var bio: String?
        var city: String?
        var editSingleOrmultipleGender: Int?
        var newEmail: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
                Id <- map["_id"]
                userId <- map["userId"]
                kinshipReason <- map["kinshipReason"]
                whenIsYourDueDate <- map["whenIsYourDueDate"]
                singleOrMultiplePregnancy <- map["singleOrMultiplePregnancy"]
                singleGender <- map["singleGender"]
                firstTimeMom <- map["firstTimeMom"]
                babyBornDate <- map["babyBornDate"]
                hobbies <- map["hobbies"]
                step <- map["step"]
                editBabyBornDate <- map["editBabyBornDate"]
                isActive <- map["isActive"]
                createdAt <- map["created_at"]
                updatedAt <- map["updated_at"]
                V <- map["__v"]
                countrycode <- map["countrycode"]
                dateOfBirth <- map["dateOfBirth"]
                firstName <- map["firstName"]
                lastName <- map["lastName"]
                lat <- map["lat"]
                long <- map["long"]
                phoneNumber <- map["phoneNumber"]
                zipcode <- map["zipcode"]
                groupId <- map["groupId"]
                profileImage <- map["profileImage"]
                bio <- map["bio"]
                city <- map["city"]
                editSingleOrmultipleGender <- map["editSingleOrmultipleGender"]
                newEmail <- map["newEmail"]
    }
}


//MARK: - REGISTER RESPONSE
class RegisterResponse: Mappable {

    var Id: String?
    var email: String?
    var isVerify: Bool?
    var isProfileCompleted: Bool?
    var profile: [Profiles]?
    var auth: Auths?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        email <- map["email"]
        isVerify <- map["isVerify"]
        isProfileCompleted <- map["isProfileCompleted"]
        profile <- map["profile"]
        auth <- map["auth"]
    }
}

class Auths: Mappable {

    var tokenType: String?
    var accessToken: String?
    var refreshToken: String?
    var expiresIn: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        tokenType <- map["tokenType"]
        accessToken <- map["accessToken"]
        refreshToken <- map["refreshToken"]
        expiresIn <- map["expiresIn"]
    }
}

class Profiles: Mappable {


    required init?(map: Map){
    }

    func mapping(map: Map) {
    }
}

//MARK: - FORGOT PASSWORD
class ForgotPasswordResponse: Mappable {

    var status: String?
    var message: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        status <- map["status"]
        message <- map["message"]
    }
}

// MARK: - OTP VERIFICATION
class OTPVerificationResponse: Mappable {

    var Id: String?
    var email: String?
    var isVerify: Bool?
    var isProfileCompleted: Bool?
    var profile: [ProfileOTP]?
    var accessToken: AccessTokenOTP?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        email <- map["email"]
        isVerify <- map["isVerify"]
        isProfileCompleted <- map["isProfileCompleted"]
        profile <- map["profile"]
        accessToken <- map["access_token"]
    }
}

class AccessTokenOTP: Mappable {

    var tokenType: String?
    var accessToken: String?
    var refreshToken: String?
    var expiresAt: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        tokenType <- map["tokenType"]
        accessToken <- map["accessToken"]
        refreshToken <- map["refreshToken"]
        expiresAt <- map["expiresAt"]
    }
}

class ProfileOTP: Mappable {


    required init?(map: Map){
    }

    func mapping(map: Map) {
    }
}

// MARK: - RESEND OTP VERIFICATION
class ResendOtpVarificationResponse: Mappable {

    var message: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        message <- map["message"]
    }
}

//MARK: - ADD PROFILE
class AddProfileResponse: Mappable {

    var status: String?
    var message: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        status <- map["status"]
        message <- map["message"]
    }
}

//MARK: - ABOUT YOU'R CONCEIVE
class AboutConceiveResponse: Mappable {

    var Id: String?
    var email: String?
    var isVerify: Bool?
    var isProfileCompleted: Bool?
    var profile: ProfileCompleted?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        email <- map["email"]
        isVerify <- map["isVerify"]
        isProfileCompleted <- map["isProfileCompleted"]
        profile <- map["profile"]
    }
}

class ProfileCompleted: Mappable {

    var editBabyBornDate: [EditBabyBornDate]?
    var Id: String?
    var userId: String?
    var kinshipReason: NSNumber?
    var whenIsYourDueDate: String?
    var singleOrMultiplePregnancy: NSNumber?
    var singleGender: NSNumber?
    var firstTimeMom: NSNumber?
    var babyBornDate: [String]?
    var hobbies: [Hobbies]?
    var step: NSNumber?
    var isActive: Bool?
    var isFull: Bool?
    var createdAt: String?
    var updatedAt: String?
    var V: NSNumber?
    var countrycode: String?
    var dateOfBirth: String?
    var firstName: String?
    var lastName: String?
    var lat: String?
    var long: String?
    var phoneNumber: String?
    var zipcode: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        editBabyBornDate <- map["editBabyBornDate"]
        Id <- map["_id"]
        userId <- map["userId"]
        kinshipReason <- map["kinshipReason"]
        whenIsYourDueDate <- map["whenIsYourDueDate"]
        singleOrMultiplePregnancy <- map["singleOrMultiplePregnancy"]
        singleGender <- map["singleGender"]
        firstTimeMom <- map["firstTimeMom"]
        babyBornDate <- map["babyBornDate"]
        hobbies <- map["hobbies"]
        step <- map["step"]
        isActive <- map["isActive"]
        isFull <- map["isFull"]
        createdAt <- map["created_at"]
        updatedAt <- map["updated_at"]
        V <- map["__v"]
        countrycode <- map["countrycode"]
        dateOfBirth <- map["dateOfBirth"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        lat <- map["lat"]
        long <- map["long"]
        phoneNumber <- map["phoneNumber"]
        zipcode <- map["zipcode"]
    }
}

class Hobbies: Mappable {

    var Id: String?
    var name: String?
    var V: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        name <- map["name"]
        V <- map["__v"]
    }
}

//class BabyBornDate: Mappable {
//
//
//    required init?(map: Map){
//    }
//
//    func mapping(map: Map) {
//    }
//}

class EditBabyBornDate: Mappable {


    required init?(map: Map){
    }

    func mapping(map: Map) {
    }
}

class HobbiesConceive: Mappable {


    required init?(map: Map){
    }

    func mapping(map: Map) {
    }
}

class BabyBornDateConceive: Mappable {


    required init?(map: Map){
    }

    func mapping(map: Map) {
    }
}

//MARK: - FOR A TAGLIST VIEW
class ObserveTegViewModel {
    var title: String = ""
    var isSelected: Bool = false
    
    init(title: String, isSelected: Bool = false){
        self.title = title
        self.isSelected = isSelected
    }
}

//MARK: - HOBBIES
class HobbiesResponse: Mappable {

    var Id: String?
    var name: String?
    var V: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        name <- map["name"]
        V <- map["__v"]
    }
}

//MARK: - LOGOUT
class LogOutResponse: Mappable {

    var message: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        message <- map["message"]
    }
}

// MARK: - GET MY PROFILE DETAILS
class HomePageResponse: Mappable {

    var groupMembers: [GroupMembers]?
    var count: Int?
    var groupId: String?
    var groupName: String?
    var image: String?
    var isGroupCreated: Bool?
    var chatGroupId: String?
    var messageCount: Bool?
    var kinshipCount: NSNumber?
    var city: String?
    var notificationCount: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        groupMembers <- map["groupMembers"]
        count <- map["count"]
        groupId <- map["groupId"]
        groupName <- map["groupName"]
        image <- map["image"]
        isGroupCreated <- map["isGroupCreated"]
        chatGroupId <- map["chatGroupId"]
        messageCount <- map["messageCount"]
        kinshipCount <- map["kinshipCount"]
        city <- map["city"]
        notificationCount <- map["notificationCount"]
    }
}

class GroupMembers: Mappable {

    var Id: String?
    var userId: String?
    var firstName: String?
    var lastName: String?
    var city: String?
    var bio: String?
    var dateOfBirth: Int?
    var profileImage: String?
    var chatGroupId: String?
    var isSelected: Bool? = false

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        city <- map["city"]
        bio <- map["bio"]
        dateOfBirth <- map["dateOfBirth"]
        profileImage <- map["profileImage"]
        chatGroupId <- map["chatGroupId"]
        isSelected <- map["isSelected"]
    }
}

//MARK: - GET MY EVENT
class GetMyEventResponse: Mappable {
        var Id: String?
        var userId: String?
        var eventName: String?
        var startTime: Int?
        var endTime: Int?
        var eventDate: Int?
        var isAllDay: Bool?
        var location: String?
        var lat: String?
        var long: String?
        var link: String?
        var photo: String?
        var eventDescription: String?
        var yes: Int?
        var no: Int?
        var maybe: Int?
        var createdAt: Int?
    
    init(Id: String? = nil, userId: String? = nil, eventName: String? = nil, startTime: Int? = nil, endTime: Int? = nil, eventDate: Int? = nil, isAllDay: Bool? = nil, location: String? = nil, lat: String? = nil, long: String? = nil, link: String? = nil, photo: String? = nil, eventDescription: String? = nil, yes: Int? = nil, no: Int? = nil, maybe: Int? = nil, createdAt: Int? = nil) {
        self.Id = Id
        self.userId = userId
        self.eventName = eventName
        self.startTime = startTime
        self.endTime = endTime
        self.eventDate = eventDate
        self.isAllDay = isAllDay
        self.location = location
        self.lat = lat
        self.long = long
        self.link = link
        self.photo = photo
        self.eventDescription = eventDescription
        self.yes = yes
        self.no = no
        self.maybe = maybe
        self.createdAt = createdAt
    }

        required init?(map: Map){
        }

        func mapping(map: Map) {
            Id <- map["_id"]
            userId <- map["userId"]
            eventName <- map["eventName"]
            startTime <- map["startTime"]
            endTime <- map["endTime"]
            eventDate <- map["eventDate"]
            isAllDay <- map["isAllDay"]
            location <- map["location"]
            lat <- map["lat"]
            long <- map["long"]
            link <- map["link"]
            photo <- map["photo"]
            eventDescription <- map["eventDescription"]
            yes <- map["yes"]
            no <- map["no"]
            maybe <- map["maybe"]
            createdAt <- map["created_at"]
        }
}

// MARK: - GET MY UPCOMING EVENTS
class GetMyUpcomingEventResponse: Mappable {

    var Id: String?
        var userId: String?
        var eventName: String?
        var startTime: NSNumber?
        var endTime: NSNumber?
        var eventDate: NSNumber?
        var isAllDay: NSNumber?
        var location: String?
        var lat: String?
        var long: String?
        var link: String?
        var photo: String?
        var eventDescription: String?
        var firstName: String?
        var lastName: String?
    
    init(Id: String?, userId: String?, eventName: String?, startTime: NSNumber?, endTime: NSNumber?, eventDate: NSNumber?, isAllDay: NSNumber?, location: String?, lat: String?, long: String?, link: String?, photo: String?, eventDescription: String?, firstName: String?, lastName: String?) {
        self.Id = Id
        self.eventName = eventName
        self.startTime = startTime
        self.endTime = endTime
        self.eventDate = eventDate
        self.isAllDay = isAllDay
        self.location = location
        self.lat = lat
        self.long = long
        self.link = link
        self.photo = photo
        self.eventDescription = eventDescription
        self.firstName = firstName
        self.lastName = lastName
    }

        required init?(map: Map){
        }

        func mapping(map: Map) {
            Id <- map["_id"]
            userId <- map["userId"]
            eventName <- map["eventName"]
            startTime <- map["startTime"]
            endTime <- map["endTime"]
            eventDate <- map["eventDate"]
            isAllDay <- map["isAllDay"]
            location <- map["location"]
            lat <- map["lat"]
            long <- map["long"]
            link <- map["link"]
            photo <- map["photo"]
            eventDescription <- map["eventDescription"]
            firstName <- map["firstName"]
            lastName <- map["lastName"]
        }
}

class CreateEventResponse: Mappable {

    var Id: String?
    var userId: String?
    var eventName: String?
    var startTime: Int?
    var endTime: Int?
    var eventDate: Int?
    var location: String?
    var lat: String?
    var long: String?
    var link: String?
    var photo: String?
    var eventDescription: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        eventName <- map["eventName"]
        startTime <- map["startTime"]
        endTime <- map["endTime"]
        eventDate <- map["eventDate"]
        location <- map["location"]
        lat <- map["lat"]
        long <- map["long"]
        link <- map["link"]
        photo <- map["photo"]
        eventDescription <- map["eventDescription"]
    }
}
//class CreateEventResponse: Mappable {
//
//    var Id: String?
//    var userId: String?
//    var eventName: String?
//    var startTime: NSNumber?
//    var endTime: NSNumber?
//    var eventDate: NSNumber?
//    var location: String?
//    var isAllDay: NSNumber?
//    var lat: String?
//    var long: String?
//    var link: String?
//    var photo: String?
//    var eventDescription: String?
//
//    required init?(map: Map){
//    }
//
//    func mapping(map: Map) {
//        Id <- map["_id"]
//        userId <- map["userId"]
//        eventName <- map["eventName"]
//        startTime <- map["startTime"]
//        endTime <- map["endTime"]
//        eventDate <- map["eventDate"]
//        location <- map["location"]
//        isAllDay <- map["isAllDay"]
//        lat <- map["lat"]
//        long <- map["long"]
//        link <- map["link"]
//        photo <- map["photo"]
//        eventDescription <- map["eventDescription"]
//    }
//}

//MARK: - NOTIFICATION
//class NotificationResponse: Mappable{
//    var Id: String?
//    var senderId: String?
//    var receiverId: String?
//    var message: String?
//    var firstName: String?
//    var lastName: String?
//    var profileImage: String?
//    var updatedAt: Int?
//    var createdAt: Int?
//    var mainId: String?
//    var subId: String?
//    var type: Int?
//
//        required init?(map: Map){
//        }
//
//        func mapping(map: Map) {
//            Id <- map["_id"]
//            senderId <- map["senderId"]
//            receiverId <- map["receiverId"]
//            message <- map["message"]
//            firstName <- map["firstName"]
//            lastName <- map["lastName"]
//            profileImage <- map["profileImage"]
//            updatedAt <- map["updatedAt"]
//            createdAt <- map["createdAt"]
//            mainId <- map["mainId"]
//            subId <- map["subId"]
//            type <- map["type"]
//        }
//}
class NotificationResponse: Mappable {

    var Id: String?
    var senderId: String?
    var receiverId: String?
    var message: String?
    var firstName: String?
    var lastName: String?
    var profileImage: String?
    var createdAt: Int?
    var updatedAt: Int?
    var mainId: String?
    var subId: String?
    var type: Int?
    var communityName: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        senderId <- map["senderId"]
        receiverId <- map["receiverId"]
        message <- map["message"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        profileImage <- map["profileImage"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        mainId <- map["mainId"]
        subId <- map["subId"]
        type <- map["type"]
        communityName <- map["communityName"]
    }
}


// MARK: - Notificaton meta response
class NotificationMetaResponse: Mappable {

    var total: Int?
    var perPage: Int?
    var currentPage: Int?
    var totalPages: Int?
    var lastPage: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        total <- map["total"]
        perPage <- map["perPage"]
        currentPage <- map["currentPage"]
        totalPages <- map["totalPages"]
        lastPage <- map["lastPage"]
    }
}

//MARK: - GET NOTIFICATION SETTING
class GetNotificationSettingResponse: Mappable {

    var Id: String?
    var email: String?
    var password: String?
    var isOnline: Bool?
    var isProfileCompleted: Bool?
    var refKey: Bool?
    var isVerify: Bool?
    var isGroupCreated: Bool?
    var allNewPosts: Bool?
    var newEvents: Bool?
    var directMessage: Bool?
    var createdAt: String?
    var updatedAt: String?
    var V: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        email <- map["email"]
        password <- map["password"]
        isOnline <- map["isOnline"]
        isProfileCompleted <- map["isProfileCompleted"]
        refKey <- map["refKey"]
        isVerify <- map["isVerify"]
        isGroupCreated <- map["isGroupCreated"]
        allNewPosts <- map["allNewPosts"]
        newEvents <- map["newEvents"]
        directMessage <- map["directMessage"]
        createdAt <- map["created_at"]
        updatedAt <- map["updated_at"]
        V <- map["__v"]
    }
} 

//MARK: - NOTIFICATION SETTING
class NotificationSettingResponse: Mappable {

    var Id: String?
    var allNewPosts: Bool?
    var newEvents: Bool?
    var directMessage: Bool?

    required init?(map: Map){
    }
    func mapping(map: Map) {
        allNewPosts <- map["allNewPosts"]
        newEvents <- map["newEvents"]
        directMessage <- map["directMessage"]
    }
}

//MARK: - ALL EVENTS
class AllEventResponse: Mappable {

    var Id: String?
        var userId: String?
        var eventName: String?
        var startTime: Int?
        var endTime: Int?
        var eventDate: Int?
        var isAllDay: String?
        var location: String?
        var lat: String?
        var long: String?
        var link: String?
        var photo: String?
        var eventDescription: String?
        var firstName: String?
        var lastName: String?

        required init?(map: Map){
        }

        func mapping(map: Map) {
            Id <- map["_id"]
            userId <- map["userId"]
            eventName <- map["eventName"]
            startTime <- map["startTime"]
            endTime <- map["endTime"]
            eventDate <- map["eventDate"]
            isAllDay <- map["isAllDay"]
            location <- map["location"]
            lat <- map["lat"]
            long <- map["long"]
            link <- map["link"]
            photo <- map["photo"]
            eventDescription <- map["eventDescription"]
            firstName <- map["firstName"]
            lastName <- map["lastName"]
        }
}

//MARK: - DIREST MESSAGE
class UserGroupListResponse: Mappable {

    var Id: String?
    var userId: [String]?
    var createdAt: String?
    var updatedAt: String?
    var V: Int?
    var message: String?
    var profileImage: String?
    var name: String?
    var type: Int?
    var count: Int?
    var receiverId: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        V <- map["__v"]
        message <- map["message"]
        profileImage <- map["profileImage"]
        name <- map["name"]
        type <- map["type"]
        count <- map["count"]
        receiverId <- map["receiverId"]
    }
}

class UserGroupListMetaResponse: Mappable {

    var total: Int?
    var lastPage: Int?
    var perPage: Int?
    var currentPage: Int?
    var totalPages: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        total <- map["total"]
        lastPage <- map["lastPage"]
        perPage <- map["perPage"]
        currentPage <- map["currentPage"]
        totalPages <- map["totalPages"]
    }
}

//MARK: - MESSAGE, IMAGE, LINK
class ChatImageLinkMessageResponse: Mappable {
    
    var Id: String?
    var senderId: String?
    var receiverId: String?
    var groupId: String?
    var userId: [String]?
    var message: String?
    var type: Int?
    var grpSeenAt: [GrpSeenAt]?
    var seenAt: Bool?
    var isLiked: Bool?
    var isgrpLiked: Bool?
    var createdAt: Int?
    var updatedAt: Int?
    var profileImage: String?
    var name: String?
    var image: String?
    
    required init?(map: Map){
    }
    
    init(Id: String? = nil, senderId: String? = nil, receiverId: String? = nil, groupId: String? = nil, userId: [String]? = nil, message: String? = nil, type: Int? = nil, grpSeenAt: Array<GrpSeenAt>? = nil, seenAt: Bool? = nil, isLiked: Bool? = nil, isgrpLiked: Bool? = nil, createdAt: Int? = nil, updatedAt: Int? = nil, profileImage: String? = nil, name: String? = nil, image: String? = nil){
        self.Id = Id
        self.senderId = senderId
        self.receiverId = receiverId
        self.groupId = groupId
        self.userId = userId
        self.message = message
        self.type = type
        self.grpSeenAt = grpSeenAt
        self.seenAt = seenAt
        self.isLiked = isLiked
        self.isgrpLiked = isgrpLiked
        self.createdAt = createdAt
        self.updatedAt = updatedAt
        self.profileImage = profileImage
        self.name = name
        self.image = image
    }
    
    func mapping(map: Map) {
        Id <- map["_id"]
        senderId <- map["senderId"]
        receiverId <- map["receiverId"]
        groupId <- map["groupId"]
        userId <- map["userId"]
        message <- map["message"]
        type <- map["type"]
        grpSeenAt <- map["grpSeenAt"]
        seenAt <- map["seenAt"]
        isLiked <- map["isLiked"]
        isgrpLiked <- map["isgrpLiked"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        profileImage <- map["profileImage"]
        name <- map["name"]
        image <- map["image"]
    }
}

class IsLikedArrayGroup: Mappable {
    
    var id: String?
    var isLiked: Bool?
    var Id: String?
    
    required init?(map: Map){
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        isLiked <- map["isLiked"]
        Id <- map["_id"]
    }
}

class GrpSeenAt: Mappable {

    var id: String?
    var seen: Bool?
    var Id: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        id <- map["id"]
        seen <- map["seen"]
        Id <- map["_id"]
    }
}

//MARK: - CHAT MESSAGE LINK
class ChatImageLinkMessageMetaResponse: Mappable {

    var total: NSNumber?
    var lastPage: NSNumber?
    var perPage: NSNumber?
    var currentPage: NSNumber?
    var totalPages: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        total <- map["total"]
        lastPage <- map["lastPage"]
        perPage <- map["perPage"]
        currentPage <- map["currentPage"]
        totalPages <- map["totalPages"]
    }
}
//  MARK: - BIRTHDATE DETAILS
class BirthdayData: Mappable {
    
    var title: String?
    var dateTimeStamp: String?
    var isForSingle: Bool?

    init(title: String?,dateTimeStamp: String?,isForSingle: Bool? = false){
        self.title = title
        self.dateTimeStamp = dateTimeStamp
        self.isForSingle = isForSingle
    }
    required init?(map: Map){
    }

    func mapping(map: Map) {
        title <- map["title"]
        dateTimeStamp <- map["dateTimeStamp"]
        isForSingle <- map["isForSingle"]
    }
}

//MARK: - CHAT DETAILS
class ChatDetail: Mappable {
    var senderID: String?
    var receiverID: String?
    var groupID: String?
    var message: String?
    
    init(senderID: String?, receiverID: String?, groupID: String?, message: String?){
        self.senderID = senderID
        self.receiverID = receiverID
        self.groupID = groupID
        self.message = message
    }
    required init?(map: ObjectMapper.Map) { }
    
    func mapping(map: ObjectMapper.Map) {
        senderID <- map["senderId"]
        receiverID <- map["receiverId"]
        groupID <- map["groupId"]
        message <- map["message"]
    }
}

//MARK: - SEND MESSAGE
class SectionMessageData: Mappable{
    
    var headerDate:String?
    var messageData:[ChatImageLinkMessageResponse]?
    
    init(headerDate:String? = nil,messageData:[ChatImageLinkMessageResponse]? = nil) {
        self.headerDate = headerDate
        self.messageData = messageData
    }
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        headerDate <- map["headerDate"]
        messageData <- map["messageData"]
    }
}

//MARK: - SEND IMAGE
class PhotosSendResponse: Mappable {

        var senderId: String?
        var receiverId: Any?
        var groupId: String?
        var userId: [String]?
        var message: String?
        var image: String?
        var type: Int?
        var grpSeenAt: [GrpSeenAt]?
        var seenAt: Bool?
        var isLiked: Bool?
        var Id: String?
        var isLikedArray: [IsLikedArray]?
        var createdAt: String?
        var updatedAt: String?
        var V: Int?

        required init?(map: Map){
        }

        func mapping(map: Map) {
            senderId <- map["senderId"]
            receiverId <- map["receiverId"]
            groupId <- map["groupId"]
            userId <- map["userId"]
            message <- map["message"]
            image <- map["image"]
            type <- map["type"]
            grpSeenAt <- map["grpSeenAt"]
            seenAt <- map["seenAt"]
            isLiked <- map["isLiked"]
            Id <- map["_id"]
            isLikedArray <- map["isLikedArray"]
            createdAt <- map["createdAt"]
            updatedAt <- map["updatedAt"]
            V <- map["__v"]
        }
    }

    class IsLikedArray: Mappable {


        required init?(map: Map){
        }

        func mapping(map: Map) {
        }
    }

    class GrpSeenAtPhotos: Mappable {

        var id: String?
        var seen: Bool?
        var Id: String?

        required init?(map: Map){
        }

        func mapping(map: Map) {
            id <- map["id"]
            seen <- map["seen"]
            Id <- map["_id"]
        }
    }

// MARK: - LINKE MESSAGE
class LikeMessageResponse: Mappable {
    
        var Id: String?
        var isLikedArray: [IsLikedArray]?
        var isLiked: Bool?
        var isgrpLiked: Bool?

        required init?(map: Map){
        }

        func mapping(map: Map) {
            Id <- map["_id"]
            isLikedArray <- map["isLikedArray"]
            isLiked <- map["isLiked"]
            isgrpLiked <- map["isgrpLiked"]
        }
}

class IsLikedArrayLikeMessage: Mappable {
    
    var id: String?
    var isLiked: Bool?
    var Id: String?
    
    required init?(map: Map){
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        isLiked <- map["isLiked"]
        Id <- map["_id"]
    }
}

//MARK: - CREATE SUBGROUP RESPONSE
class SubgroupResponse: Mappable {

    var userId: [String]?
    var Id: String?
    var createdAt: String?
    var updatedAt: String?
    var V: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        userId <- map["userId"]
        Id <- map["_id"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        V <- map["__v"]
    }
}

//MARK: - SEARCH MESSAGE RESPONSE
class SearchMessageDisplayResponse: Mappable {
    var Id: String?
        var senderId: String?
        var receiverId: Any?
        var groupId: String?
        var userId: [String]?
        var message: String?
        var type: NSNumber?
        var grpSeenAt: [GrpSeenAt]?
        var seenAt: Bool?
        var isLiked: Bool?
        var isgrpLiked: Bool?
        var createdAt: NSNumber?
        var updatedAt: NSNumber?
        var profileImage: String?
        var name: String?
        var image: Any?

        required init?(map: Map){
        }

        func mapping(map: Map) {
            Id <- map["_id"]
            senderId <- map["senderId"]
            receiverId <- map["receiverId"]
            groupId <- map["groupId"]
            userId <- map["userId"]
            message <- map["message"]
            type <- map["type"]
            grpSeenAt <- map["grpSeenAt"]
            seenAt <- map["seenAt"]
            isLiked <- map["isLiked"]
            isgrpLiked <- map["isgrpLiked"]
            createdAt <- map["createdAt"]
            updatedAt <- map["updatedAt"]
            profileImage <- map["profileImage"]
            name <- map["name"]
            image <- map["image"]
        }
}

// MARK: - RSVP MEMBERS RESPONSE
class RSVPNameResponse: Mappable {
    var firstName: String?
        var lastName: String?
        var profileImage: String?

        required init?(map: Map){
        }

        func mapping(map: Map) {
            firstName <- map["firstName"]
            lastName <- map["lastName"]
            profileImage <- map["profileImage"]
        }
}

//MARK: - SUB GROUP MEMBERS LIST
class SubGroupMembersResponse: Mappable {

    var name: String?
    var profileImage: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        name <- map["name"]
        profileImage <- map["profileImage"]
    }
} 

class SubGroupCreateResponse: Mappable {

    var userId: [String]?
    var Id: String?
    var createdAt: String?
    var updatedAt: String?
    var V: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        userId <- map["userId"]
        Id <- map["_id"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        V <- map["__v"]
    }
}


class VersionCheckResponse: Mappable {

    var message: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        message <- map["message"]
    }
} 

// MARK: - My Community response
class MyCommunityResponse: Mappable {

    var Id: String?
    var name: String?
    var members: Int?
    var unseenCount: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        name <- map["name"]
        members <- map["members"]
        unseenCount <- map["unseenCount"]
    }
}


// MARK: - Community listing response
class CommunityListingMeta: Mappable {

    var total: Int?
    var perPage: Int?
    var currentPage: Int?
    var totalPages: Int?
    var lastPage: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        total <- map["total"]
        perPage <- map["perPage"]
        currentPage <- map["currentPage"]
        totalPages <- map["totalPages"]
        lastPage <- map["lastPage"]
    }
}

class CommunityListingData: Mappable {

    var Id: String?
    var userId: String?
    var message: String?
    var file: String?
    var like: Int?
    var isLiked: Bool?
    var commentCount: Int?
    var createdAt: Int?
    var updatedAt: Int?
    var profileImage: String?
    var firstName: String?
    var lastName: String?

    init(Id: String? = nil, userId: String? = nil, message: String? = nil, file: String? = nil, like: Int? = nil, isLiked: Bool? = nil, commentCount: Int? = nil, createdAt: Int? = nil, updatedAt: Int? = nil, profileImage: String? = nil, firstName: String? = nil, lastName: String? = nil) {
        self.Id = Id
        self.userId = userId
        self.message = message
        self.file = file
        self.like = like
        self.isLiked = isLiked
        self.commentCount = commentCount
        self.createdAt = createdAt
        self.updatedAt = updatedAt
        self.profileImage = profileImage
        self.firstName = firstName
        self.lastName = lastName
    }
    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        message <- map["message"]
        file <- map["file"]
        like <- map["like"]
        isLiked <- map["isLiked"]
        commentCount <- map["commentCount"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        profileImage <- map["profileImage"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
    }
}

// MARK: - Comment listing response
class CommentListingMeta: Mappable {

    var total: Int?
    var perPage: Int?
    var currentPage: Int?
    var totalPages: Int?
    var lastPage: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        total <- map["total"]
        perPage <- map["perPage"]
        currentPage <- map["currentPage"]
        totalPages <- map["totalPages"]
        lastPage <- map["lastPage"]
    }
}

//class CommentListingData: Mappable {
//
//    var Id: String?
//    var userId: String?
//    var like: Int?
//    var createdAt: Int?
//    var updatedAt: Int?
//    var profileImage: String?
//    var firstName: String?
//    var lastName: String?
//
//    required init?(map: Map){
//    }
//
//    func mapping(map: Map) {
//        Id <- map["_id"]
//        userId <- map["userId"]
//        like <- map["like"]
//        createdAt <- map["createdAt"]
//        updatedAt <- map["updatedAt"]
//        profileImage <- map["profileImage"]
//        firstName <- map["firstName"]
//        lastName <- map["lastName"]
//    }
//}
class CommentListingData: Mappable {

    var Id: String?
    var userId: String?
    var message: String?
    var like: Int?
    var createdAt: Int?
    var updatedAt: Int?
    var profileImage: String?
    var firstName: String?
    var lastName: String?
    var isLiked: Bool?
    
    init(Id: String? = nil, userId: String? = nil, message: String? = nil, like: Int? = nil, createdAt: Int? = nil, updatedAt: Int? = nil, profileImage: String? = nil, firstName: String? = nil, lastName: String? = nil, isLiked: Bool? = nil) {
        self.Id = Id
        self.userId = userId
        self.message = message
        self.like = like
        self.createdAt = createdAt
        self.updatedAt = updatedAt
        self.profileImage = profileImage
        self.firstName = firstName
        self.lastName = lastName
        self.isLiked = isLiked
    }

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        message <- map["message"]
        like <- map["like"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        profileImage <- map["profileImage"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        isLiked <- map["isLiked"]
    }
}

class CommentListingData2: Mappable {

    var Id: String?
    var userId: String?
    var postId: String?
    var text: String?
    var createdAt: Int?
    var updatedAt: Int?
    var firstName: String?
    var lastName: String?
    var profileImage: String?

//    init(Id: String? = nil, userId: String? = nil, text: String? = nil, like: Int? = nil, createdAt: Int? = nil, updatedAt: Int? = nil, profileImage: String? = nil, firstName: String? = nil, lastName: String? = nil) {
//        self.Id = Id
//        self.userId = userId
//        self.text = text
//
//        self.createdAt = createdAt
//        self.updatedAt = updatedAt
//        self.profileImage = profileImage
//        self.firstName = firstName
//        self.lastName = lastName
//    }
    
    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        postId <- map["postId"]
        text <- map["text"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        profileImage <- map["profileImage"]
    }
    
    
}

// MARK: - Explore Community response
class ExploreCommunity: Mappable {

    var Id: String?
    var name: String?
    var members: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        name <- map["name"]
        members <- map["members"]
    }
}

// MARK: - Add community response
class AddPostResponse: Mappable {

    var Id: String?
    var userId: String?
    var communityId: String?
    var message: String?
    var file: String?
    var firstName: String?
    var lastName: String?
    var profileImage: String?
    var commentCount: Int?
    var createdAt: Int?
    var updatedAt: Int?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        communityId <- map["communityId"]
        message <- map["message"]
        file <- map["file"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        profileImage <- map["profileImage"]
        commentCount <- map["commentCount"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
    }
}

// MARK: - Community comment search
//class CommunitycommentsearchResponse: Mappable {
//
//    var post: Post?
//    var comments: [Comments]?
//
//    required init?(map: Map){
//    }
//
//    func mapping(map: Map) {
//        post <- map["post"]
//        comments <- map["comments"]
//    }
//}

class PostResponse: Mappable {

    var Id: String?
    var userId: String?
    var communityId: String?
    var message: String?
    var file: String?
    var firstName: String?
    var lastName: String?
    var profileImage: String?
    var commentCount: NSNumber?
    var createdAt: NSNumber?
    var updatedAt: NSNumber?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        Id <- map["_id"]
        userId <- map["userId"]
        communityId <- map["communityId"]
        message <- map["message"]
        file <- map["file"]
        firstName <- map["firstName"]
        lastName <- map["lastName"]
        profileImage <- map["profileImage"]
        commentCount <- map["commentCount"]
        createdAt <- map["createdAt"]
        updatedAt <- map["updatedAt"]
    }
}

// MARK: - greoup details
class GrpObj: Mappable {

    var name: String?
    var profileImage: String?
    var userId: [String]?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        name <- map["name"]
        profileImage <- map["profileImage"]
        userId <- map["userId"]
    }
}

// MARK: - Terms and conditions url
class TermsAndConditions: Mappable {

    var url: String?

    required init?(map: Map){
    }

    func mapping(map: Map) {
        url <- map["url"]
    }
}
