package com.kinship.mobile.app.ux.container.communityFeature.addNewPost


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils.createMultipartBody
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import javax.inject.Inject

class GetAddNewPostUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val showLoader = MutableStateFlow(false)
    private val _communityName = MutableStateFlow("")
    private val communityName: StateFlow<String> get() = _communityName

    private val postTitle = MutableStateFlow("")
    private val _joinCommunity = MutableStateFlow(false)
    private val joinCommunity: StateFlow<Boolean> get() = _joinCommunity
    private val noDataFoundText = MutableStateFlow(false)
    private val communityPostList = MutableStateFlow<List<CommunityPostResponse>>(emptyList())

    private val communityPostFlow = MutableStateFlow("")
    private val openPickImgDialog = MutableStateFlow(false)
    private val launchCamera = MutableStateFlow(false)
    private val captureUri = MutableStateFlow<Uri?>(null)
    private val selectImage = MutableStateFlow(false)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        communityId: String,
        navigate: (NavigationAction) -> Unit,
    ): AddNewPostUiState {
        Log.d("TAG", "communityId: $communityId")
        return AddNewPostUiState(
            showLoader = showLoader,
            communityPostList = communityPostList,
            communityName = this.communityName,
            joinCommunity = this.joinCommunity,
            postTitle = postTitle,
            onPostTitleValueChange = {
                postTitle.value = it
            },
            onClickOfCamera = { createUriForCaptureImg(it) },
            communityPostFlow = communityPostFlow,
            onCommunityPostImgPick = { communityPostFlow.value = it; launchCamera.value = false },
            captureUri = captureUri,
            onOpenORDismissDialog = { openPickImgDialog.value = it },
            launchCamera = launchCamera,
            openPickImgDialog = openPickImgDialog,
            onImageFlag = { selectImage.value = it },
            onBackClick = {
                navigate(NavigationAction.Pop())
              //  navigate(NavigationAction.Navigate(CommunityPostRoute.createRoute(communityId = communityId, communityName = "", joinCommunity = false)))
            },
            addNewPostAPICall = {
                if (isAddNewPostInfoValid(context)){
                    callAddNewPostAPICall(
                        coroutineScope = coroutineScope,
                        context = context,
                        navigate = navigate,
                        communityId = communityId
                    )
                }
            }

        )
    }

    private fun isAddNewPostInfoValid(context: Context): Boolean {
        var validInfo = true
        if (postTitle.value.isBlank() && communityPostFlow.value.isBlank()) {
            Toasty.warning(
                context,
                "You must post image or message",
                Toast.LENGTH_SHORT,
                false
            ).show()
            validInfo = false
        }

        return validInfo
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

    private fun callAddNewPostAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        communityId: String,
    ) {
        val requestBody: HashMap<String, RequestBody> = hashMapOf()
        requestBody[Constants.Community.COMMUNITY_ID] =
            communityId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (postTitle.value.isNotEmpty()) requestBody[Constants.Community.MESSAGE] =
            postTitle.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val communityPostFilePath = communityPostFlow.value
        coroutineScope.launch {
            if (communityPostFilePath.isNotEmpty()) {
                val communityPostFile = File(communityPostFilePath)
                val communityPost = createMultipartBody(communityPostFile, Constants.Community.FILE)
                apiRepository.addNewPostCommunity(req = requestBody, communityPost = communityPost)
                    .collect { handleApiResponse(context, it,navigate) }
            } else {
                apiRepository.addNewPostCommunityWithOutImage(req = requestBody)
                    .collect { handleApiResponse(context, it, navigate) }
            }
        }
    }
    private fun handleApiResponse(
        context: Context,
        networkResult: NetworkResult<ApiResponse<Any>>,
        navigate: (NavigationAction) -> Unit
    ) {
        when (networkResult) {
            is NetworkResult.Error -> {
                showLoader.value = false
                Toasty.error(
                    context,
                    networkResult.data?.message.toString(),
                    Toast.LENGTH_SHORT,
                    false
                ).show()
            }

            is NetworkResult.Loading -> {
                showLoader.value = true
            }
            is NetworkResult.Success -> {
                showLoader.value = false
                Toasty.success(
                    context,
                    networkResult.data?.message ?: "",
                    Toast.LENGTH_SHORT,
                    false
                ).show()
                navigate(NavigationAction.Pop())
            }
            is NetworkResult.UnAuthenticated -> {
                showLoader.value = false
                Toasty.warning(
                    context,
                    networkResult.data?.message.toString(),
                    Toast.LENGTH_SHORT,
                    false
                ).show()
            }
        }
    }

}