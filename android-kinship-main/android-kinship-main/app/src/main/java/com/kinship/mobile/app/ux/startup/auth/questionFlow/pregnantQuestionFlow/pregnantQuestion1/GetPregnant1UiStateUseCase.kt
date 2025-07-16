package com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.pregnantRequest.PregnantRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails.UserProfileRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class GetPregnant1UiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val dobFlow = MutableStateFlow("")
    private val singleOrMultiplePregnancyFlow = MutableStateFlow(1)
    private val singleGenderPregnancyFlow = MutableStateFlow(0)
    private val multipleGenderPregnancyFlow = MutableStateFlow(0)
    private val dobErrorFlow = MutableStateFlow<String?>(null)
    private val firstTimeMomFlow = MutableStateFlow(1)
    private val kinshipReason = MutableStateFlow(2)
    private var singleGenderList: ArrayList<String> = arrayListOf()
    private var multipleGenderList: ArrayList<String> = arrayListOf()
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): Pregnant1UiState {
        return Pregnant1UiState(
            onUserProfileAPICall = {
                if (isPregnantInfoValid(context)) {
                    makeResendOtpReq(coroutineScope)
                }
            },
            onUserProfileClick = {
                navigate(NavigationAction.Navigate(UserProfileRoute.createRoute()))
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            //
            onSelectedDateChange = { dob ->
                val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                val date: Date = formatter.parse(dob) as Date
                dobFlow.value = date.time.toString()
                dobErrorFlow.value = null
                Log.d("TAG", "selectDate: ${dobFlow.value}")
            },
            dobErrorFlow = dobErrorFlow,
            onGirlValue = {
                singleGenderPregnancyFlow.value = 1
            },
            onBoyValue = {
                singleGenderPregnancyFlow.value = 2
            },
            onItSurpriseValue = {
                singleGenderPregnancyFlow.value = 3
                Log.d("TAG", "invoke: ${singleGenderPregnancyFlow.value}")
            },
            onSingleClick = {
                singleOrMultiplePregnancyFlow.value = 1
                multipleGenderList.clear()
            },
            onMultipleClick = {
                singleOrMultiplePregnancyFlow.value = 2
                singleGenderList.clear()
            },
            onYesClick = {
                firstTimeMomFlow.value = 1
            },
            onNoClick = {
                firstTimeMomFlow.value = 2
            },
            onSingleGenderList = {
                singleGenderList.add(it)
                multipleGenderList.clear()
            },
            onMultipleGenderList = {
                multipleGenderList.add(it)
                singleGenderList.clear()
            },
            apiResultFlow = apiResultFlow,
            onMultipleGirlValue = {
                multipleGenderPregnancyFlow.value = 1
            },
            onMultipleBoyValue = {
                multipleGenderPregnancyFlow.value = 2
            },
            onMultipleBothValue = {
                multipleGenderPregnancyFlow.value = 3
            },
            onMultipleItSurpriseValue = {
                multipleGenderPregnancyFlow.value = 4
            },
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            }
        )
    }

    private fun makeResendOtpReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val pregnantRequest = PregnantRequest(
                step = 1,
                whenIsYourDueDate = dobFlow.value,
                singleOrMultiplePregnancy = singleOrMultiplePregnancyFlow.value,
                singleGender = singleGenderPregnancyFlow.value,
                kinshipReason = kinshipReason.value,
                multipleGender = multipleGenderPregnancyFlow.value,
                firstTimeMom = firstTimeMomFlow.value,
            )
            callResendOtpAPI(pregnantRequest, coroutineScope)
        }
    }

    private fun callResendOtpAPI(pregnantRequest: PregnantRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.pregnantRequest(pregnantRequest).collect {
                apiResultFlow.value = it

            }
        }
    }

    private fun isPregnantInfoValid(context: Context): Boolean {
        var validInfo = true
        if (dobFlow.value.isBlank()) {
            dobErrorFlow.value = context.getString(R.string.select_due_date)
            validInfo = false
        }
        if (singleOrMultiplePregnancyFlow.value == 1) {
            if (singleGenderList.isEmpty()) {
                Toasty.warning(
                    context,
                    context.getString(R.string.please_select_the_gender),
                    Toast.LENGTH_SHORT,
                    false
                )
                    .show()

                validInfo = false
            }
        } else {
            if (multipleGenderList.isEmpty()) {
                Toasty.warning(
                    context,
                    context.getString(R.string.please_select_the_gender),
                    Toast.LENGTH_SHORT,
                    false
                )
                    .show()
                validInfo = false
            }
        }
        return validInfo
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null

    }

}