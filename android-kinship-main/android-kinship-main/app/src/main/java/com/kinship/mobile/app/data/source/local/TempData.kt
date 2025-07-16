package com.kinship.mobile.app.data.source.local

import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore

object TempData {
    private val appPreferenceDataStore: AppPreferenceDataStore? = null
    val commonQuestionList = listOf(
        "I’m trying to conceive",
        "I’m pregnant",
        "I had a baby",
        "I adopted"

    )
    val conceive1List = listOf(
        "Less than a year",
        "Between 1 and 2 years",
        "More than 2 years",


        )



    val conceive2List = listOf(
        "The old-fashioned way",
        "Intra-uterina insemination (IUI)",
        "In-vitro fertizilation (IVF)",
    )
    val obsessedList = listOf(
        "Fitness",
        "Fashion",
        "Travel",
        "Pets",
        "Cooking",
        "Celebs",
        "Hiking",
        "Faith",
        "Wine tasting",
        "Reality TV",
        "Gaming",
        "Watching sports",
        "Playing sports",
        "Art",
        "Investing",
        "Shopping",
        "Skincare",
        "Healthy living",
        "Politics",
        "Sustainability",
        "Concerts",
        "Homemaking",
        "Dining out",
    )
    val SingleList = listOf(
        "Girl",
        "Boy",
        "It’s a surprise"
    )
    val multiplePregnancyList = listOf(
        "Girls",
        "Boys",
        "Both",
        "It’s a surprise"
    )
    val adoptList = listOf(
        "Girl",
        "Boy",
    )
    val BabyMultiple = listOf(
        "Girls",
        "Boys",
        "Both"

    )
    data class MyCommunities(
        var title:String,
        var subTitle:String
    )
//    var uiState = ChatUiState()

    data class SettingData(
        val title: Int,
        val icon: Int,
        val isArrowVisible: Boolean = false
    )
    val myCommunitiesList= listOf(
        MyCommunities("Yoga Community", subTitle = "12 Members"),
        MyCommunities("Fitness Challenge", subTitle = "22 Members"),
        MyCommunities("Baby Caring", subTitle = "09 Members")
    )

    val settingScreenListingData = listOf(
        SettingData(title = R.string.edit_profile, icon = R.drawable.ic_edit_profile, true),
        SettingData(title = R.string.notification_setting, icon = R.drawable.ic_notification_pink, true),
        SettingData(title = R.string.change_password, icon = R.drawable.ic_lock_pink, true),
        SettingData(title = R.string.update_contact_details, icon = R.drawable.ic_update_contact_details, true),
        SettingData(title = R.string.events_details, icon = R.drawable.ic_event_details, true),
        SettingData(title = R.string.help, icon = R.drawable.ic_help,true),
        SettingData(title = R.string.leave_your_kinship, icon = R.drawable.ic_leave_kinship,true),
        SettingData(title = R.string.delete_account, icon = R.drawable.ic_delete_account,true),
        SettingData(title = R.string.terms_conditions, icon = R.drawable.ic_term_condition),
        SettingData(title = R.string.logout, icon = R.drawable.ic_logout)
    )

    data class DrawerData(
        val title: String,
        val icon: Int,
        val isArrowVisible: Boolean = false
    )

    val galleryPhoto = listOf(
        R.drawable.ic_help,
        R.drawable.ic_add_event,
        R.drawable.ic_help,
        R.drawable.ic_add_event


    )

    data class DirectMessage(
        val profile: Int?,
        val userName: String,
        val message: String,
        val messageCount: Int = 0,
    )
    val directMessageItems = listOf(
        DirectMessage(
            profile = R.drawable.ic_placeholder,
            userName = "Arlene McCoy",
            message = "Has anyone introduce solid foods yet?".repeat(15),
            messageCount = 1
        ),
        DirectMessage(
            profile = R.drawable.ic_placeholder,
            userName = "Dianne Russell",
            message = "Has anyone introduce solid foods yet?",
            messageCount = 10
        ),
        DirectMessage(
            profile = R.drawable.ic_placeholder,
            userName = "Arlene McCoy, Dianne Russell",
            message = "Has anyone introduce solid foods yet?"
        ),
        DirectMessage(
            profile = R.drawable.ic_placeholder,
            userName = "Arlene McCoy",
            message = "Has anyone introduce solid foods yet?",
            messageCount = 15
        ),
        DirectMessage(
            profile = null,
            userName = "Darrell Steward",
            message = "Has anyone introduce solid foods yet?"
        ),
        DirectMessage(
            profile = R.drawable.ic_launcher_background,
            userName = "Dianne Russell",
            message = "Has anyone introduce solid foods yet?",
            messageCount = 100
        ),
    )
}



    /* val drawerListingData = listOf(
         DrawerData(title = R.string.members, icon = R.drawable.ic_member,true),
         DrawerData(title = R.string.gallery, icon = R.drawable.ic_gallery,true),
         DrawerData(title = R.string.links, icon = R.drawable.ic_link,true),
         DrawerData(title = R.string.search_messages, icon = R.drawable.ic_search,true),

     )*/


