package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
@Preview(showBackground = true)
@Composable
private fun WelcomeKinshipDialogPreview() {
    WelcomeKinshipDialog(
        onDismissRequest = { /*TODO*/ },
        title = "We’ve matched you with  11  other [ moms /\n" +
                "mom - to - be ] who we think you'll relate to",

        )
}
@Composable
fun WelcomeKinshipDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    onPositiveClick: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(White)
                .border(width = 1.dp, color = Black50),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 13.dp),
            ) {
                Spacer(modifier = Modifier.padding(15.dp))
                Text(
                    text = "Welcome to your Kinship!",
                    color = AppThemeColor,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = title,
                    color = Black,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Introduce yourself!",
                    color = Black,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    onClick = onPositiveClick,
                    color = AppThemeColor,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "Ok",
                        fontWeight = FontWeight.W400,
                        fontFamily = OpenSans,
                        fontSize = 15.sp,
                        color = White,
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 5.dp)
                    )
                }

            }

        }
    }
}



