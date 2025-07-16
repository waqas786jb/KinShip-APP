package com.kinship.mobile.app.ui.compose.common
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlineTextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    title: String,
    onClick: () -> Unit = {},
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isTitleVisible: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isError = !errorMessage.isNullOrBlank()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isTitleVisible) {
            Text(
                text = title,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W500,
                color = Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 18.dp)
            )
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
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    interactionSource = interactionSource,
                    modifier = modifier.fillMaxWidth()
                        .padding(start = 5.dp)
                        .indicatorLine(
                            enabled = true,
                            isError = false,
                            colors =TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White,
                                cursorColor = Black,
                                focusedLabelColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            interactionSource = interactionSource,
                            focusedIndicatorLineThickness = 0.dp,
                            unfocusedIndicatorLineThickness = 0.dp
                        ),
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
fun OutlineTextFiledComponent(
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
            isTitleVisible = true,
            title = stringResource(R.string.password),
            value = email.value,
            errorMessage = "Error",
            onValueChange = { email.value = it },
            header = stringResource(id = R.string.password),
        )

    }
}