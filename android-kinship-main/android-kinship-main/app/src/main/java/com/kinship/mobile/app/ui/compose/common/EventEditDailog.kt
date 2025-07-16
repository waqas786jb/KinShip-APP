package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview(showBackground = true)
@Composable
private fun EventEditDialogPreview() {
    EventEditDialog(
        onDismissRequest = { /*TODO*/ },
        onBottomClick = {},
        onTopClick = {},
        topText = "Edit Event",
        bottomText = "Delete Event",
        bottomIcon = R.drawable.ic_edit,
        topIcon = R.drawable.ic_edit
    )
}
@Composable
fun EventEditDialog(
    onDismissRequest: () -> Unit = {},
    topText: String,
    bottomText: String,
    onTopClick: () -> Unit,
    onBottomClick: () -> Unit,
    topIcon: Int? = null,
    bottomIcon: Int? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .heightIn(110.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EventEditActionButton(
                    text = topText,
                    icon = topIcon,
                    onClick = onTopClick
                )
                Spacer(modifier = Modifier.height(8.dp))
               androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(horizontal = 40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                ActionButton(
                    text = bottomText,
                    icon = bottomIcon,
                    onClick = onBottomClick
                )
            }
        }
    }
}

@Composable
fun EventEditActionButton(
    text: String,
    icon: Int? = null,
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                
                Image(
                    painter = painterResource(icon),
                    contentDescription = "User",
                    modifier = Modifier.size(20.dp),

                    )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = OpenSans,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/*@Composable
fun HorizontalDivider(startPadding: Dp) {
    Divider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = startPadding, end = 16.dp)
    )
}*/
