package com.kinship.mobile.app.ux.container.setting.updateContactDetails

import android.content.Context
import android.util.Log
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ui.compose.countryCode.CountryCodePickerNew
import com.kinship.mobile.app.ui.compose.countryCode.allCountries
import com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification.OtpUpdateVerificationRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class UpdateContactUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {

    private val emailFlow = MutableStateFlow("")
    private var countryCode: String? = null
    private val emailErrorFlow = MutableStateFlow<String?>(null)
    private val phoneNumberFlow = MutableStateFlow("")
    private val phoneNumberErrorFlow = MutableStateFlow<String?>(null)
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<ProfileData>>?>(null)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): UpdateContactUiState {
        if (getUserData(coroutineScope)?.email?.isNotBlank() == true) {
            emailFlow.value = getUserData(coroutineScope)?.email.toString()
        }
        if (getUserData(coroutineScope)?.profile?.phoneNumber?.isNotBlank() == true) {
            phoneNumberFlow.value = getUserData(coroutineScope)?.profile?.phoneNumber.toString()
        }
        val data =
            allCountries.find { it.cCountryPhoneNoCode == "${getUserData(coroutineScope)?.profile?.countrycode}" }
        countryCode = data?.countryCode
        Log.d("TAG", "invoke:$+${getUserData(coroutineScope)?.profile?.countrycode} ")
        return UpdateContactUiState(
            emailFlow = emailFlow,
            onEmailValueChange = { emailFlow.value = it; emailErrorFlow.value = null },
            emailErrorFlow = emailErrorFlow,
            phoneNumberFlow = phoneNumberFlow,
            countryCode = countryCode,
            onPhoneNumberValueChange = {
                phoneNumberFlow.value = it; phoneNumberErrorFlow.value = null
            },
            phoneNumberErrorFlow = phoneNumberErrorFlow,
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            apiResultFlow = apiResultFlow,
            onApiSuccess = { storeSuccessDataAndNavigate(it, coroutineScope, navigate, context) },
            onClickOfUpdateButton = {
                if (validInfo(context)) {
                    callUpdateContactAPI(coroutineScope)
                }
            },
            onBackClick = { navigate(NavigationAction.PopIntent) }
        )
    }
    private fun callUpdateContactAPI(coroutineScope: CoroutineScope) {
        val map: HashMap<String, RequestBody> = hashMapOf()
        if (emailFlow.value.isNotBlank() && emailFlow.value != getUserData(coroutineScope)?.email.toString()) map[Constants.MultipartApiKeyNames.EMAIL] =
            emailFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (CountryCodePickerNew.getCountryPhoneCodeNew()
                .isNotBlank()
        ) map[Constants.MultipartApiKeyNames.COUNTRY_CODE] =
            CountryCodePickerNew.getCountryPhoneCodeNew()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (phoneNumberFlow.value.isNotBlank()) map[Constants.MultipartApiKeyNames.PHONE_NUMBER] =
            phoneNumberFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        coroutineScope.launch {
            apiRepository.editProfile(map).collect {
                apiResultFlow.value = it
            }
        }
    }
    private fun storeSuccessDataAndNavigate(
        data: ProfileData,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        context: Context
    ) {
        val userData = UserAuthResponseData()
        userData.profile = data
        userData.auth = getUserData(coroutineScope)?.auth!!
        userData._id = getUserData(coroutineScope)?._id!!
        userData.isProfileCompleted = getUserData(coroutineScope)?.isProfileCompleted!!
        userData.isVerify = getUserData(coroutineScope)?.isVerify!!
        userData.email = getUserData(coroutineScope)?.email!!
        coroutineScope.launch {
            if (data.newEmail.isBlank()) {
                appPreferenceDataStore.saveUserData(userData)
                navigate(NavigationAction.PopIntent)
            } else {
                if (appPreferenceDataStore.getUserData()?.email == data.newEmail) {
                    appPreferenceDataStore.saveUserData(userData)
                    navigate(NavigationAction.PopIntent)
                } else {
                    appPreferenceDataStore.saveUserData(userData)
                    navigate(NavigationAction.Navigate(OtpUpdateVerificationRoute.createRoute(email = emailFlow.value)))
                }
            }
        }
    }



    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }
    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }

    private fun validInfo(context: Context): Boolean {
        var validInfo = true
        if (emailFlow.value.isBlank()) {
            emailErrorFlow.value = context.getString(R.string.enter_email_validation)
            validInfo = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailFlow.value).matches()) {
            emailErrorFlow.value = context.getString(R.string.enter_valid_email_validation)
            validInfo = false
        }
        val phoneNumberLength = phoneNumberFlow.value.length
        if (phoneNumberFlow.value.isBlank()) {
            phoneNumberErrorFlow.value = context.getString(R.string.enter_the_phone_number)
            validInfo = false
        } else if (phoneNumberLength < 7 || phoneNumberLength > 15) {
            phoneNumberErrorFlow.value =
                context.getString(R.string.phone_number_must_be_between_7_to_15_digits)
            validInfo = false
        }
        return validInfo
    }
}