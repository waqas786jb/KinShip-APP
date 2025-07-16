package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun DialogWithDoubleButtonPreview() {
    val email = remember {
        mutableStateOf("")
    }
    DialogWithInputAndButton(
        onDismissRequest = { /*TODO*/ },
        title = "Please tell us why you’re leaving your kinship.",
        negativeText = "Cancel",
        positiveText = "Logout",
        value = email.value,
        onValueChange = { email.value = it }
    )
}
@Composable
fun DialogWithInputAndButton(
    onDismissRequest: () -> Unit = {},
    title: String,
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
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp),

                ) {
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .paddingFromBaseline(top = 15.dp, bottom = 2.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Surface(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = HoneyFlower50,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .heightIn(115.dp),

                    contentColor = White,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .background(color = White)

                    ) {
                        TextField(
                            value = value,
                            onValueChange = onValueChange,
                            singleLine = false,
                            modifier = Modifier
                                .align(alignment = Alignment.Top),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = White,
                                unfocusedIndicatorColor = White,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White,
                                cursorColor = Black
                            ),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.type_here),
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 11.sp,
                                    color = Black50,
                                    modifier = Modifier.align(alignment = Alignment.Top)
                                        .fillMaxWidth()
                                    )
                            }
                        )

                    }
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
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
                Spacer(modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}



