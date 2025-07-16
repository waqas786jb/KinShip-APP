package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Composable
fun SendMessageComponent(
    profileImage:String,
    imageCloseClick: () -> Unit = {},
    value: String,
    onValueChange: (String) -> Unit = {},
    onTrailingIconClick:()->Unit ={},
    onLeadingIconClick:()-> Unit={}
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier.padding(
            start = 20.dp,
            end = 20.dp,
            bottom = 10.dp
        )
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            if (profileImage.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(5.dp))
                Row {
                    Spacer(modifier = Modifier.padding(12.dp))
                    AsyncImage(
                        model = profileImage,
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                    )
                    IconButton(
                        onClick = imageCloseClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Remove Image"
                        )
                    }
                }
            }


        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(color = White)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                interactionSource = interactionSource,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = White,
                    unfocusedIndicatorColor = White,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    cursorColor = Black,
                    disabledIndicatorColor = White
                ),
                trailingIcon = {
                    Row {
                        Spacer(modifier = Modifier.padding(5.dp))
                        IconButton(onClick = onTrailingIconClick) {
                            Image(
                                painter = painterResource(R.drawable.ic_send_msg),
                                contentDescription = "Send Message"
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(onClick = onLeadingIconClick) {
                        Image(
                            painter = painterResource(R.drawable.ic_add_event),
                            contentDescription = "User",
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.send_message),
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        color = Black50
                    )
                }
            )
        }

    }


}
@Preview
@Composable
fun SendMessageComponentPreview() {
    SendMessageComponent(profileImage = "",
        imageCloseClick = {},
        value = "",
        onTrailingIconClick = {""})
}