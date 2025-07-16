@file:Suppress("NAME_SHADOWING", "DEPRECATION")

package com.kinship.mobile.app.ux.container.communityFeature.suggestion

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldMultipleLine
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithOutTrailingIcon
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledDateSelectComponent
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.White

@Composable
fun NewSuggestionScreen(
    navController: NavController = rememberNavController(),
    viewModel: NewSuggestionViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.send_new_suggestion),
                isLineVisible = true, isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    onClick = { uiState.onClickOfSend() },
                    text = stringResource(R.string.send),
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                )
            }
        )
    ) {
        SuggestionContent(uiState = uiState)
    }

    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    if (showLoader) {
        CustomLoader()
    }

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun SuggestionContent(uiState: NewSuggestionUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val suggestionState by uiState.suggestionState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                keyboardController?.hide()
            }
            .padding(vertical = 20.dp, horizontal = 20.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SuggestionTextFields(
            uiState = uiState,
            suggestionDataState = suggestionState
        )
    }
}

@Composable
private fun SuggestionTextFields(
    modifier: Modifier = Modifier,
    uiState: NewSuggestionUiState,
    suggestionDataState: SuggestionDataState?
) {
    val context = LocalContext.current
    val field = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
    LaunchedEffect(Unit) {
        //val apiKey = context.getString(R.string.map_api_key)
        Places.initialize(context, BuildConfig.MAP_API_KEY)
    }
    val intent =
        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field).build(context)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    uiState.onCityValueChange(place.name)
                }
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = result.data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.e("Autocomplete Error", status?.statusMessage ?: "Error")
            }
        }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlineTextFieldWithOutTrailingIcon(
                value = suggestionDataState?.communityName ?: "",
                onValueChange = { uiState.onCommunityValueChange(it) },
                isLeadingIconVisible = true,
                title =stringResource(id = R.string.community_name),
                errorMessage = suggestionDataState?.communityNameErrorMsg ?: "",
                header = stringResource(id = R.string.community_name),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlineTextFiledDateSelectComponent(
                value = suggestionDataState?.city?:"",
                header = stringResource(R.string.city),
                errorMessage = suggestionDataState?.cityErrorMsg,
                title = stringResource(id = R.string.city),
                onClick = {
                    launcher.launch(intent)
                }
            )
            Spacer(Modifier.padding(10.dp))
            
        }
        OutlineTextFieldMultipleLine(
            value = suggestionDataState?.newIdea ?: "",
            errorMessage = suggestionDataState?.newIdeaErrorMsg ?: "",
            onValueChange = {
                uiState.onIdeaValueChange(it)
            },
            isHeaderVisible = true,
            header = stringResource(id = R.string.type_here),
            title = stringResource(id = R.string.idea_for_community)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = NewSuggestionUiState()
    Surface {
        SuggestionContent(uiState = uiState)
    }
}