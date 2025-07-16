package com.kinship.mobile.app.ui.compose.common
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
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
fun OutlineTextFiledWithOutLeadingIcon(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    onClick: () -> Unit = {},
    @DrawableRes trailingIcon: Int? = null,
    isLeadingIconVisible: Boolean = false,
    errorMessage: String? = null,
    isTimeSelect: Boolean = false,
    isTitleVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    valueTextColor: Color = Black23,
    title:String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    ) {
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
            modifier = Modifier
                .heightIn(56.dp)
                .fillMaxWidth(),
            border = BorderStroke(
                1.dp,
                if (errorMessage.isNullOrBlank()) HoneyFlower50 else MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(50),
            color = White,
            onClick = onClick
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                if (leadingIcon != null)
                    Image(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = null,
                        colorFilter = (if (errorMessage.isNullOrBlank()) null else MaterialTheme.colorScheme.error)?.let { ColorFilter.tint(it) },
                        modifier = Modifier.padding(start = 15.dp)
                    )
                Text(
                    text = (value.ifEmpty { header }),
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = if (value.isNotBlank() && !isTimeSelect) valueTextColor else Black50,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                )
                if (trailingIcon != null)
                    IconButton(onClick = onClick, modifier = Modifier.padding(end = 16.dp)) {
                        Image(
                            painter = painterResource(id = trailingIcon),
                            contentDescription = trailingIcon.toString(),
                        )
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
fun TwoTimerTextFiledNewPreviewNew(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        val firstName by remember {
            mutableStateOf("")
        }
        val lastName by remember {
            mutableStateOf("")
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = White)
            ) {
                OutlineTextFiledWithOutLeadingIcon(
                    value = firstName,
                    errorMessage = "firstNameError",
                    title = "What should we call you?",
                    trailingIcon = R.drawable.ic_time,
                    header = stringResource(id = R.string.first_name),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = White)
            ) {
                OutlineTextFiledWithOutLeadingIcon(
                    value = lastName,


                    trailingIcon = R.drawable.ic_time,
                    errorMessage = "lastNameError",
                    title = stringResource(id = R.string.what_should_we_call_you),
                    header = stringResource(id = R.string.last_name),

                )
            }
        }
    }
}