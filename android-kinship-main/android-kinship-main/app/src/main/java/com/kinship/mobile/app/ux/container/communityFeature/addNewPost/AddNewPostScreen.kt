package com.kinship.mobile.app.ux.container.communityFeature.addNewPost

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.AddNewPostBottomBarComponent
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils

@Preview
@Composable
fun AddNewPostScreen(
    navController: NavController = rememberNavController(),
    viewModel: AddNewPostViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val keyboardController = LocalSoftwareKeyboardController.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = "Add New Post",
                isLineVisible = true,
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                    keyboardController?.hide()
                }
            )
        },
    ) {
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
        AddNewPostContent(uiState)

    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun AddNewPostContent(uiState: AddNewPostUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val showDialog by uiState.openPickImgDialog.collectAsStateWithLifecycle()
    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e(ContentValues.TAG, "Camera Permission ${result.resultCode}")
        }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    val uri by uiState.captureUri.collectAsStateWithLifecycle()
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val selectedProfileImagePath = AppUtils.getFileFromContentUri(
                    context, uri,
                    Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
                )?.absolutePath ?: ""
                uiState.onCommunityPostImgPick(selectedProfileImagePath)
            } else {
                Log.e("Profile", "OpenPhotoPicker: No media selected")
            }
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            if (captured) {
                capturedImageUri = uri ?: Uri.EMPTY
                val selectedProfileImagePath = AppUtils.getFileFromContentUri(
                    context, capturedImageUri,
                    Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
                )?.absolutePath ?: ""
                uiState.onCommunityPostImgPick(selectedProfileImagePath)
            } else {
                uiState.onClearUnUsedUseState()
            }
        }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            uiState.onClickOfCamera(context)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable( indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                keyboardController?.hide()
            }
            .padding(horizontal = 20.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            AddNewPostView(uiState)
        }
        AddNewPostBottomBarComponent(onPostUpload = {
            uiState.addNewPostAPICall()

        }, onImageUploadClick = {
            uiState.onOpenORDismissDialog(true)
        })
    }
    if (showDialog) {
        CameraGalleryDialog(
            onDismissRequest = { uiState.onOpenORDismissDialog(false) },
            topText = stringResource(id = R.string.gallery),
            bottomText = stringResource(id = R.string.camera),
            onTopClick = {
                uiState.onOpenORDismissDialog(false)
                uiState.onImageFlag(true)
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onBottomClick = {
                uiState.onOpenORDismissDialog(false)
                uiState.onImageFlag(true)
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    uiState.onClickOfCamera(context)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
    val launchCamera by uiState.launchCamera.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (launchCamera && !isLandscape) { // Skip launching in landscape
        uri?.let { validUri ->
            Logger.e("capture uri: $validUri")
            cameraLauncher.launch(validUri)
        } ?: Logger.e("URI is null, cannot launch camera")
    } else if (isLandscape) {
        Logger.e("Skipping camera launch in landscape mode")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewPostView(uiState: AddNewPostUiState) {
    val interactionSource = remember { MutableInteractionSource() }
    val postTitle by uiState.postTitle.collectAsStateWithLifecycle()
    val communityPost by uiState.communityPostFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)

    ) {
        communityPost.takeIf { it.isNotEmpty() }?.let {
            Spacer(Modifier.padding(10.dp))
            AsyncImage(
                model = communityPost,
                contentDescription = stringResource(R.string.post_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .padding()
                    .height(200.dp)
            )
        }
        Spacer(Modifier.padding(3.dp))
        BasicTextField(
            value = postTitle,
            onValueChange = {
                uiState.onPostTitleValueChange(it)
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier,
            interactionSource = interactionSource,
            readOnly = false,
            enabled = true,
        ) {
            TextFieldDefaults.DecorationBox(
                value = postTitle,
                innerTextField = it,
                singleLine = true,
                enabled = true,
                placeholder = {
                    if (postTitle.isEmpty()) {
                        Text(
                            text = "Type here",
                            fontFamily = OpenSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Black50,
                        )
                    }
                },
                interactionSource = interactionSource,
                // change the padding
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                    top = 5.dp, bottom = 10.dp, end = 5.dp, start = 1.dp
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    errorIndicatorColor = Color.White,
                ),
                visualTransformation = VisualTransformation.None
            )
        }
    }
}

@Preview
@Composable
fun AddNewPostViewPreview(modifier: Modifier = Modifier) {
    val uiState = AddNewPostUiState()
    AddNewPostView(uiState)
}






