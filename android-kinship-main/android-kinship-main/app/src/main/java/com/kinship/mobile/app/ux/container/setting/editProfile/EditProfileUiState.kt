package com.kinship.mobile.app.ux.container.setting.editProfile
import android.content.Context
import android.net.Uri
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditProfileUiState(

    //data
    val firstNameFlow: StateFlow<String> = MutableStateFlow(""),
    val onFirstNameValueChange: (String) -> Unit = {},
    val firstNameErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    val lastNameFlow: StateFlow<String> = MutableStateFlow(""),
    val onLastNameValueChange: (String) -> Unit = {},
    val lastNameErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    val cityFlow: StateFlow<String> = MutableStateFlow(""),
    val onCityValueChange: (String) -> Unit = {},
    val bioFlow: StateFlow<String> = MutableStateFlow(""),
    val onBioValueChange: (String) -> Unit = {},
    val bioErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    val onSingleBabyBornDate: (Long) -> Unit = {},

    val onMultipleFirstBabyBornDate: (Long) -> Unit = {},
    val onMultipleSecondBabyBornDate: (Long) -> Unit = {},
    val onMultipleThirdBabyBornDate: (Long) -> Unit = {},


    val profilePicFlow: StateFlow<String> = MutableStateFlow(""),
    val captureUri: StateFlow<Uri?> = MutableStateFlow(null),


    val singleMultipleOption: StateFlow<Int> = MutableStateFlow(0),


    val tempSingleDateFlow: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList()),
    val tempMultipleDateFlow: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList()),


    val openPickImgDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onOpenORDismissDialog: (Boolean) -> Unit = {},
    val onImageFlag: (Boolean) -> Unit = {},


    val launchCamera: StateFlow<Boolean> = MutableStateFlow(false),


    //event
    val onEditProfileAPICall: () -> Unit = {},
    val onEditProfileClick: (ProfileData) -> Unit = {},
    val onSingleClick: () -> Unit = {},
    val onMultipleClick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val onProfileImgPick: (String) -> Unit = {},
    val onClearUnUsedUseState: () -> Unit = {},
    val onClickOfCamera: (Context) -> Unit = {},

    //api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<ProfileData>>?> = MutableStateFlow(null),

    //response clear
    val clearAllApiResultFlow: () -> Unit = {},


    )


