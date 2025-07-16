package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.Surface
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.tabSelector.Tab
import com.kinship.mobile.app.ui.theme.Alto
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.CreamBrulee
import com.kinship.mobile.app.ui.theme.MineShaftOriginal
import com.kinship.mobile.app.ui.theme.OpenSans

@Composable
fun CustomTabs(
    tab: List<Tab>,
    isTitleShow:Boolean=false,
    title:String? = null,
    modifier: Modifier = Modifier,
    selectedPosition:Int=0,
    onTabSelected: (Int) -> Unit={}
) {
    val selectedIndex = rememberSaveable { mutableStateOf(selectedPosition) }
    Column {
        Spacer(modifier = modifier.padding(3.dp))
        if (isTitleShow){
            Text(text = title?:"",
                modifier = Modifier
                    .padding(start = 18.dp)
                    .fillMaxWidth(),
                fontFamily = OpenSans,
                fontSize = 14.sp,
                color = Black23,
                fontWeight = FontWeight.W400
            )
            Spacer(modifier = Modifier.padding(3.dp))
        }
        Surface(
            modifier = modifier.size(height = 48.dp, width = 220.dp),
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Alto)
        ) {
            TabRow(selectedTabIndex = selectedIndex.value,
                containerColor = Color(0xffffffff),
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                indicator = { _: List<TabPosition> ->
                    Box {}
                }
            ) {
                tab.forEachIndexed { index, text ->
                    val selected = selectedIndex.value == index
                    LeadingIconTab(
                        icon = {
                            text.icon?.let { Image(painter = it, contentDescription = null) }
                        },

                        modifier = if (selected) Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .weight(1f)
                            .background(
                                color = Alto
                            )
                        else modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                color = Color.White
                            ),
                        selected = selected,
                        onClick = {
                            selectedIndex.value = index
                            onTabSelected(index) // Invoke callback when tab is selected
                        },
                        text = { Text(text = text.text, fontSize = 12.sp, color = MineShaftOriginal, fontWeight = FontWeight.W400, fontFamily = OpenSans) }
                    )

                }

            }

        }

    }
}

@Preview
@Composable
fun Preview(
    modifier: Modifier = Modifier
) {
    val babyList = listOf(
        Tab(stringResource(R.string.yes)),
        Tab(stringResource(R.string.no))
    )
    CustomTabs(
        tab = babyList,
        isTitleShow = true,
        title = stringResource(id = R.string.does_your_child_have_special_needs)
    )
}