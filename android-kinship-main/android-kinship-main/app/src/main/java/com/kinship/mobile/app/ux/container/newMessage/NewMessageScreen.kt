@file:OptIn(ExperimentalLayoutApi::class)

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.userStaticData.TempUserGroupData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.compose.common.message.MessageDisplayComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.ux.container.newMessage.MessageNewMessageUiState
import com.kinship.mobile.app.ux.container.newMessage.NewMessageViewModel

@Composable
fun NewMessageScreen(
    navController: NavHostController,
    viewModel: NewMessageViewModel = hiltViewModel(),
) {

    val uiState = viewModel.uiState
    BackHandler(onBack = {

        uiState.onBackClick()
    })
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.new_message),
                isLineVisible = true,
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.Continue),
                    onClick = {
                        uiState.addMemberGroupAPICall()
                    },
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                )
            }
        )
    ) {
        NewMessageContent(
            uiState
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun NewMessageContent(uiState: MessageNewMessageUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            },
    ) {
        UserGroupListView(uiState)
    }
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    if (showLoader) {
        CustomLoader()
    }
}

@Composable
fun UserGroupListView(uiState: MessageNewMessageUiState) {
    val selectedUsers = remember { mutableStateListOf<String>() }
    val member = uiState.memberList.filter { it.userId != uiState.userId }
    Column {
        UserSelector(
            users = selectedUsers
        )
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier,
    ) {
        items(member) { item ->
            item.apply {
                NewMessageItem(
                    profile = item.profileImage,
                    username = item.firstName.plus(" ").plus(item.lastName),
                    onItemClick = { user ->
                        if (selectedUsers.contains(user)) {
                            selectedUsers.remove(user)
                            uiState.onUserList(TempUserGroupData(id =item.userId, isAdd = false))
                        } else {
                            selectedUsers.add(user)
                            uiState.onUserList(TempUserGroupData(id = item.userId, isAdd = true))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NewMessageItem(
    profile: String?,
    username: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var checkedState by rememberSaveable { mutableStateOf(false) }
    val checkedStateItem = if (checkedState) Pair(R.drawable.ic_checked, "Checked") else Pair(
        R.drawable.ic_un_checked,
        "Un Checked"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                checkedState = !checkedState
                onItemClick(username)
            }
    ) {
        MessageDisplayComponent(
            profile = profile,
            username = username,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = {
            onItemClick(username)
            checkedState = !checkedState
        }) {
            Image(
                painter = painterResource(
                    id = checkedStateItem.first
                ), contentDescription = checkedStateItem.second
            )
        }

    }
}


@Composable
private fun UserSelector(
    users: List<String>
) {

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "To :",
            style = TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                color = AppThemeColor
            )
        )

        users.forEach { user ->
            UserSelectorItem(username = user)
        }
    }

   /* LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        item {
            Text(
                text = "To :", style = TextStyle(
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    color = PinkCC
                )
            )
        }

        items(users) { user ->
            UserSelectorItem(username = user)
        }
    }*/

}

@Composable
private fun UserSelectorItem(
    username: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                border = BorderStroke(width = 1.dp, color = AppThemeColor),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = username,
            style = TextStyle(
                fontFamily = OpenSans,
                fontSize = 10.sp,
                lineHeight = 12.sp,  // Adjust lineHeight as needed
                textAlign = TextAlign.Center,
                color = Black

            )
        )
    }
}





