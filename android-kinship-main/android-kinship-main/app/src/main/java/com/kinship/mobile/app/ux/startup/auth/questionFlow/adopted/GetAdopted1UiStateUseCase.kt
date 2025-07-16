package com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.adoptedRequest.AdoptedRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails.UserProfileRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

class GetAdopted1UiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val dobErrorFlow = MutableStateFlow<String?>(null)
    private val dobFlow = MutableStateFlow("")
    private val singleGenderPregnancyFlow = MutableStateFlow(0)
    private val babyBornList : ArrayList<String> = arrayListOf()
    private var singleGenderList: ArrayList<String> = arrayListOf()
    private val childFlow = MutableStateFlow(2)
    private val kinshipReason = MutableStateFlow(4)
    private val singleOrMultiplePregnancyFlow = MutableStateFlow(1)

    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): Adopted1UiState {
        return Adopted1UiState(
            onUserDetailsAPICall = {
                if (isAdoptedInfoValid(context)) {
                    makeAdoptedProfileInReq(coroutineScope)
                }
            },
            onUserDetailsAPICLick ={
                navigate(NavigationAction.Navigate(UserProfileRoute.createRoute()))

            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            onSelectedDate = { dob ->
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val date = formatter.parse(dob)
                if (date != null) {
                    val millis = date.time.toString()
                    // Remove all existing dates from babyBornList
                    babyBornList.clear()
                    // Add the latest selected date
                    babyBornList.add(millis)
                    // Update dobFlow.value
                    dobFlow.value = millis
                    // Clear any previous error message
                    dobErrorFlow.value = null
                    Log.d("TAG", "selectDate: $millis")
                } else {
                    // Handle parsing error if necessary
                    dobErrorFlow.value = context.getString(R.string.invalid_date_format) // Example error message
                }
            },
            apiResultFlow = apiResultFlow,
            onGirlValue = {
                singleGenderPregnancyFlow.value = 1
            },
            dobErrorFlow = dobErrorFlow,

            onBoyValue = {
                singleGenderPregnancyFlow.value = 2
            },
            onSingleGenderList = {
                singleGenderList.add(it)
            },
            onChildYesClick = {
                childFlow.value = 1
            },
            onChildNoClick = {
                childFlow.value = 2
            },
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            }

        )

    }

    private fun makeAdoptedProfileInReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val babyRequest = AdoptedRequest(
                step = 1,
                babyBornDate = babyBornList,
                singleGender = singleGenderPregnancyFlow.value,
                childHasSpecialNeed = childFlow.value,
                kinshipReason = kinshipReason.value,
                singleOrMultiplePregnancy = singleOrMultiplePregnancyFlow.value
            )
            callUserProfileAPI(babyRequest, coroutineScope)
        }
    }

    private fun callUserProfileAPI(babyRequest: AdoptedRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.adoptedRequest(babyRequest).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun isAdoptedInfoValid(context: Context): Boolean {
        var validInfo = true
        if (dobFlow.value.isBlank()) {
            dobErrorFlow.value = context.getString(R.string.enter_the_date_of_birth)
            //Enter the date of birth
            validInfo = false
        }
        if (singleGenderList.isEmpty()) {
            Toasty.warning(context,
                context.getString(R.string.please_select_the_gender), Toast.LENGTH_SHORT, false).show()
            validInfo = false
        }

        return validInfo
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null

    }

}