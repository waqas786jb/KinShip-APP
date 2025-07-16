package com.kinship.mobile.app.ui.compose.common
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.BlackC9
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White


@Composable
fun TopBarComponent(
    modifier: Modifier = Modifier,
    header: String,
    isLineVisible: Boolean = false,
    isBackVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    @DrawableRes trailingIcon: Int? = null,
    onClick: () -> Unit = {},
    colorsFilter: ColorFilter? = null,
    onTrailingIconClick:()->Unit={}
) {
    Column(
        modifier = Modifier
            .background(White)
            .padding(horizontal = 20.dp)
    ){
        Row(
            modifier = modifier
                .requiredHeight(54.dp)
                .fillMaxWidth()

        ) {
            if (isBackVisible) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    colorFilter =colorsFilter ,

                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onClick()
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu_white),
                    contentDescription = null,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .alpha(0f)
                )
            }
            Text(
                text = header,
                color = Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 10.dp)

                    .weight(1f), fontFamily = OpenSans,
                fontWeight = FontWeight.W600
            )
            if (isTrailingIconVisible && trailingIcon != null) {
                Image(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onTrailingIconClick()
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu_white),
                    contentDescription = null,

                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .alpha(0f)
                )
            }
        }
        if (isLineVisible) {
            Box(
                modifier = modifier
                    .background(color = BlackC9)
                    .fillMaxWidth()
                    .alpha(70f)
                    .padding(horizontal = 30.dp)
                    .height(0.5.dp)
            )
        }
    }
}


@Preview
@Composable
fun TopBarComponentPreview() {
    TopBarComponent(
        header = "Title",
        isLineVisible = true,
        isTrailingIconVisible = true
    )
}

