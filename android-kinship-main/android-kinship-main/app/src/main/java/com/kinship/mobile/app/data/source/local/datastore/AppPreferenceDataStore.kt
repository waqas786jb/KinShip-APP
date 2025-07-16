package com.kinship.mobile.app.data.source.local.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore.Keys.QUESTION_DATA

import com.kinship.mobile.app.model.domain.response.QuesRequest
import com.kinship.mobile.app.model.domain.response.conceive.ConceiveResponseData
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.model.domain.response.notification.NotificationSettingResponse
import com.kinship.mobile.app.model.domain.response.roomId.RoomId
import com.kinship.mobile.app.model.domain.response.signUp.Auth
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreferenceDataStore
@Inject constructor(
    private val application: Application

) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "app_preferences",
        corruptionHandler = ReplaceFileCorruptionHandler {
            emptyPreferences()
        }
    )

    //User Data
    suspend fun saveUserData(userAuthResponseData: UserAuthResponseData) {
        val userData = Gson().toJson(userAuthResponseData, UserAuthResponseData::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_DATA] = userData
        }
    }

    suspend fun saveUseProfileData(userAuthResponseData: ConceiveResponseData) {
        val userProfile = Gson().toJson(userAuthResponseData, ConceiveResponseData::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_DATA] = userProfile
        }
    }


    // editProfileData
    suspend fun saveEditProfileData(profileData: ProfileData) {
        val userData = Gson().toJson(profileData, ProfileData::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_DATA] = userData
        }
    }
    suspend fun isProfilePicUpdated(): Boolean {
        val isUpdated =
            application.dataStore.data.firstOrNull()?.get(Keys.IS_PROFILE_PIC_UPDATED)
        return isUpdated ?: false
    }
    suspend fun setIsProfilePicUpdated(isUpdated: Boolean) {
        application.dataStore.edit { pref ->
            pref[Keys.IS_PROFILE_PIC_UPDATED] = isUpdated
        }
    }


    suspend fun getEditProfileData(): ProfileData? {
        val userProfile = application.dataStore.data.firstOrNull()?.get(Keys.USER_DATA)
        if (userProfile.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(userProfile, ProfileData::class.java)
    }


    //profileData
    suspend fun saveProfileData(userAuthResponseData: GroupMember) {
        val userData = Gson().toJson(userAuthResponseData, GroupMember::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.PROFILE_DATA] = userData
        }
    }

    suspend fun getProfileData(): GroupMember? {
        val userProfile = application.dataStore.data.firstOrNull()?.get(Keys.PROFILE_DATA)
        if (userProfile.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(userProfile, GroupMember::class.java)
    }

    suspend fun getUserData(): UserAuthResponseData? {
        val userData = application.dataStore.data.firstOrNull()?.get(Keys.USER_DATA)
        if (userData.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(userData, UserAuthResponseData::class.java)
    }

    suspend fun saveRoomId(roomId: String) {
        application.dataStore.edit { preferences ->
            preferences[Keys.ROOM_ID] = roomId
        }
    }

    //conceive data
    suspend fun saveConceiveData(userAuthResponseData: ConceiveResponseData) {
        val userData = Gson().toJson(userAuthResponseData, ConceiveResponseData::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_DATA] = userData
        }
    }

    suspend fun getConceiveData(): ConceiveResponseData? {
        val userData = application.dataStore.data.firstOrNull()?.get(Keys.USER_DATA)
        if (userData.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(userData, ConceiveResponseData::class.java)
    }


    //User auth data
    suspend fun saveUserAuthData(authData: Auth) {
        val userAuthData = Gson().toJson(authData, Auth::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_AUTH_DATA] = userAuthData
        }
    }

    suspend fun saveCreateGroupData(createGroup: CreateGroup?) {
        val createGroup = Gson().toJson(createGroup, CreateGroup::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.GROUP_DATA] = createGroup
        }
    }
    suspend fun saveEventData(eventData: MyEventsData?) {
        val eventData = Gson().toJson(eventData, MyEventsData::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.EVENT_DATA] = eventData
        }
    }

    suspend fun getEventData(): MyEventsData? {
        val eventData = application.dataStore.data.firstOrNull()?.get(Keys.EVENT_DATA)
        if (eventData.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(eventData, MyEventsData::class.java)
    }

    suspend fun saveRoomId(roomId: RoomId?) {
        val createGroup = Gson().toJson(roomId, RoomId::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.ROOM_ID] = createGroup
        }
    }
    suspend fun getRoomId(): String? {
        return application.dataStore.data.firstOrNull()?.get(Keys.ROOM_ID)
    }

    suspend fun getCreateGroupData(): CreateGroup? {
        val groupData = application.dataStore.data.firstOrNull()?.get(Keys.GROUP_DATA)
        if (groupData.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(groupData, CreateGroup::class.java)
    }

    suspend fun saveNotificationSettingData(data: NotificationSettingResponse) {
        val data = Gson().toJson(data, NotificationSettingResponse::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.NOTIFICATION_SETTING_DATA] = data
        }
    }


    suspend fun saveQuestionData(questionData: QuesRequest) {
        val jsonString = Gson().toJson(questionData)
        application.dataStore.edit { preferences ->
            preferences[QUESTION_DATA] = jsonString
        }
    }

    suspend fun getQuestionData(): QuesRequest? {
        val jsonString = application.dataStore.data.firstOrNull()?.get(QUESTION_DATA)
        if (jsonString.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(jsonString, object : TypeToken<QuesRequest>() {}.type)
    }

    //my Preference code
    suspend fun getUserAuthData(): Auth? {
        val userAuthData = application.dataStore.data.firstOrNull()?.get(Keys.USER_AUTH_DATA)
        if (userAuthData.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(userAuthData, Auth::class.java)
    }

    suspend fun getNotificationSettingData(): NotificationSettingResponse? {
        val data = application.dataStore.data.firstOrNull()?.get(Keys.NOTIFICATION_SETTING_DATA)
        if (data.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(data, NotificationSettingResponse::class.java)
    }

    //token time
    suspend fun saveAccessTokenStoreTime(storeTime: Long) {
        application.dataStore.edit { pref ->
            pref[Keys.ACCESS_TOKEN_SAVED_TIME] = storeTime
        }
    }

    suspend fun getAccessTokenStoreTime(): Long? {
        return application.dataStore.data.firstOrNull()?.get(Keys.ACCESS_TOKEN_SAVED_TIME)
    }


    //startUp flow start destination
    suspend fun saveStartUpStartDestination(screen: String?) {
        application.dataStore.edit { pref ->
            pref[Keys.START_DESTINATION] = screen ?: Constants.AppScreen.START_UP
        }
    }
    suspend fun getStatUpStartDestination(): String? {
        return application.dataStore.data.firstOrNull()?.get(Keys.START_DESTINATION)
    }
    suspend fun getContainerStartDestination(): String? {
        return application.dataStore.data.firstOrNull()?.get(Keys.START_DESTINATION)
    }

    //clearing whole pref data
    suspend fun clearAll() {
        application.dataStore.edit { it.clear() }
    }
    object Keys {

        val USER_DATA = stringPreferencesKey("userData")
        val GROUP_DATA = stringPreferencesKey("groupData")
        val EVENT_DATA = stringPreferencesKey("groupData")
        val ROOM_ID = stringPreferencesKey("roomId")

        val PROFILE_DATA = stringPreferencesKey("profileData")
        val NOTIFICATION_SETTING_DATA = stringPreferencesKey("notificationSettingData")
        val USER_PROFILE = stringPreferencesKey("userProfile")
        val LOGIN_DATA = stringPreferencesKey("userData")
        val OTP_DATA = stringPreferencesKey("otpData")
        val USER_AUTH_DATA = stringPreferencesKey("userAuthData")
        val ACCESS_TOKEN_SAVED_TIME = longPreferencesKey("accessTokenSavedTime")
        val QUESTION_DATA = stringPreferencesKey("questionData")
        val START_DESTINATION = stringPreferencesKey("startDestination")
        val CONTAINER_START_DESTINATION = stringPreferencesKey("containerStartDestination")
        val IS_PROFILE_PIC_UPDATED = booleanPreferencesKey("isProfilePicUpdated")

        private var INSTANCE: AppPreferenceDataStore? = null

        fun getInstance(application: Application): AppPreferenceDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferenceDataStore(application).also { INSTANCE = it }
            }
        }


    }


}
