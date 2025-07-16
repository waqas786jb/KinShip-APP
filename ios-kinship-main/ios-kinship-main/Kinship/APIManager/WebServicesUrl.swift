 //
//  WebServicesUrl.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation

var serverUrl = "https://demo.iroidsolutions.com:8001/api/v1/"
//var serverUrl = "https://api.findkinship.com/api/v1/"

let socketServerUrl = "https://demo.iroidsolutions.com:8001/"
//let socketServerUrl = "https://api.findkinship.com/"

// MARK: - AUTHENTICATION
let loginUrl = serverUrl + "auth/login"     // Login
let registerUrl = serverUrl + "auth/register"       // Register
let forgotPasswordUrl = serverUrl + "user/forgot-password"      // Forgot Password
let otpVerificationUrl = serverUrl + "auth/verify-otp"      // OTP verification
let resendOtpVarificationUrl = serverUrl + "auth/resend-otp"        // resend OTP Verification
let forgotPasswordAuthUrl = serverUrl + "user/forgot-password-auth"        // Forgot Password in Change Password
let logOutUrl = serverUrl + "auth/logout"       // Logout
let checkVersionUrl = serverUrl + "auth/version"       // Logout
let fcmTokenUrl = serverUrl + "user/fcm-token"       // Logout

//MARK: - PROFILE (Add profile of user)
let addProfileUrl = serverUrl + "user/profile"      // Profile
let hobbiesUrl = serverUrl + "data/"        // Hobbies

//MARK: - HOME PAGE
let createGroupUrl = serverUrl + "group/create"        // Create Group
let getGroupDetailsUrl = serverUrl + "group/my"        // get my group details

// MARK: - EDIT PROFILE
let editProfileURL = serverUrl + "user/profile"         // Edit Profile
let changePasswordUrl = serverUrl + "user/change-password"          // Change Password
let termsAndconditionsUrl = serverUrl + "user/terms-and-policy"          // Change Password

//MARK: - LEAVE KINSHIP
let leaveKinshipUrl = serverUrl + "user/leave-kinship"      // Leave kinship
let deleteAccountUrl = serverUrl + "user/delete-user"       // Delete Account

//  MARK: - EVENT DETAILS
let getMyEventsUrl = serverUrl + "event/"       // Get my all events
let getMyUpcomingEventUrl = serverUrl + "event/upcoming"        // Get my upcoming Events
let getRSVPMemberUrl = serverUrl + "event/names"         // get RSVP member names

//MARK: - NOTIFICATION
let userNotificationUrl = serverUrl + "user/notifications"      // User Notification
let getNotificationSettingUrl = serverUrl + "user/notification-settings"        // Get Notification Setting
let notificationSettingUrl = serverUrl + "user/notification-settings"      // Notification Setting

//MARK: - EVENTS
let createEventUrl = serverUrl + "event/"       // Create new event
let allEventsUrl = serverUrl + "event/events"   // Get all events
let eventStatusUrl = serverUrl + "event/event-status"       // set event status
let updateEventUrl = serverUrl + "event/"           // Update my event
let deleteEventUrl = serverUrl + "event/event-delete"           // Delete my event

// MARK: - DIRECT MESSAGE
let getUserGroupUrl = serverUrl + "chat/user-group-list"        //Get user and members list
let chatImageLinkMessageListUrl = serverUrl + "chat/image-link-message-list"        //Get user all message, images and link
let photosSendUrl = serverUrl + "chat/attechment"           //Post image in API
let subgroupCreateUrl = serverUrl + "chat/subgroup"         // Create sub group
let popUpRemoveUrl = serverUrl + "user/flag"            // Remove flag API
let kinshipGroupChangeUrl = serverUrl + "chat/groupNameChange"          // Kinship Group name change
let subgroupMemberListUrl = serverUrl + "chat/group-members"           // Subgroup member user list

//MARK: - LIKE MESSAGE
let likeMessageUrl = serverUrl + "chat/like-message"        // Set like on message
let kinshipChatDataUrl = serverUrl + "chat/kinship-data"        // Get all Chat kinship data

//MARK: - SEARCH MESSAGE
let searchResultUrl = serverUrl + "chat/search"               // search result

//MARK: - HELP
let helpUrl = serverUrl + "user/contact-support"                //help mail


// MARK: - Community
let myCommunityUrl = serverUrl + "community/my-communities"     // Get all my community
let sendSuggestionUrl = serverUrl + "community/new-suggestion"     // Send new community suggestion
let communityPostUrl = serverUrl + "community/community-posts"     // see all the community post
let newPostUrl = serverUrl + "community/post"     // Send new post
let commentPostUrl = serverUrl + "comment/comments"     // get all comments
let addCommentUrl = serverUrl + "Comment/"     // add New comments
let likePostUrl = serverUrl + "community/like-post"     // like Post
let likeCommentUrl = serverUrl + "comment/like-comment"     // like Comments
let exploreCommunityUrl = serverUrl + "community/communities"     // Explore new community
let leaveCommunityUrl = serverUrl + "community/leave-community"     // Leave community
let joinCommunityUrl = serverUrl + "community/join-community"     // Join community
let searchCommunityPostUrl = serverUrl + "user/notification-redirect"     // see all the search community post
