package com.kinship.mobile.app.ui.compose.countryCode

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White


@Composable
fun CountryCodePickerComponent(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    title: String,
    onClick: () -> Unit = {},
    countryCode: (Int) -> Unit = {},
    @DrawableRes trailingIcon: Int? = null,
    isLeadingIconVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    modifier: Modifier = Modifier,
    textSize: TextUnit = 18.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    isHeaderVisible: Boolean = false,
    setCountryCode: String? = null
) {
    val isError = !errorMessage.isNullOrBlank()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isHeaderVisible) {
            Text(
                text = title,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                color = Black23,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 18.dp)
            )
        }
        Surface(
            onClick = onClick,
            modifier = modifier.border(
                width = 1.dp, color = if (isError) MaterialTheme.colorScheme.error else HoneyFlower50,
                RoundedCornerShape(50.dp),


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
                    Image(
                        painter = painterResource(leadingIcon),
                        contentDescription = "User",
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                OutlinedTextField(
                    value = value, onValueChange = onValueChange,
                    singleLine = true,
                    modifier = modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        errorContainerColor = White,
                        focusedIndicatorColor = White,
                        unfocusedIndicatorColor = White,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedTextColor = Color.Black
                    ),
                    keyboardOptions = keyboardOptions,
                    leadingIcon = {
                        KomposeCountryCodePickerNew(
                            modifier = Modifier
                                .size(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.Red,
                                unfocusedTextColor = Color.Red,
                                focusedContainerColor = White,
                                focusedSupportingTextColor = Color.Red,
                                focusedLabelColor = Color.Red,
                                focusedIndicatorColor = Color.Red,
                                unfocusedContainerColor = White,
                                errorContainerColor = White
                            ),
                            showOnlyCountryCodePicker = true,
                            showCountryFlag = false,
                            defaultCountryCode = setCountryCode
                        )

                    },
                    visualTransformation = visualTransformation,
                    placeholder = {
                       Text(
                            text = header,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            color = Black50,

                            )
                    },

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

    //  if(isError && !errorMessage.isNullOrBlank()) {


    // }
}

@Composable
private fun DefaultPlaceholder(defaultLang: String) {
    Text(
        text = stringResource(id = getNumberHint(allCountries.single { it.countryCode == defaultLang }.countryCode.lowercase())),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.ExtraLight,
        ),
    )
}


@Preview
@Composable
fun TextFiledNewPreviewNew2(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        val email = remember {
            mutableStateOf("")
        }
        CountryCodePickerComponent(
            isHeaderVisible = true,
            title = stringResource(R.string.password),
            isLeadingIconVisible = true,
            value = email.value,
            onValueChange = { email.value = it },
            leadingIcon = R.drawable.ic_password,
            header = stringResource(id = R.string.password),
        )

    }
}

/*
@Composable
fun OutlineTextFieldComponent2(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    title: String,
    onClick: () -> Unit = {},
    leadingIconShow: () -> Unit = {},
    @DrawableRes trailingIcon: Int? = null,
    isLeadingIconVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    modifier: Modifier = Modifier,
    textSize: TextUnit = 18.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    isHeaderVisible: Boolean = false
) {
    val isError = !errorMessage.isNullOrBlank()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isHeaderVisible) {
            Text(
                text = title,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                color = Black23,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 18.dp)
            )
        }
        Surface(
            onClick = onClick,
            modifier = modifier.border(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else PinkE9,
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
                    Image(
                        painter = painterResource(leadingIcon),
                        contentDescription = "User",
                        modifier = Modifier.padding(start = 15.dp)

                    )
                }
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    modifier = modifier.fillMaxWidth(0.8f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = White,
                        unfocusedIndicatorColor = White,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = Black
                    ),
                   leadingIcon ={
                                leadingIconShow
                   } ,
                    visualTransformation = visualTransformation,
                    placeholder = {
                        Text(
                            text = header,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            color = Black50,
                        )
                    }
                )
                Row {
                    Spacer(modifier = Modifier.padding(5.dp))
                    if (isTrailingIconVisible && trailingIcon != null) {
                        IconButton(onClick = { onTogglePasswordVisibility() }) {
                            Image(
                                painter = painterResource(trailingIcon),
                                contentDescription = "User",
                            )
                        }
                    }
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
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
@Preview
@Composable
fun TextFiledNewPreviewNew2(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        val email = remember {
            mutableStateOf("")
        }
        OutlineTextFieldComponent(
            isHeaderVisible = true,
            isTrailingIconVisible = true,
            title = stringResource(R.string.password),
            isLeadingIconVisible = true,
            value = email.value,
            errorMessage = "Error",
            textSize = 40.sp,
            onValueChange = { email.value = it },
            trailingIcon = R.drawable.ic_hide_password_key,
            leadingIcon = R.drawable.ic_password,
            header = stringResource(id = R.string.password),
        )

    }
}
*/
