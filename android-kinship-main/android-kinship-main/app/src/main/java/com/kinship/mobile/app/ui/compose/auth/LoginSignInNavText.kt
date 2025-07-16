    package com.kinship.mobile.app.ui.compose.auth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black60
import com.kinship.mobile.app.ui.theme.OpenSans


@Composable
fun LogInSignInNavText(
    description: String,
    destinationName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,

    ) {
    Column() {

        Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
            Text(
                text = description,
                fontFamily = OpenSans,
                color = Black60,

                fontWeight = FontWeight.W400,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = destinationName,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                color = AppThemeColor,
                modifier = modifier.clickable(onClick = onClick)

            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
    }



}


