package com.kinship.mobile.app.navigation.graph
import NewMessageScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ux.container.chat.GroupChatRoute
import com.kinship.mobile.app.ux.container.chat.GroupChatScreen
import com.kinship.mobile.app.ux.container.communityFeature.addNewPost.AddNewPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.addNewPost.AddNewPostScreen
import com.kinship.mobile.app.ux.container.communityFeature.comment.CommentRoute
import com.kinship.mobile.app.ux.container.communityFeature.comment.CommentScreen
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostScreen
import com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity.ExploreCommunityRoute
import com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity.ExploreCommunityScreen
import com.kinship.mobile.app.ux.container.gallery.GalleryRoute
import com.kinship.mobile.app.ux.container.gallery.GalleryScreen
import com.kinship.mobile.app.ux.container.likeMessage.LikeMessageRoute
import com.kinship.mobile.app.ux.container.likeMessage.LikeMessageScreen
import com.kinship.mobile.app.ux.container.communityFeature.myCommunities.MyCommunitiesRoute
import com.kinship.mobile.app.ux.container.communityFeature.myCommunities.MyCommunitiesScreen
import com.kinship.mobile.app.ux.container.communityFeature.searchCommunity.SearchCommunityRoute
import com.kinship.mobile.app.ux.container.communityFeature.searchCommunity.SearchCommunityScreen
import com.kinship.mobile.app.ux.container.newMessage.NewMessageRoute
import com.kinship.mobile.app.ux.container.search.SearchRoute
import com.kinship.mobile.app.ux.container.search.SearchScreen
import com.kinship.mobile.app.ux.container.setting.SettingRoute
import com.kinship.mobile.app.ux.container.setting.changePassword.ChangePasswordRoute
import com.kinship.mobile.app.ux.container.setting.changePassword.ChangePasswordScreen
import com.kinship.mobile.app.ux.container.setting.deleteAccount.DeleteAccountRoute
import com.kinship.mobile.app.ux.container.setting.deleteAccount.DeleteAccountScreen
import com.kinship.mobile.app.ux.container.setting.editProfile.EditProfileRoute
import com.kinship.mobile.app.ux.container.setting.editProfile.EditProfileScreen
import com.kinship.mobile.app.ux.container.setting.eventDetails.EventDetailsRoute
import com.kinship.mobile.app.ux.container.setting.eventDetails.EventDetailsScreen
import com.kinship.mobile.app.ux.container.setting.help.ContactSupportRoute
import com.kinship.mobile.app.ux.container.setting.help.ContactSupportScreen
import com.kinship.mobile.app.ux.container.setting.leaveKinship.LeaveKinshipRoute
import com.kinship.mobile.app.ux.container.setting.leaveKinship.LeaveKinshipScreen
import com.kinship.mobile.app.ux.container.setting.notification.notificationListing.NotificationListingRoute
import com.kinship.mobile.app.ux.container.setting.notification.notificationListing.NotificationListingScreen
import com.kinship.mobile.app.ux.container.setting.notification.notificationSetting.NotificationSettingRoute
import com.kinship.mobile.app.ux.container.setting.notification.notificationSetting.NotificationSettingScreen
import com.kinship.mobile.app.ux.container.setting.updateContactDetails.UpdateContactRoute
import com.kinship.mobile.app.ux.container.setting.updateContactDetails.UpdateContactScreen
import com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification.OtpUpdateVerificationRoute
import com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification.OtpUpdateVerificationScreen
import com.kinship.mobile.app.ux.container.singleUserChat.SingleGroupChatRoute
import com.kinship.mobile.app.ux.container.singleUserChat.SingleUserChatScreen
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionRoute
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionScreen
import com.kinship.mobile.app.ux.main.events.createEvent.CreateEventRoute
import com.kinship.mobile.app.ux.main.events.createEvent.CreateEventScreen

