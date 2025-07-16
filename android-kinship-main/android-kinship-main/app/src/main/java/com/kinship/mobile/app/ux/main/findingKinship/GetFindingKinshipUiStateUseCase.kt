package com.kinship.mobile.app.ux.main.findingKinship
import android.content.Context
import android.content.Intent
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetFindingKinshipUiStateUseCase
@Inject constructor(

) { operator fun invoke(
    context: Context,
    coroutineScope: CoroutineScope,
    navigate: (NavigationAction) -> Unit,
): FindingKinshipUiState {
    navigateToHomeScreen(context, navigate, coroutineScope)
    return FindingKinshipUiState()
}private fun navigateToHomeScreen(context: Context, navigate: (NavigationAction) -> Unit, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        delay(2000)
        val intent = Intent(context, MainActivity::class.java)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))

    }
}
}