package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun EventDeleteDailogPreview() {

    EventDeleteDialog(
        onDismissRequest = { /*TODO*/ },
        title = "Delete Event",
        positiveText = "Delete",
        description = "Are you sure you want to delete the event?",
        negative = "Cancel"

    )
}

@Composable
fun EventDeleteDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    description: String,
    positiveText: String,
    negative: String,
    onPositiveClick: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = White)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Spacer(modifier = Modifier.padding(13.dp))

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    fontSize = 16.sp,
                    color = AppThemeColor,
                    modifier = Modifier.paddingFromBaseline(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    color = Black,
                    modifier = Modifier.padding(horizontal = 50.dp)

                )
                Spacer(modifier = Modifier.padding(15.dp))
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
                            text = negative,
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
                Spacer(modifier = Modifier.padding(12.dp))
            }
        }
    }
}



