package com.kinship.mobile.app.ux.container.setting.editProfile

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils.createMultipartBody
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import javax.inject.Inject


class EditProfileUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val firstNameFlow = MutableStateFlow("")
    private val firstNameErrorFlow = MutableStateFlow<String?>(null)


    //child birthday list
    private var tempSingleDateList = ArrayList<Long>()
    private var tempMultipleDateList = ArrayList<Long>()

    //child birthdate flow
    private val tempMultipleDateFlow = MutableStateFlow<List<Long>>(emptyList())
    private val tempSingleDateFlow = MutableStateFlow<List<Long>>(emptyList())


    private val profileImgFlow = MutableStateFlow("")
    private val lastNameFlow = MutableStateFlow("")
    private val lastNameErrorFlow = MutableStateFlow<String?>(null)
    private val cityFlow = MutableStateFlow("")
    private val cityErrorFlow = MutableStateFlow<String?>(null)
    private val bioFlow = MutableStateFlow("")
    private val bioErrorFlow = MutableStateFlow<String?>(null)
    private val singleOrMultiplePregnancyFlow = MutableStateFlow(0)
    private var singleOrMultiple: Int = 0
    private val openPickImgDialog = MutableStateFlow(false)
    private val selectImage = MutableStateFlow(false)
    private val launchCamera = MutableStateFlow(false)
    private val captureUri = MutableStateFlow<Uri?>(null)
    var list: List<Long> = emptyList()
    private val dateHashMapMultiple: HashMap<String, Long?> =
        hashMapOf(
            Constants.MultipleDate.FIRST to null,
            Constants.MultipleDate.SECOND to null,
            Constants.MultipleDate.THIRD to null
        )
    private val dateHashMapSingle: HashMap<String, Long?> =
        hashMapOf(Constants.SingleDate.FIRST to null)


    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<ProfileData>>?>(null)

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): EditProfileUiState {
        setEditProfileData(coroutineScope)
        singleOrMultiple =
            if (getUserData(coroutineScope)?.profile?.editSingleOrmultipleGender == 1 || getUserData(
                    coroutineScope
                )?.profile?.editSingleOrmultipleGender == null
            ) 0 else 1

        return EditProfileUiState(
            firstNameFlow = firstNameFlow,
            onFirstNameValueChange = { firstNameFlow.value = it;firstNameErrorFlow.value = null },
            lastNameFlow = lastNameFlow,
            onLastNameValueChange = { lastNameFlow.value = it;lastNameErrorFlow.value = null },
            apiResultFlow = apiResultFlow,
            cityFlow = cityFlow,
            onCityValueChange = {
                Log.d("TAG", "location: $it")
                cityFlow.value = it;cityErrorFlow.value = null
            },
            firstNameErrorFlow = firstNameErrorFlow,
            bioErrorFlow = bioErrorFlow,
            lastNameErrorFlow = lastNameErrorFlow,
            bioFlow = bioFlow,
            onBioValueChange = { bioFlow.value = it;bioErrorFlow.value = null },
            onSingleBabyBornDate = { dob ->
                dateHashMapSingle[Constants.SingleDate.FIRST] = dob
                tempSingleDateFlow.value = listOf(dob)
                tempSingleDateList.add(dob)
            },
            onEditProfileClick = {
                storeResponseToDataStore(
                    coroutineScope = coroutineScope,
                    navigate = navigate,
                    profileData = it
                )
            },
            onOpenORDismissDialog = { openPickImgDialog.value = it },
            onImageFlag = { selectImage.value = it },
            openPickImgDialog = openPickImgDialog,
            profilePicFlow = profileImgFlow,
            onClearUnUsedUseState = { clearUnUsedUseState() },
            onBackClick = { navigate(NavigationAction.PopIntent) },
            launchCamera = launchCamera,
            onClickOfCamera = { createUriForCaptureImg(it) },
            onProfileImgPick = {
                profileImgFlow.value = it; launchCamera.value = false
            },
            onMultipleFirstBabyBornDate = { dob1 ->
                dateHashMapMultiple[Constants.MultipleDate.FIRST] = dob1
                tempMultipleDateFlow.value = listOf(dob1)
                tempMultipleDateList.add(dob1)
            },
            onMultipleSecondBabyBornDate = { dob2 ->
                dateHashMapMultiple[Constants.MultipleDate.SECOND] = dob2
                tempMultipleDateFlow.value = listOf(dob2)
                tempMultipleDateList.add(dob2)

            },

            onMultipleThirdBabyBornDate = { dob3 ->
                dateHashMapMultiple[Constants.MultipleDate.THIRD] = dob3
                tempMultipleDateFlow.value = listOf(dob3)
                tempMultipleDateList.add(dob3)
            },
            captureUri = captureUri,

            tempMultipleDateFlow = tempMultipleDateFlow,
            tempSingleDateFlow = tempSingleDateFlow,
            singleMultipleOption = singleOrMultiplePregnancyFlow,
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            },
            onSingleClick = {
                dateHashMapMultiple.clear()
                singleOrMultiple = 0
                singleOrMultiplePregnancyFlow.value = 1
            },
            onMultipleClick = {
                dateHashMapSingle.clear()
                singleOrMultiple = 1
                singleOrMultiplePregnancyFlow.value = 2
            },
            onEditProfileAPICall = {
                if (isInfoValid(context)) {
                    if (singleOrMultiple == 1) {
                        if (dateHashMapMultiple[Constants.MultipleDate.FIRST] == null) {
                            callEditProfileAPI(coroutineScope)
                        } else if (dateHashMapMultiple[Constants.MultipleDate.SECOND] == null) {
                            Toasty.warning(
                                context,
                                context.getString(R.string.please_select_at_least_two_birthdate),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()

                        } else {
                            callEditProfileAPI(coroutineScope)
                        }

                    } else {
                        callEditProfileAPI(coroutineScope)
                    }
                }

            },


            )
    }


    @SuppressLint("SuspiciousIndentation")
    private fun setEditProfileData(coroutineScope: CoroutineScope) {
        if (getUserData(coroutineScope)?.profile?.firstName?.isNotBlank() == true) {
            firstNameFlow.value = getUserData(coroutineScope)?.profile?.firstName.toString()
        }
        if (getUserData(coroutineScope)?.profile?.lastName?.isNotBlank() == true) {
            lastNameFlow.value = getUserData(coroutineScope)?.profile?.lastName.toString()
        }
        if (getUserData(coroutineScope)?.profile?.city?.isNotBlank() == true) {
            cityFlow.value = getUserData(coroutineScope)?.profile?.city.toString()
        }
        if (getUserData(coroutineScope)?.profile?.bio?.isNotBlank() == true) {
            bioFlow.value = getUserData(coroutineScope)?.profile?.bio.toString()
        }
        profileImgFlow.value = getUserData(coroutineScope)?.profile?.profileImage.toString()
        if (getUserData(coroutineScope)?.profile?.kinshipReason == 3 && getUserData(coroutineScope)?.profile?.editSingleOrmultipleGender == null) {
            singleOrMultiplePregnancyFlow.value =
                if (getUserData(coroutineScope)?.profile?.singleOrMultipleBirth == 1 || getUserData(
                        coroutineScope
                    )?.profile?.singleOrMultipleBirth == -1
                ) 0 else 1
            if (getUserData(coroutineScope)?.profile?.singleOrMultipleBirth == 1) {
                tempSingleDateFlow.value =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate as ArrayList<Long> else arrayListOf()
            } else {
                tempMultipleDateFlow.value =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate as ArrayList<Long> else arrayListOf()
            }
        } else {
            val babyBornDateList = getUserData(coroutineScope)?.profile?.editBabyBornDate
            singleOrMultiplePregnancyFlow.value =
                if (getUserData(coroutineScope)?.profile?.editSingleOrmultipleGender == 1 || getUserData(
                        coroutineScope
                    )?.profile?.editSingleOrmultipleGender == null
                ) 0 else 1
            if (singleOrMultiplePregnancyFlow.value == 0) {
                dateHashMapSingle[Constants.SingleDate.FIRST] =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate?.firstOrNull() else null
                tempSingleDateFlow.value =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate as ArrayList<Long> else arrayListOf()
            } else {
                dateHashMapMultiple[Constants.MultipleDate.FIRST] =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate?.firstOrNull() else null
                if (babyBornDateList != null && babyBornDateList.size >= 2) {
                    // Access the second element safely
                    dateHashMapMultiple[Constants.MultipleDate.SECOND] = babyBornDateList[1]
                } else {
                    // If the list is null or does not have enough elements, set the value to null
                    dateHashMapMultiple[Constants.MultipleDate.SECOND] = null
                }
                dateHashMapMultiple[Constants.MultipleDate.THIRD] =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate?.lastOrNull() else null
                tempMultipleDateFlow.value =
                    if (getUserData(coroutineScope)?.profile?.editBabyBornDate?.size != 0) getUserData(
                        coroutineScope
                    )?.profile?.editBabyBornDate as ArrayList<Long> else arrayListOf()
            }
        }
    }

    private fun createUriForCaptureImg(context: Context) {
        val file = context.createImageFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
        Logger.e("capture uri: $uri")
        captureUri.value = uri
        launchCamera.value = true
    }

    @SuppressLint("SimpleDateFormat")
    fun Context.createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir      /* directory */
        )
        return image
    }

    private fun callEditProfileAPI(
        coroutineScope: CoroutineScope,

        ) {
        val map: HashMap<String, RequestBody> = hashMapOf()
        if (firstNameFlow.value.isNotBlank()) map[Constants.EditProfile.FIRSTNAME] =
            firstNameFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (lastNameFlow.value.isNotBlank()) map[Constants.EditProfile.LASTNAME] =
            lastNameFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (cityFlow.value.isNotBlank()) map[Constants.EditProfile.CITY] =
            cityFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (dateHashMapMultiple["1"] == null) {
            map[Constants.EditProfile.EDIT_SINGLE_OR_MULTIPLE_GENDER] =
                1.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else if (singleOrMultiple == 0) {
            map[Constants.EditProfile.EDIT_SINGLE_OR_MULTIPLE_GENDER] =
                1.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            map[Constants.EditProfile.EDIT_SINGLE_OR_MULTIPLE_GENDER] =
                2.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        var dobLists = ""
        if (singleOrMultiple == 0) {
            for (i in dateHashMapSingle) {
                dobLists = TextUtils.join(",", dateHashMapSingle.values.filterNotNull())
            }
        } else {
            for (i in dateHashMapMultiple) {
                dobLists = TextUtils.join(",", dateHashMapMultiple.values.filterNotNull())
            }
        }
        if (dobLists.isNotBlank()) map[Constants.EditProfile.EDIT_BABY_BORN_DATE] =
            dobLists.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (bioFlow.value.isNotBlank()) map[Constants.EditProfile.BIO] =
            bioFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull()) else map[Constants.EditProfile.BIO] =
            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (selectImage.value) {
            val profileImageFile = File(profileImgFlow.value)
            val profileImage =
                createMultipartBody(profileImageFile, Constants.EditProfile.PROFILE_IMAGE)
            coroutineScope.launch {
                apiRepository.editProfileWithImage(map, profileImage).collect {
                    apiResultFlow.value = it
                }
            }
        } else {
            coroutineScope.launch {
                apiRepository.editProfile(map).collect {
                    apiResultFlow.value = it
                }
            }
        }
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }

    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        profileData: ProfileData
    ) {
        val userData = UserAuthResponseData()
        userData.auth = getUserData(coroutineScope)?.auth
        userData._id = getUserData(coroutineScope)?._id
        userData.isProfileCompleted = getUserData(coroutineScope)?.isProfileCompleted
        userData.isVerify = getUserData(coroutineScope)?.isVerify
        userData.email = getUserData(coroutineScope)?.email
        userData.profile = profileData
        coroutineScope.launch {
            appPreferenceDataStore.saveUserData(userData)
            appPreferenceDataStore.setIsProfilePicUpdated(true)
            navigate(NavigationAction.PopIntent)
        }
    }

    private fun isInfoValid(context: Context): Boolean {
        var validInfo = true
        if (firstNameFlow.value.isBlank()) {
            firstNameErrorFlow.value = context.getString(R.string.enter_first_name)
            validInfo = false
        }
        if (lastNameFlow.value.isBlank()) {
            lastNameErrorFlow.value = context.getString(R.string.enter_last_name)
            validInfo = false
        }
        return validInfo
    }

    private fun clearUnUsedUseState() {
        launchCamera.value = false
    }

    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }
}