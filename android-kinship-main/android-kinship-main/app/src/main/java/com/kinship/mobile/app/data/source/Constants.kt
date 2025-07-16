package com.kinship.mobile.app.data.source

object Constants {

    const val ANDROID = "Android"

    object AppScreen {
        const val START_UP: String = "startUp"
        const val SIGN_UP: String = "signUp"
        const val LOG_IN: String = "logIn"
        const val OTP_VERIFICATION: String = "otpVerification"
        const val COMMON_QUESTION_SCREEN: String = "commonQuestionScreen"
        const val MESSAGE_SCREEN ="messageScreen"
        const val MEMBER_SCREEN ="memberScreen"
        const val CREATE_EVENT ="createEvent"
        const val EVENT_SCREEN = "eventScreen"
        const val NOTIFICATION_SCREEN = "notificationScreen"
        const val COMMENT_SCREEN = "commentScreen"
        const val EXPLORE_COMMUNITY_SCREEN = "exploreCommunityScreen"
        const val SEARCH_SCREEN = "searchScreen"
        const val MY_COMMUNITY_SCREEN = "myCommunityScreen"
        const val COMMUNITY_POST_SCREEN = "communityPost"
    }

    object ContainerScreens {
        const val EDIT_PROFILE_SCREEN = "editProfileScreen"
        const val COMMUNITY_POST_SCREEN = "communityPostScreen"
        const val CREATE_EVENT_SCREEN = "createEventScreen"
        const val CHANGE_PASSWORD_SCREEN = "changePasswordScreen"
        const val LEAVE_KINSHIP_SCREEN = "leaveKinshipScreen"
        const val DELETE_ACCOUNT_SCREEN = "deleteAccountScreen"
        const val UPDATE_CONTACT_DETAILS = "updateContactDetails"
        const val EVENT_DETAILS = "eventDetailsScreen"
        const val NOTIFICATION_SETTING_SCREEN = "notificationSettingScreen"
        const val NOTIFICATION_LISTING_SCREEN = "notificationListingScreen"
        const val MY_COMMUNITIES_SCREEN = "myCommunitiesScreen"
        const val CHAT_SCREEN = "chatScreen"
        const val MEMBER_SCREEN = "memberScreen"
        const val MESSAGE_SCREEN = "messageScreen"
        const val GALLERY_SCREEN = "galleryScreen"
        const val OTP_VERIFICATION = "otpVerification"
        const val SETTING_SCREEN = "settingScreen"
        const val LINK_SCREEN = "linkScreen"
        const val SEARCH_SCREEN = "searchScreen"
        const val IS_LIKE = "isLike"
        const val NEW_MESSAGE = "newMessage"
        const val SINGLE_USER_CHAT_SCREEN = "singleUserChatScreen"
        const val MEMBER_SINGLE_CHAT_SCREEN = "memberSingleChatScreen"
        const val CREATE_EVENT_EDIT = "createEventEdit"
        const val HELP_SCREEN = "helpScreen"
        const val RSVP_SCREEN = "rsvpScreen"
        const val MEMBER_CHAT_SCREEN = "memberChatScreen"
        const val GROUP_CHAT_SCREEN = "groupChatScreen"



    }

    object NotificationPush {
        const val PUSH_KINSHIP_GROUP_TYPE: Int = 1
        const val PUSH_EVENT_CREATE: Int = 3
        const val PUSH_SINGLE_SUBGROUP_CHAT: Int = 2
        const val COMMUNITY_POST_TYPE: Int = 6
        const val TYPE: String = "type"
    }

    object Notification {
        const val KINSHIP: Int = 1
        const val DIRECT_MESSAGE: Int = 2
        const val EVENT_POST: Int = 3
        const val ARRIVING_EVENT: Int =  4
        const val CREATE_POST_IN_COMMUNITY_POST: Int = 5
        const val LIKE_DISLIKE_POST: Int = 6
        const val ADD_COMMENT: Int = 7

    }



    object BundleKey {
        const val EXTRA_BUNDLE = "extraBundle"
        const val IS_COME_FOR = "isComeFor"
        const val CHAT_ID = "chatId"
        const val EVENT_ID = "eventId"
        const val PROFILE_ID = "profileId"
        const val MESSAGE_RESPONSE = "messageResponse"
        const val MEMBER_RESPONSE = "memberResponse"
        const val MY_EVENT_RESPONSE = "myEventResponse"
        const val MAIN_ID = "mainID"
        const val TYPE = "type"
        const val RESET = "reset"
        const val RESTART_APP = "restartApp"
    }

