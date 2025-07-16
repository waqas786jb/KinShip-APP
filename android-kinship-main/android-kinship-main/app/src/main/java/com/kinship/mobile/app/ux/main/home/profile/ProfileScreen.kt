package com.kinship.mobile.app.ux.main.home.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.MultipleLineComponentText
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black85
import com.kinship.mobile.app.ui.theme.OpenSans

import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel(),
) {

    val uiState = viewModel.uiState
    val isSelfHide = uiState.memberUserId == uiState.userId


    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = uiState.memberName,
                isBackVisible = true,
                isTrailingIconVisible = !isSelfHide,
                trailingIcon = if (!isSelfHide) R.drawable.ic_profile_chat else null ,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()

                },
                onTrailingIconClick = {
                    uiState.navigateToSingleGroupChat()
                }
            )
        },
        navBarData = null
    ) {
        ProfileScreenContent(uiState)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)

}

@Composable
fun ProfileScreenContent(uiState: ProfileUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 30.dp, end = 20.dp)
            .imePadding()
            .background(White)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        AsyncImage(
            model = uiState.memberProfile,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(id = R.drawable.ic_placeholder),
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp), contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(35.dp)
        ) {
            ProfileDetailsText(uiState)
        }
    }
}

@Composable
fun ProfileDetailsText(uiState: ProfileUiState) {
    Spacer(modifier = Modifier.padding(5.dp))
    Row {
        Text(
            text = stringResource(R.string.name), fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            color = Black85,
            modifier = Modifier, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        // if (data.location.isNullOrEmpty()) "-" else data.location
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = if (uiState.memberName.isNullOrEmpty()) "-" else uiState.memberName,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            color = AppThemeColor,
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )


    }
    Row {
        Text(
            text = "City :", fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            color = Black85,
            modifier = Modifier, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = if (uiState.memberCite.isNullOrEmpty()) "-" else uiState.memberCite,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            color = AppThemeColor,
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    Row {
        Text(
            text = stringResource(id = R.string.birthdate_), fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            color = Black85,
            modifier = Modifier, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = if (uiState.memberBirthDay.isNullOrEmpty()) "-" else uiState.memberBirthDay,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            color = AppThemeColor,
            modifier = Modifier
                .fillMaxWidth(), maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
    MultipleLineComponentText(
        header = uiState.memberBio,
        isHeaderVisible = true,
        title = stringResource(R.string.bio_title)
    )
}


@Preview
@Composable
fun Preview() {
    val uiState = ProfileUiState()
    ProfileScreenContent(uiState)

}