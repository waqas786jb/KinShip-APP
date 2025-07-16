package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun DialogWithDoubleButtonPreview() {
    PermissionDialog(
        onDismissRequest = { /*TODO*/ },
        title = "Kinship App",
        description = "Allow “Kinship App” to access your storage and camera while you are using the app.",
        negativeText = "Cancel",
        positiveText = "Open Setting"
    ) {

    }
}

@Composable
fun PermissionDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    description: String = "",
    negativeText: String,
    positiveText: String,
    onPositiveClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(120.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = White)

        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500,
                    fontFamily = OpenSans,
                    fontSize = 22.sp,
                    color = Black,
                    modifier = Modifier.paddingFromBaseline(bottom = 10.dp)
                )

                if (description.isNotBlank())
                    Text(
                        text = description,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W500,
                        fontFamily = OpenSans,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = Black,
                        modifier = Modifier.padding(horizontal = 20.dp)


                    )

                Spacer(modifier = Modifier.padding(5.dp))
                HorizontalDivider()

                Row() {
                    androidx.compose.material3.Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.widthIn(113.dp)


                    ) {
                        Text(
                            text = negativeText,
                            fontWeight = FontWeight.W500,
                            fontFamily = OpenSans,
                            color = Black,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                    VerticalDivider(
                        modifier = Modifier.height(50.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    androidx.compose.material3.Button(
                        onClick = onPositiveClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Black
                        ),
                        modifier = Modifier.widthIn(113.dp)
                    ) {
                        Text(
                            text = positiveText,
                            fontWeight = FontWeight.W500,
                            fontFamily = OpenSans,
                            fontSize = 14.sp,
                            color = Black
                        )
                    }


                }


            }
        }
    }
}