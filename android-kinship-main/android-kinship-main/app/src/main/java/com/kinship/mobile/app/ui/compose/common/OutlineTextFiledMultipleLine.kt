package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@Composable
fun OutlineTextFieldMultipleLine(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    title: String,
    errorMessage: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,

    isHeaderVisible: Boolean = false,
) {

    val isError = !errorMessage.isNullOrBlank()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isHeaderVisible) {
            Text(
                text = title,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W500,
                color = Black23,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 18.dp)
            )
        }
        Surface(
            onClick = onClick,
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.error else HoneyFlower50,
                    shape = RoundedCornerShape(10.dp)
                )
                .height(150.dp),
            contentColor = White,
            shape = RoundedCornerShape(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = White)
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = false,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                        .fillMaxHeight(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = White,
                        unfocusedIndicatorColor = White,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = Black
                    ),
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
fun TextFiledNewPreviewNewMultiple(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        val email = remember {
            mutableStateOf("")
        }
        OutlineTextFieldMultipleLine(
            isHeaderVisible = true,
            title = stringResource(R.string.password),
            value = email.value,
            onValueChange = { email.value = it },
            header = "Type here..",
        )

    }
}

