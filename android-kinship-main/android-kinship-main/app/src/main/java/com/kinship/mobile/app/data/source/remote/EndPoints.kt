package com.kinship.mobile.app.data.source.remote

import com.kinship.mobile.app.BuildConfig


object EndPoints {
    object URLs {
        const val BASE_URL: String = BuildConfig.BASE_URL
    }

    object Auth {
        const val SIGN_UP: String = "auth/register"
        const val LOG_IN: String = "auth/login"
        const val OTP_VERIFICATION: String = "auth/verify-otp"
        const val RESET_OTP: String = "auth/resend-otp"
        const val GET_HOBBIES: String = "data"
        const val LOGOUT: String = "auth/logout"
        const val CREATE_GROUP: String = "group/my"
        const val REFRESH_TOKEN: String = "auth/refresh-token"
    }

    object Home {
        const val EDIT_PROFILE: String = "user/profile"
    }
    object Version {
        const val VERSION: String = "auth/version"
    }

    object User {
        const val FORGET_PASSWORD: String = "user/forgot-password"
        const val USER_PROFILE: String = "user/profile"
        const val CHANGE_PASSWORD: String = "user/change-password"
        const val LEAVE_KINSHIP: String = "user/leave-kinship"
        const val DELETE_KINSHIP_USER: String = "user/delete-user"
        const val EDIT_USER_PROFILE: String = "user/profile"
        const val REGISTER_FOR_PUSH: String = "user/fcm-token"
        const val NOTIFICATION_LISTING: String = "user/notifications"
        const val NOTIFICATION_SETTINGS: String = "user/notification-settings"
        const val KINSHIP_NAME_EDIT = "chat/groupNameChange"
        const val USER_CONTACT_SUPPORT = "user/contact-support"
        const val FORGET_PASSWORD_USER = "user/forgot-password-auth"
    }
    object Event{
        const val MY_EVENTS :String = "event"
        const val UPCOMING_EVENTS :String = "event/upcoming"
        const val EVENTS :String = "event/events"
        const val EVENTS_STATUS :String = "event/event-status"
        const val CREATE_EVENT: String = "event"
        const val EVENT_NAME: String = "event/names"
        const val EVENT_DELETE: String = "event/event-delete"
        const val EDIT_EVENT: String = "event"

    }
    object Chat{

        const val CHAT_LIST_FOR_KINSHIP_GROUP = "chat/image-link-message-list"
        const val SEND_MESSAGE = "chat/attechment"
        const val IS_LIKE_DISLIKE = "chat/like-message"
        const val GET_KINSHIP_DATA_CHAT_LIST = "chat/kinship-data"
        const val USER_FLAG = "user/flag"
        const val CHAT_USER_GROUP_LIST = "chat/user-group-list"
        const val ADD_GROUP_MEMBER = "chat/subgroup"
        const val SEARCH_CHAT = "chat/search"
        const val GROUP_MEMBER = "chat/group-members"


    }
    object Communities {
        const val MY_COMMUNITIES = "community/my-communities"
        const val NEW_SUGGESTION = "community/new-suggestion"
        const val EXPLORE_COMMUNITY = "community/communities"
        const val COMMUNITY_POST_LIST = "community/community-posts"
        const val ADD_NEW_POST = "community/post"
        const val JOIN_COMMUNITY = "community/join-community"
        const val LEAVE_COMMUNITY = "community/leave-community"
        const val POST_COMMENT = "comment/comments"
        const val ADD_COMMENT = "Comment/"
        const val COMMUNITY_POST_LIKE_DISLIKE = "community/like-post"
        const val COMMENT_POST_LIKE_DISLIKE = "comment/like-comment"
        const val TERM_AND_CONDITION = "user/terms-and-policy"
        const val USER_NOTIFICATION_NOTIFICATION_REDIRECT = "user/notification-redirect"



    }

}