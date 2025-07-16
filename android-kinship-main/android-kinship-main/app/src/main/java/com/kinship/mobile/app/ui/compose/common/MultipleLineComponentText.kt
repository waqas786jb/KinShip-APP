package com.kinship.mobile.app.ui.compose.common
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black85
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@Composable
fun MultipleLineComponentText(
    header: String,
    title: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isHeaderVisible: Boolean = false,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isHeaderVisible) {
            Text(
                text = title,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                color = Black85,
                fontSize = 14.sp,
                )
        }
        Surface(
            onClick = onClick,
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = HoneyFlower50,
                    shape = RoundedCornerShape(10.dp)
                )
                .height(150.dp),
            contentColor = White,
            shape = RoundedCornerShape(10.dp),
        ) {
            Row(

                modifier = Modifier
                    .background(color = White)
                    .fillMaxWidth()
                    .heightIn(150.dp)
            ) {
                Text(
                    text = header,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 5.dp),
                    color = Black85,
                )
                /*OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = false,
                    readOnly =readOnly?:false ,
                    modifier = modifier.fillMaxWidth().fillMaxHeight(),
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
                            fontSize = 12.sp,
                            color = Black50,
                        )
                    }*/

            }
        }
    }
}
@Preview
@Composable
fun PreviewMultipleLineComponentText() {

    MultipleLineComponentText(
        header = "uiState.memberName",
        isHeaderVisible = true,
        title = stringResource(R.string.bio_title)
    )
    
}

