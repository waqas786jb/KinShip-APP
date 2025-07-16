package com.kinship.mobile.app.ui.compose.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.HoneyFlower20

@Composable
fun QuestionSelector(
    header: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) HoneyFlower20 else Color.Transparent
    Column() {

        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(50.dp),

            modifier = modifier
                .fillMaxWidth(),
            contentColor = backgroundColor,
            border = BorderStroke(width = 1.dp, HoneyFlower50),
            onClick = onClick,

        )
        {

            Row(
                modifier = modifier
                    .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = header,

                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 15.dp, start = 30.dp),
                    fontSize = 13.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    color = Black50,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (isSelected) {
                    Image(
                        painterResource(id = R.drawable.ic_seletor_tick),
                        modifier = Modifier.padding(end = 30.dp),
                        contentDescription = "Selected",
                    )
                }
            }

        }



    }

    Spacer(modifier = Modifier.padding(10.dp))

}

@Preview
@Composable
fun QuestionSelectorPreview() {
    val selectedQuestionIndex = remember { mutableIntStateOf(0) }
    QuestionSelector(
        header = stringResource(R.string.i_m_trying_to_conceive),
        isSelected = selectedQuestionIndex.value != 0,
        onClick = { selectedQuestionIndex.value = 0 }

    )
}

