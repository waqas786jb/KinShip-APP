package com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.userProfileRequest.UserProfileRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.hobbiesData.HobbiesData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ui.compose.countryCode.CountryCodePickerNew
import com.kinship.mobile.app.ui.compose.countryCode.allCountries
import com.kinship.mobile.app.ux.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@Suppress("DEPRECATION")
class GetUserProfileUiStateUseCase @Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    private val firstNameFlow = MutableStateFlow("")
    private val firstNameErrorFlow = MutableStateFlow<String?>(null)
    private val lastNameFlow = MutableStateFlow("")
    private val lastNameErrorFlow = MutableStateFlow<String?>(null)
    private val phoneNumberFlow = MutableStateFlow("")
    private val phoneNumberErrorFlow = MutableStateFlow<String?>(null)
    private val dobFlow = MutableStateFlow("")
    private val obsessedListFlow = MutableStateFlow<List<String>>(emptyList())
    private val dobErrorFlow = MutableStateFlow<String?>(null)
    private val cityErrorFlow = MutableStateFlow<String?>(null)
    private val zipCodeFlow = MutableStateFlow("")
    private val cityFlow = MutableStateFlow("")
    private val zipCodeErrorFlow = MutableStateFlow<String?>(null)
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var countryCode: String? = null
    private var hobbyList: ArrayList<String> = arrayListOf()
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)
    private val apiHobbiesListResultFlow =
        MutableStateFlow<NetworkResult<ApiResponseNew<HobbiesData>>?>(null)

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): UserProfileUiState {
        callHobbiesListAPI(coroutineScope)
        val data = allCountries.find { it.cCountryPhoneNoCode == "+1" }
        countryCode = data?.countryCode
        return UserProfileUiState(
            firstNameFlow = firstNameFlow,
            onFirstNameValueChange = { firstNameFlow.value = it;firstNameErrorFlow.value = null },
            lastNameFlow = lastNameFlow,
            apiResultFlow = apiResultFlow,
            apiHobbiesListResultFlow = apiHobbiesListResultFlow,
            onLastNameValueChange = { lastNameFlow.value = it;lastNameErrorFlow.value = null },
            phoneNumberFlow = phoneNumberFlow,
            onPhoneNumberValueChange = {
                phoneNumberFlow.value = it;phoneNumberErrorFlow.value = null
            },
            obsessedListFlow = obsessedListFlow,
            zipCodeFlow = zipCodeFlow,
            onZipCodeValueChange = {
                zipCodeFlow.value = it;zipCodeErrorFlow.value = null
            },
            countryCode = countryCode,
            onNavigateToHome = {
                storeResponseToDataStore(
                    context = context,
                    coroutineScope = coroutineScope,
                    navigate = navigate,
                    userAuthResponseData = it
                )

            },
            onSelectedDateChange = { dob ->
                val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                val date: Date = formatter.parse(dob) as Date
                dobFlow.value = date.time.toString()
                dobErrorFlow.value = null
                Log.d("TAG", "selectDate: ${dobFlow.value}")
            },
            onHobbyList = { list ->
                if (list.isAdd) {
                    hobbyList.add(list.id)
                } else {
                    hobbyList.remove(list.id)
                }
            },

            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            firstNameErrorFlow = firstNameErrorFlow,
            lastNameErrorFlow = lastNameErrorFlow,
            phoneNumberErrorFlow = phoneNumberErrorFlow,
            dobErrorFlow = dobErrorFlow,
            zipCodeErrorFlow = zipCodeErrorFlow,
            onFindingKinshipClick = {
                if (isUserProfileInInfoValid(context)) {
                    getLatLongFromZipCode(context, zipCodeFlow.value, coroutineScope)

                }
            },
            cityFlow = cityFlow,
           onCityValueChanges = {
               cityFlow.value=it
           },
            cityErrorFlow = cityErrorFlow
        )
    }
    private fun storeResponseToDataStore(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: UserAuthResponseData?
    ) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.saveUserData(it)
            }
            navigateToHomeScreen(context, navigate, coroutineScope)
        }
    }

    private fun makeUserProfileInReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val userProfileRequest = UserProfileRequest(
                step = 2,
                firstName = firstNameFlow.value,
                lastName = lastNameFlow.value,
                phoneNumber = phoneNumberFlow.value,
                dateOfBirth = dobFlow.value,
                countrycode = CountryCodePickerNew.getCountryPhoneCodeNew(),
                hobbies = hobbyList,
                lat = latitude.toString(),
                long = longitude.toString(),
                zipcode = zipCodeFlow.value,
                city = cityFlow.value
            )
            callUserProfileAPI(userProfileRequest, coroutineScope)
        }
    }

    private fun callUserProfileAPI(
        userProfileRequest: UserProfileRequest,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            apiRepository.userProfileRequest(userProfileRequest).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun callHobbiesListAPI(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getHobbiesList().collect {
                apiHobbiesListResultFlow.value = it

            }
        }
    }
    private fun isUserProfileInInfoValid(context: Context): Boolean {
        var validInfo = true
        if (firstNameFlow.value.isBlank()) {
            firstNameErrorFlow.value = context.getString(R.string.enter_first_name)
            validInfo = false
        }
        if (lastNameFlow.value.isBlank()) {
            lastNameErrorFlow.value = context.getString(R.string.enter_last_name)
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
        if (dobFlow.value.isBlank()) {
            dobErrorFlow.value = context.getString(R.string.enter_the_date_of_birth)
            validInfo = false
        }
        val zipCodeLength = zipCodeFlow.value.length
        if (zipCodeFlow.value.isBlank()) {
            zipCodeErrorFlow.value = context.getString(R.string.enter_the_zip_code)
            validInfo = false
        } else if (zipCodeLength < 4 || zipCodeLength > 6) {
            zipCodeErrorFlow.value =
                context.getString(R.string.zip_code_must_be_between_4_to_6_digits)
            validInfo = false
        }
        if (cityFlow.value.isBlank()) {
            cityErrorFlow.value = "Please enter the city"
            validInfo = false
        }
        return validInfo


    }

    private fun navigateToHomeScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            val intent = Intent(context, MainActivity::class.java)
            navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getLatLongFromZipCode(
        context: Context,
        zipCode: String,
        coroutineScope: CoroutineScope
    ) {
        val geocoder = Geocoder(context)
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(zipCode, 1)
            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
                // Use the address as needed
                latitude = address.latitude
                longitude = address.longitude
                if (latitude != 0.0 && longitude != 0.0) {
                    if (hobbyList.isEmpty()) {

                        Toasty.warning(
                            context,
                            context.getString(R.string.please_select_the_hobbies),
                            Toast.LENGTH_SHORT,
                            false
                        ).show()
                    } else if (hobbyList.size < 3) {
                        Toasty.warning(
                            context,
                            context.getString(R.string.please_select_between_3_5_hobbies),
                            Toast.LENGTH_SHORT,
                            false
                        ).show()
                    } else {
                        makeUserProfileInReq(coroutineScope)
                    }


                } else {
                    Toasty.warning(
                        context,
                        context.getString(R.string.please_enter_valid_zipcode),
                        Toast.LENGTH_SHORT,
                        false
                    ).show()

                }

            } else {
                Toasty.warning(
                    context,
                    context.getString(R.string.please_enter_valid_zipcode),
                    Toast.LENGTH_SHORT,
                    false
                ).show()


            }
        } catch (e: IOException) {
            // handle exception
        }
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
        apiHobbiesListResultFlow.value = null
    }
}
