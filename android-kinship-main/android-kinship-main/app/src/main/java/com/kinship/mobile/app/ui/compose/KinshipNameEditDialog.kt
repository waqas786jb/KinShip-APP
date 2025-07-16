package com.kinship.mobile.app.ui.compose
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.ui.theme.Black30
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor

import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun KinshipNameEditDialogPreview() {
    val email = remember {
        mutableStateOf("")
    }
    KinshipNameEditDialog(
        onDismissRequest = { /*TODO*/ },
        negativeText = "Cancel",
        positiveText = "Save",
        value = email.value,
        onValueChange = { email.value = it }
    )
}


@Composable
fun KinshipNameEditDialog(
    onDismissRequest: () -> Unit = {},
    negativeText: String,
    positiveText: String,
    onPositiveClick: () -> Unit = {},
    value: String,
    onValueChange: (String) -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        // Draw a rectangle shape with rounded corners inside the dialog

        Card(
            modifier = Modifier
                .fillMaxWidth(),

            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(15.dp))
                Text(
                    text = "Change your kinship name",
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    maxLines = 1,

                    modifier = Modifier.fillMaxWidth()
                        .offset(y = 10.dp)
                        .padding(),

                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = Black30,
                        focusedTextColor = AppThemeColor,
                        unfocusedTextColor = AppThemeColor

                    ),
                    placeholder = {
                        Text(
                            text = "Kinship name",
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            color = Black50,

                        )
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp))
                Spacer(modifier = Modifier.padding(15.dp))

                Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                    androidx.compose.material3.Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Color.Blue
                        ),
                        border = BorderStroke(width = 1.dp, color = AppThemeColor),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(30.dp)
                    ) {
                        Text(
                            text = negativeText,
                            fontWeight = FontWeight.W400,
                            fontFamily = OpenSans,
                            fontSize = 12.sp,
                            color = AppThemeColor
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                    androidx.compose.material3.Button(
                        onClick = onPositiveClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppThemeColor,
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(width = 1.dp, color = AppThemeColor),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(30.dp)
                    ) {
                        Text(
                            text = positiveText,
                            fontWeight = FontWeight.W400,
                            fontFamily = OpenSans,
                            fontSize = 12.sp,
                            color = White
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
            }

        }


    }
}



