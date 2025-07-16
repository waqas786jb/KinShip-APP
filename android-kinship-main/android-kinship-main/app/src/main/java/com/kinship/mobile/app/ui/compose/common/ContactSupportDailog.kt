package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun ContactSupportDialogPreview() {
    ContactSupportStateDialog(
        onDismissRequest = { /*TODO*/ },
        description = "The contact Support request has been send to the support",
        title = "Contact Support",

        )


}

@Composable
fun ContactSupportStateDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    description: String,
    onPositiveClick: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                ,
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White)

        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.padding(7.dp))
                Text(
                    text = title,
                    color = AppThemeColor,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = description,
                    color = Black,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(15.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    onClick = onPositiveClick,
                    color = AppThemeColor,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "Ok",
                        fontWeight = FontWeight.W400,
                        fontFamily = OpenSans,
                        fontSize = 15.sp,
                        color = White,
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 7.dp)
                    )
                }

            }
        }
    }
}



