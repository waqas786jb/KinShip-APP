package com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.hobbiesData.HobbiesData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.model.domain.response.userStaticData.TempHobbyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserProfileUiState(

  val firstNameFlow: StateFlow<String> = MutableStateFlow(""),
  val onFirstNameValueChange: (String) -> Unit = {},
 val lastNameFlow: StateFlow<String> = MutableStateFlow(""),
 val onLastNameValueChange: (String) -> Unit = {},
 val passwordFlow: StateFlow<String> = MutableStateFlow(""),
 val onPasswordValueChange: (String) -> Unit = {},
 val phoneNumberFlow: StateFlow<String> = MutableStateFlow(""),
  val countryCode: String? = null,
 val onPhoneNumberValueChange: (String) -> Unit = {},
 val dobFlow: StateFlow<String> = MutableStateFlow(""),
 val obsessedListFlow: StateFlow<List<String>> = MutableStateFlow(emptyList()),
 val zipCodeFlow: StateFlow<String> = MutableStateFlow(""),
 val onZipCodeValueChange: (String) -> Unit = {},

  val cityFlow: StateFlow<String> = MutableStateFlow(""),
  val onCityValueChanges: (String) -> Unit = {},
  val cityErrorFlow: StateFlow<String?> = MutableStateFlow(null),



 // error flow
 val firstNameErrorFlow: StateFlow<String?> = MutableStateFlow(null),
 val lastNameErrorFlow: StateFlow<String?> = MutableStateFlow(null),
 val phoneNumberErrorFlow: StateFlow<String?> = MutableStateFlow(null),
 val dobErrorFlow: StateFlow<String?> = MutableStateFlow(null),
 val zipCodeErrorFlow: StateFlow<String?> = MutableStateFlow(null),

 //click
 val onFindingKinshipClick: () -> Unit = {},
 val onNavigateToHome:(UserAuthResponseData?) -> Unit = {},
 val onBackClick: () -> Unit = {},

 //data
 val onSelectedDateChange: (String) -> Unit = {},
 val list: StateFlow<String> = MutableStateFlow(""),
 val onHobbyList: (TempHobbyData) -> Unit = {},


 //clear API
 val clearAllApiResultFlow: () -> Unit = {},


 //apiResponse
 val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
 val apiHobbiesListResultFlow: StateFlow<NetworkResult<ApiResponseNew<HobbiesData>>?> = MutableStateFlow(null),



 )
