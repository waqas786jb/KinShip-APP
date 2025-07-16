package com.kinship.mobile.app.ui.compose.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.OpenSans


@Composable
fun QuestionTitle(
    header: String,

    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Text(
        text = header,
        fontSize = 20.sp,
        color = AppThemeColor,
        fontFamily = OpenSans,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.W400,
        modifier = modifier
            .fillMaxWidth()

    )
}

@Preview
@Composable
fun QuestionTitlePreview() {
    QuestionTitle(header = stringResource(id = R.string.what_brings_you_to_kinship))
}
    
