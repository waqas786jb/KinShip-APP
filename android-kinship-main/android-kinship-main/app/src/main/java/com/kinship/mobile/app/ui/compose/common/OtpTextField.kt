package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 4,
    errorMessage: String? = null,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    // Check if there's any error in OTP input


    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.NumberPassword
        ).copy(imeAction = ImeAction.Next),
        decorationBox = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = otpText,
                            borderColor = if (otpText.length == index) HoneyFlower50 else HoneyFlower50,
                            errorMessage = errorMessage
                        )
                        if (index < otpCount - 1) {
                            Spacer(modifier = Modifier.weight(1f)) // Adjust the space as needed
                        }
                    }
                }
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 16.dp
                        ) // Adjust padding as needed
                    )
                }
            }


        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String,
    borderColor: Color,
    errorMessage: String? = null,
) {
    val char = text.getOrNull(index)?.toString() ?: ""

    Box(
        modifier = Modifier
            .widthIn(65.dp)
            .heightIn(50.dp)
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(50.dp)
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )



    }
}
/*@Preview
@Composable
fun CharViewPreview() {
    CharView(index = 0, text = "1")
}*/
//covert my code
