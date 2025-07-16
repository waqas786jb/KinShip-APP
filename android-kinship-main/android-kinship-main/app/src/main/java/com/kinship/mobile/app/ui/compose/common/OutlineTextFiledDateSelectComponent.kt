package com.kinship.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@Composable
fun OutlineTextFiledDateSelectComponent(
    modifier: Modifier = Modifier,
    value: String,
    valueTextColor: Color = Black23,
    header: String? = null,
    title: String? = null,
    errorMessage: String? = null,
    isCreateMatchScreen: Boolean = false,
    showLeadingIcon: Boolean = false,
    infoClick: () -> Unit = {},
    @DrawableRes trailingIcon: Int? = null,
    @DrawableRes leadingIcon: Int? = null,
    onClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (title != null) {
                Text(
                    text = title,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W500,
                    color = Black23,

                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 18.dp)
                )

                if (showLeadingIcon) {
                    Image(
                        painter = painterResource(leadingIcon?:0),
                        contentDescription = leadingIcon.toString(),

                        modifier = Modifier.clickable(onClick = infoClick)
                    )
                }
            }
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

                (value.ifEmpty { header })?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        lineHeight = 24.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = if (value.isNotBlank() && !isCreateMatchScreen) valueTextColor else Black50,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 18.dp)
                    )
                }
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
fun ClickInputComponentPreview() {
    OutlineTextFiledDateSelectComponent(
        value = "9.20 AM",
        header = "Create",
        showLeadingIcon = false,
        leadingIcon = R.drawable.ic_calendar
    )
}