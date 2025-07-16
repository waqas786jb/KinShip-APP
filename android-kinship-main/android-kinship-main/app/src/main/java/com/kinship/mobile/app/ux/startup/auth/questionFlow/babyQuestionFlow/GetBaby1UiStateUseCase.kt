package com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.babyRequest.BabyRequest
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

class GetBaby1UiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val singleBabyBornFlow = MutableStateFlow("")
    private val multipleFirstBabyBornFlow = MutableStateFlow("")
    private val multipleSecondBabyBornFlow = MutableStateFlow("")
    private val babySingleBornList: ArrayList<String> = arrayListOf()
    private val babyMultipleBornList: ArrayList<String> = arrayListOf()


    private val singleDobErrorFlow = MutableStateFlow<String?>(null)
    private val multipleFirstDobErrorFlow = MutableStateFlow<String?>(null)
    private val multipleSecondDobErrorFlow = MutableStateFlow<String?>(null)
    private val singleOrMultipleBirthFlow = MutableStateFlow(1)
    private val singleGenderBabyPregnancyFlow = MutableStateFlow(0)
    private val multipleGenderBabyPregnancyFlow = MutableStateFlow(0)
    private val firstTimeMomFlow = MutableStateFlow(1)
    private val childFlow = MutableStateFlow(2)
    private val kinshipReason = MutableStateFlow(3)
    private var singleGenderList: ArrayList<String> = arrayListOf()
    private var multipleGenderList: ArrayList<String> = arrayListOf()
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): Baby1UiState {
        return Baby1UiState(
            onUserDetailsAPICall = {
                if (isBabyInfoValid(context)) {
                    makeUserProfileInReq(coroutineScope)
                }
            },

            onUserDetailsCLick = {
                navigate(NavigationAction.Navigate(UserProfileRoute.createRoute()))

            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            onSingleBabyBornDate = { dob ->
                val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                val date: Date = formatter.parse(dob) as Date
                singleBabyBornFlow.value = date.time.toString()
                babySingleBornList.add(singleBabyBornFlow.value)
                singleDobErrorFlow.value = null
            },
            onMultipleFirstBabyBornDate = { dob ->
                val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                val date: Date = formatter.parse(dob) as Date
                multipleFirstBabyBornFlow.value = date.time.toString()
                babyMultipleBornList.add(multipleFirstBabyBornFlow.value)
                multipleFirstDobErrorFlow.value = null
                Log.d("TAG", "selectDate: ${multipleFirstBabyBornFlow.value}")
            },
            onMultipleSecondBabyBornDate = { dob ->
                val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                val date: Date = formatter.parse(dob) as Date
                multipleSecondBabyBornFlow.value = date.time.toString()
                babyMultipleBornList.add(multipleSecondBabyBornFlow.value)
                multipleSecondDobErrorFlow.value = null
                Log.d("TAG", "selectDate: ${multipleSecondBabyBornFlow.value}")
            },
            onSingleClick = {
                singleOrMultipleBirthFlow.value = 1
                babyMultipleBornList.clear()

            },
            onMultipleClick = {
                singleOrMultipleBirthFlow.value = 2
                babySingleBornList.clear()
            },
            singleBabyBornDateErrorFlow = singleDobErrorFlow,
            multipleFirstBabyBornDateErrorFlow = multipleFirstDobErrorFlow,
            multipleSecondBabyBornDateErrorFlow = multipleSecondDobErrorFlow,

            onSingleGirlValue = {
                singleGenderBabyPregnancyFlow.value = 1
            },
            onSingleBoyValue = {
                singleGenderBabyPregnancyFlow.value = 2
            },
            onMultipleGirlValue = {
                multipleGenderBabyPregnancyFlow.value = 1
            },
            onMultipleBoyValue = {
                multipleGenderBabyPregnancyFlow.value = 2
            },
            apiResultFlow = apiResultFlow,
            onMultipleBothValue = {
                multipleGenderBabyPregnancyFlow.value = 3
            },
            onChildYesClick = {
                childFlow.value = 1
            },
            onChildNoClick = {
                childFlow.value = 2
            },
            onFirstTimeMomYesClick = {
                firstTimeMomFlow.value = 1
            },
            onFirstTimeMomNoClick = {
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
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            }
        )
    }

    private fun makeUserProfileInReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val babyRequest = BabyRequest(
                step = 1,
                kinshipReason = kinshipReason.value,
                singleGender = singleGenderBabyPregnancyFlow.value,
                multipleGender = multipleGenderBabyPregnancyFlow.value,
                singleOrMultipleBirth = singleOrMultipleBirthFlow.value,
                babyBornDate = if (singleOrMultipleBirthFlow.value == 1) babySingleBornList else babyMultipleBornList,
                childHasSpecialNeed = childFlow.value,
                firstTimeMom = firstTimeMomFlow.value,
            )
            callUserProfileAPI(babyRequest, coroutineScope)
        }
    }

    private fun callUserProfileAPI(babyRequest: BabyRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.babyRequest(babyRequest).collect {
                apiResultFlow.value = it
            }
        }
    }


    private fun isBabyInfoValid(context: Context): Boolean {
        var validInfo = true

        if (singleOrMultipleBirthFlow.value == 1) {
            if (singleBabyBornFlow.value.isBlank()) {
                singleDobErrorFlow.value = context.getString(R.string.please_select_baby_born)
                validInfo = false
            }
            if (singleGenderList.isEmpty()) {
                Toasty.warning(context, context.getString(R.string.please_select_babies_sex), Toast.LENGTH_SHORT, false)
                    .show()
                    //please select the gender
                multipleGenderList.clear()
                validInfo = false
            }
        } else if (singleOrMultipleBirthFlow.value == 2) {
            if (multipleFirstBabyBornFlow.value.isBlank()) {
                multipleFirstDobErrorFlow.value = context.getString(R.string.please_select_babies_born_date)
                validInfo = false
            }
            if (multipleSecondBabyBornFlow.value.isBlank()) {
                multipleSecondDobErrorFlow.value = context.getString(R.string.please_select_babies_born_date)
                //
                validInfo = false
            }
            if (multipleGenderList.isEmpty()) {
                Toasty.warning(context, context.getString(R.string.please_select_the_gender), Toast.LENGTH_SHORT, false)
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