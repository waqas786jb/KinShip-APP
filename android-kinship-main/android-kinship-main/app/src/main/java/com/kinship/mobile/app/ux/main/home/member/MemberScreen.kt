package com.kinship.mobile.app.ux.main.home.member

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun MemberScreen(
    navController: NavController = rememberNavController(),
    viewModel: MemberViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = "Members [${uiState.memberCount}]",
                isLineVisible = true,
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = null
    ) {
        MemberScreenContent(uiState)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.groupMemberAPICall()
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun MemberScreenContent(uiState: MemberUiState) {
    val groupMemberList by uiState.groupMemberList.collectAsStateWithLifecycle()
    // val member = uiState.memberList
    LazyColumn {
        items(groupMemberList) { member ->
            MemberListItem(member, uiState)
        }
    }
}

@Composable
fun MemberListItem(member: GroupMember, uiState: MemberUiState) {
    val isSelfHide = member.userId == uiState.userId
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(15.dp)
            .clickable {
                uiState.onProfileClickButton(member)
            },
    ) {
        AsyncImage(
            //replace with image from server
            model = member.profileImage,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(id = R.drawable.ic_placeholder),
            modifier = Modifier
                .clip(CircleShape)
                .size(35.dp), contentScale = ContentScale.Crop
        )
        Text(
            text = member.firstName.plus(" ").plus(member.lastName),
            fontSize = 15.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!isSelfHide) {
            Image(
                painter = painterResource(id = R.drawable.ic_chat), contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        uiState.navigateToSingleGroupChat(member)
                    }
            )
        }

    }
}

@Preview
@Composable
fun Preview() {
    val uiState = MemberUiState()
    MemberScreenContent(uiState = uiState)

}



