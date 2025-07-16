package com.kinship.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@Composable
fun OutlineTextFieldWithOutTrailingIcon(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    onClick: () -> Unit = {},
    isLeadingIconVisible: Boolean = false,
    errorMessage: String? = null,
    title: String?=null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    isTitleVisible: Boolean = false

) {
    val interactionSource = remember { MutableInteractionSource() }
    val isError = !errorMessage.isNullOrBlank()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isTitleVisible) {
            title?.let {
                Text(
                    text = it,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    color = Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 18.dp)
                )
            }
        }
        Surface(
            onClick = onClick,
            modifier = modifier.border(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else HoneyFlower50,
                shape = RoundedCornerShape(50.dp)
            ),
            contentColor = White,
            shape = RoundedCornerShape(50.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = White)
                    .fillMaxWidth()
            ) {
                if (isLeadingIconVisible && leadingIcon != null) {
                    val leadingIconColor = if (isError) MaterialTheme.colorScheme.error else AppThemeColor
                    Image(
                        painter = painterResource(leadingIcon),
                        contentDescription = "User",
                        modifier = Modifier.padding(start = 15.dp),
                        colorFilter = ColorFilter.tint(leadingIconColor)
                    )
                }
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    interactionSource = interactionSource,
                    modifier = modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = Black,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    maxLines = 1,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    placeholder = {
                        Text(
                            text = header,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            color = Black50,
                        )
                    }
                )

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
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
@Preview
@Composable
fun TwoTextFiledNewPreviewNew(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        var firstName by remember {
            mutableStateOf("")
        }
        var lastName by remember {
            mutableStateOf("")
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = White)
            ) {
                OutlineTextFieldWithOutTrailingIcon(
                    value = firstName,
                    onValueChange = { firstName = it },
                    isLeadingIconVisible = true,
                    errorMessage = "firstNameError",
                    //title = "What should we call you?",

                    header = stringResource(id = R.string.first_name),
                    title = "",
                    leadingIcon = R.drawable.ic_person,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = true,
                        imeAction = ImeAction.Next
                    )
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = White)
            ) {
                OutlineTextFieldWithOutTrailingIcon(
                    value = lastName,
                    onValueChange = { lastName = it },
                    isLeadingIconVisible = true,
                    errorMessage = "lastNameError",
                    title = stringResource(id = R.string.what_should_we_call_you),
                    header = stringResource(id = R.string.last_name),
                    leadingIcon = R.drawable.ic_person,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                )
            }
        }
    }
}