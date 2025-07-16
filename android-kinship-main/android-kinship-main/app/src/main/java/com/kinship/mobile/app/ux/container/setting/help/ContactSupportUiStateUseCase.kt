package com.kinship.mobile.app.ux.container.setting.help

import android.content.Context
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.contactSupport.ContactSupportRequest
import com.kinship.mobile.app.navigation.NavigationAction
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class ContactSupportUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val reasonFlow = MutableStateFlow("")
    private val reasonErrorFlow = MutableStateFlow<String?>(null)

    private val descriptionFlow = MutableStateFlow("")
    private val descriptionErrorFlow = MutableStateFlow<String?>(null)

    private val showDialog = MutableStateFlow(false)
    private val showLoader = MutableStateFlow(false)


    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ContactSupportUiState {
        return ContactSupportUiState(
            reasonFlow = reasonFlow,
            onReason = { reasonFlow.value = it;reasonErrorFlow.value = null },
            reasonErrorFlow = reasonErrorFlow,

            descriptionFlow = descriptionFlow,
            ontDescription = { descriptionFlow.value = it;descriptionErrorFlow.value = null },
            descriptionErrorFlow = descriptionErrorFlow,

            showDialog = showDialog,
            onShowDialog = {
                showDialog.value = it
            },
            showLoader = showLoader,

            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            onContactReportSendClick = {
                if (isContactSupportInfoValid(context)) {
                    makeContactReportInReq(coroutineScope, context)
                }
            }
        )
    }

    private fun makeContactReportInReq(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            val contactSupportRequest = ContactSupportRequest(
                reason = reasonFlow.value,
                description = descriptionFlow.value
            )
            callContactReportAPI(contactSupportRequest, coroutineScope, context)
        }
    }

    private fun callContactReportAPI(
        contactSupportRequest: ContactSupportRequest,
        coroutineScope: CoroutineScope,
        context: Context
    ) {
        coroutineScope.launch {
            apiRepository.contactSupport(contactSupportRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false).show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        Toasty.success(context, it.data?.message?:"", Toast.LENGTH_SHORT, false).show()
                        showDialog.value=true
                        reasonFlow.value=""
                        descriptionFlow.value=""
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false).show()
                    }
                }

            }
        }
    }
    private fun isContactSupportInfoValid(context: Context): Boolean {
        var validInfo = true
        if (reasonFlow.value.isBlank()) {
            reasonErrorFlow.value = context.getString(R.string.enter_the_reason)
            validInfo = false
        }
        if (descriptionFlow.value.isBlank()) {
            descriptionErrorFlow.value = context.getString(R.string.enter_the_comment)
            validInfo = false
        }

        return validInfo
    }
}

