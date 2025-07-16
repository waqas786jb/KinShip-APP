package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun DialogWithDoubleButtonPreview() {
    GroupNotCreateDialog(
        onDismissRequest = { /*TODO*/ },
        title = "You are not able to add event because you don't have in have kinship group ",
        positiveText = "okay",
    )
}
@Composable
fun GroupNotCreateDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    positiveText: String,
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
                    .padding(horizontal = 20.dp),

                ) {
                Spacer(modifier = Modifier.padding(15.dp))
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    fontSize = 15.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .paddingFromBaseline(top = 15.dp, bottom = 2.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))

                Spacer(modifier = Modifier.padding(top = 20.dp))
                Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                    androidx.compose.material3.Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppThemeColor,
                            contentColor = Color.Blue
                        ),
                        border = BorderStroke(width = 1.dp, color = AppThemeColor),
                        modifier = Modifier
                            .heightIn(15.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = positiveText,
                            fontWeight = FontWeight.W400,
                            fontFamily = OpenSans,
                            fontSize = 15.sp,
                            color = White
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}



