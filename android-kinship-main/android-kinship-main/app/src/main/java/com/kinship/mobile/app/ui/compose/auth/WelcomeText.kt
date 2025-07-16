package com.kinship.mobile.app.ui.compose.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Beauty
import com.kinship.mobile.app.ui.theme.Black40
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White


@Composable
fun WelComeText(
    header:String,
    title:String,
    ) {
    Column(
        modifier = Modifier.background(color = White)
    ) {
        androidx.compose.material3.Text(
            text = header,
            fontSize = 50.sp,
            fontWeight = FontWeight.W400,
            color = AppThemeColor,
            fontFamily = Beauty,
        )
        androidx.compose.material3.Text(text = title,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            fontSize = 18.sp,
            color = Black40)

    }

}
@Preview
@Composable
fun Text() {
    WelComeText(
        header = stringResource(id = R.string.kinship),
        title = stringResource(R.string.life_together),

        )

    
}

