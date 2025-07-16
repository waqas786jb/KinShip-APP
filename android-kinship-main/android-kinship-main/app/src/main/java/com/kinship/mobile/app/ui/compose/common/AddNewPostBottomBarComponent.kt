package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Composable
fun AddNewPostBottomBarComponent(onImageUploadClick:()->Unit,onPostUpload:()->Unit,) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 20.dp)) {
        Surface(color = BlackLiteF0, shape = RoundedCornerShape(10.dp), onClick = onImageUploadClick) {
            Image(
                (painterResource(R.drawable.ic_add_event)),
                contentDescription = null,
                modifier = Modifier.padding(8.dp)

            )
        }
        Spacer(Modifier.weight(1f))
        androidx.compose.material3.Button(
            onClick = {
                onPostUpload()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppThemeColor,
            ),
            modifier = Modifier

                .heightIn(30.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                Text(
                    text = "post",
                    fontWeight = FontWeight.W400,
                    fontFamily = OpenSans,
                    fontSize = 18.sp,
                    color = White
                )
                Spacer(Modifier.padding(3.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_send_post),
                    contentDescription = null, modifier = Modifier.padding(top = 3.dp)
                )

            }
        }
    }

}