    object IntentData {
        const val SCREEN_NAME = "screenName"
    }
    object AppInfo {
        const val DIR_NAME = "Kinship"
    }

    object SingleDate {
        const val FIRST = "first"
    }
    object MultipleDate {
        const val FIRST = "1"
        const val SECOND = "2"
        const val THIRD = "3"
    }
    object EditProfile{
        const val FIRSTNAME = "firstName"
        const val LASTNAME= "lastName"
        const val PROFILE_IMAGE = "profileImage"
        const val BIO = "bio"
        const val CITY = "city"
        const val EDIT_SINGLE_OR_MULTIPLE_GENDER = "editSingleOrmultipleGender"
        const val EDIT_BABY_BORN_DATE = "editBabyBornDate"
        const val EMAIL = "email"
        const val PHONE_NUMBER = "phoneNumber"
    }
    object CreateEvent{
        const val EVENT_ID = "eventId"
        const val EVENT_NAME = "eventName"
        const val START_TIME= "startTime"
        const val END_TIME = "endTime"
        const val EVENT_DATE = "eventDate"
        const val IS_ALL_DAY = "isAllDay"
        const val LINK = "link"
        const val LOCATION = "location"
        const val LAT = "lat"
        const val LONG = "long"
        const val EVENT_DESCRIPTION = "eventDescription"
        const val PHOTO = "photo"
    }


    object MultipartApiKeyNames{
        const val EMAIL = "email"
        const val COUNTRY_CODE = "countrycode"
        const val PHONE_NUMBER = "phoneNumber"
    }
    object SendImage{
        const val GROUP_ID = "groupId"
        const val RECEIVER_ID = "receiverId"
        const val FILE = "file"
        const val TYPE = "type"
        const val MESSAGE = "message"
    }
    object Socket{
        const val SOCKET_URL: String = "https://demo.iroidsolutions.com:8001"
       // const val SOCKET_URL: String = "https://api.findkinship.com"
        const val UPDATE_STATUS_TO_ONLINE: String = "UpdateStatusToOnline"
        const val CREATE_ROOM = "createRoom"
        const val ROOM_DISCONNECT = "roomDisconnect"
        const val ROOM_CONNECTED = "roomConnected"
        const val ROOM_DISCONNECTED = "roomDisconnected"
        const val SENDER_ID = "senderId"
        const val SENDER_NAME = "senderName"
        const val RECEIVER_ID = "receiverId"
        const val PROFILE = "profileImage"
        const val ROOM_ID = "roomId"
        const val STATUS_ONLINE = "statusOnline"
        const val IS_ONLINE = "isOnline"
        const val SEND_MESSAGE = "sendMessage"
        const val NEW_MESSAGE = "newMessage"

        const val MESSAGE = "message"
        const val MESSAGE_ID = "message_id"
        const val GROUP_ID = "groupId"
        const val CHAT_ID = "chatId"
        const val MESSAGE_TYPE = "type"
        const val CREATED_AT = "created_at"

        const val START_URL_ZOOM = "startZoomLink"
        const val IMAGE = "image"
    }
    object NetworkError{
        const val NETWORK_ERROR = "Network error"

    }
    object VersionType{
        const val VERSION_TYPE = "2"

    }

    object Message {
        const val TYPE_NORMAL_MSG = 1
        const val ONLY_IMAGE = 2
        const val IMAGE_AND_MESSAGE = 4
        const val TYPE_LINK = 3
        const val TYPE_ZOOM = 4
        const val TYPE_VIDEO = 5

    }

    object Community {
        const val TYPE = 2
        const val MY_COMMUNITY_SCREEN = "myCommunitiesScreen"
        const val EXPLORE_NEW_COMMUNITY = "exploreNewCommunity"
        const val COMMUNITY_ID = "communityId"
        const val MESSAGE = "message"
        const val FILE = "file"




    }

    object Gallery {
        const val TYPE = 1
        const val IMAGE_LINK_TYPE = 1

    }

}