@Composable
fun AppContainerGraph(
    navController: NavHostController,
    screen: String,
    messageData: String,
    myEventData:String,
    memberData:String,
    chatId:String,
    eventId:String,
) {
    val appStartDestination = when (screen) {
        Constants.ContainerScreens.EDIT_PROFILE_SCREEN -> {
            EditProfileRoute.routeDefinition.value
        }

        Constants.ContainerScreens.CHANGE_PASSWORD_SCREEN -> {
            ChangePasswordRoute.routeDefinition.value
        }

        Constants.ContainerScreens.LEAVE_KINSHIP_SCREEN -> {
            LeaveKinshipRoute.routeDefinition.value
        }

        Constants.ContainerScreens.DELETE_ACCOUNT_SCREEN -> {
            DeleteAccountRoute.routeDefinition.value
        }

        Constants.ContainerScreens.UPDATE_CONTACT_DETAILS -> {
            UpdateContactRoute.routeDefinition.value
        }
        Constants.ContainerScreens.EVENT_DETAILS -> {
            EventDetailsRoute.routeDefinition.value
        }
        Constants.ContainerScreens.SEARCH_SCREEN -> {
            SearchRoute.routeDefinition.value
        }
        Constants.ContainerScreens.GALLERY_SCREEN -> {
            GalleryRoute.routeDefinition.value
        }
        Constants.ContainerScreens.NOTIFICATION_SETTING_SCREEN -> {
            NotificationSettingRoute.routeDefinition.value
        }
        Constants.ContainerScreens.NOTIFICATION_LISTING_SCREEN -> {
            NotificationListingRoute.routeDefinition.value
        }

        Constants.ContainerScreens.CHAT_SCREEN -> {
            GroupChatRoute.routeDefinition.value
        }

        Constants.ContainerScreens.CREATE_EVENT_SCREEN -> {
            CreateEventRoute.routeDefinition.value
        }
        Constants.ContainerScreens.IS_LIKE -> {
            LikeMessageRoute.routeDefinition.value
        }

       Constants.ContainerScreens.CREATE_EVENT_EDIT -> {
            CreateEventRoute.routeDefinition.value
        }

        Constants.ContainerScreens.NEW_MESSAGE -> {
            NewMessageRoute.routeDefinition.value
        }
        Constants.ContainerScreens.OTP_VERIFICATION -> {
            OtpUpdateVerificationRoute.routeDefinition.value
        }
        Constants.ContainerScreens.SINGLE_USER_CHAT_SCREEN -> {
            SingleGroupChatRoute.routeDefinition.value
        }
       /* Constants.ContainerScreens.MEMBER_SINGLE_CHAT_SCREEN -> {
            MemberSingleChatRoute.routeDefinition.value
        }*/
        Constants.ContainerScreens.HELP_SCREEN -> {
            ContactSupportRoute.routeDefinition.value
        }
        Constants.ContainerScreens.SETTING_SCREEN -> {
            SettingRoute.routeDefinition.value
        }
        Constants.ContainerScreens.GROUP_CHAT_SCREEN -> {
            GroupChatRoute.routeDefinition.value
        }
        Constants.ContainerScreens.MY_COMMUNITIES_SCREEN -> {
            MyCommunitiesRoute.routeDefinition.value
        }
        Constants.ContainerScreens.COMMUNITY_POST_SCREEN -> {
            CommunityPostRoute.routeDefinition.value
        }
      /*Constants.ContainerScreens.RSVP_SCREEN -> {
            RsvpRoute.routeDefinition.value
        }*/
        else ->  CommunityPostRoute.routeDefinition.value
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        EditProfileRoute.addNavigationRoute(this) { EditProfileScreen(navController) }
        NotificationListingRoute.addNavigationRoute(this) { NotificationListingScreen(navController) }
        ChangePasswordRoute.addNavigationRoute(this) { ChangePasswordScreen(navController) }
        LeaveKinshipRoute.addNavigationRoute(this) { LeaveKinshipScreen(navController) }
        DeleteAccountRoute.addNavigationRoute(this) { DeleteAccountScreen(navController) }
        UpdateContactRoute.addNavigationRoute(this) { UpdateContactScreen(navController) }
        EventDetailsRoute.addNavigationRoute(this) { EventDetailsScreen(navController, screen = screen) }
        NotificationSettingRoute.addNavigationRoute(this) { NotificationSettingScreen(navController) }
        GroupChatRoute.addNavigationRoute(this) { GroupChatScreen(navController) }
        OtpUpdateVerificationRoute.addNavigationRoute(this) { OtpUpdateVerificationScreen(navController, screen = screen) }
        CreateEventRoute.addNavigationRoute(this) { CreateEventScreen(navController, myEventResponse = myEventData, screen = screen) }
        GalleryRoute.addNavigationRoute(this) { GalleryScreen(navController) }
        SearchRoute.addNavigationRoute(this) { SearchScreen(navController) }
        LikeMessageRoute.addNavigationRoute(this) { LikeMessageScreen (navController) }
        NewMessageRoute.addNavigationRoute(this) { NewMessageScreen (navController) }
        SingleGroupChatRoute.addNavigationRoute(this) { SingleUserChatScreen (navController, messageDataResponse = messageData, memberDataResponse = memberData, chatID = chatId) }
        SearchRoute.addNavigationRoute(this) { SearchScreen (navController) }
      //  RsvpRoute.addNavigationRoute(this) { RsvpScreen (navController, eventId = eventId, screen = screen) }
       //   MemberSingleChatRoute.addNavigationRoute(this) { MemberSingleChatScreen (navController,memberData, chatId = chatId) }
        ContactSupportRoute.addNavigationRoute(this) { ContactSupportScreen (navController) }
        MyCommunitiesRoute.addNavigationRoute(this) { MyCommunitiesScreen (navController) }
        NewSuggestionRoute.addNavigationRoute(this) { NewSuggestionScreen(navController) }
        ExploreCommunityRoute.addNavigationRoute(this) { ExploreCommunityScreen(navController) }
        SearchCommunityRoute.addNavigationRoute(this) { SearchCommunityScreen(navController) }
        CommunityPostRoute.addNavigationRoute(this) { CommunityPostScreen(navController) }
        AddNewPostRoute.addNavigationRoute(this) { AddNewPostScreen(navController) }
        CommentRoute.addNavigationRoute(this) { CommentScreen(navController) }
    }
}
