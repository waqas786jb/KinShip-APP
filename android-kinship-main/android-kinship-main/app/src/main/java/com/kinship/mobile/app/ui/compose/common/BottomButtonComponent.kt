package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor

@Composable
fun BottomButtonComponent(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier=Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
            .heightIn(50.dp), colors = ButtonDefaults.buttonColors(
            containerColor = AppThemeColor
        )
    ) {
        androidx.compose.material3.Text(
            text = text,
            textAlign = TextAlign.Center,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 17.sp
        )
    }
}
@Preview
@Composable
fun Button() {
    BottomButtonComponent(
        text = "Logiididsmdismdisdmisdmsidmsidmsidmsin",
        onClick = {}
    )
}
