package com.kinship.mobile.app.ui.compose.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black

import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.MineShaftOriginal
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White


@Composable
fun QuestionSubTitle(
    header:String,
    modifier: Modifier=Modifier
) {
    Column(
        modifier = Modifier.background(White)
    ) {
        androidx.compose.material3.Text(
            text = header,
            fontSize = 13.sp,
            color = MineShaftOriginal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            modifier = modifier
                .fillMaxWidth()
               
        )
    }
}
@Preview
@Composable
fun QuestionSubTitlePreview() {
    QuestionSubTitle(header = stringResource(R.string.select_one))
}