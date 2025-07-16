package com.kinship.mobile.app.ux.main.findingKinship

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.theme.Black60
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor

@Preview
@Composable
fun FindingKinshipScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: FindingKinshipViewModel = hiltViewModel()

) {
    //  val uiState = viewModel.uiState
    AppScaffold(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        FindingKinshipContent()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun FindingKinshipContent(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = modifier.padding(top = 150.dp))
        Text(
            text = stringResource(R.string.finding_your_kinship),
            color = AppThemeColor, fontSize = 20.sp,
            fontWeight = FontWeight.W400,
            fontFamily = OpenSans
        )
        Spacer(modifier = modifier.padding(top = 150.dp))
        Text(
            text = stringResource(R.string.moms),
            color = Black60, fontSize = 16.sp,
            textAlign = TextAlign.Center, fontWeight = FontWeight.W400,
            fontFamily = OpenSans
        )
    }

}