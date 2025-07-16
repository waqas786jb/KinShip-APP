package com.kinship.mobile.app.ui.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

/*
@Preview
@Composable
fun CustomLoader(
    @DrawableRes icon: Int? = null
) {
    Dialog(onDismissRequest = { */
/*TODO*//*
 }, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(trackColor = androidx.compose.ui.graphics.Color.Transparent, color = PinkE9, modifier = Modifier.size(60.dp))
            Image(painter = painterResource(id = R.drawable.ic_app_logo), contentDescription = "loading", modifier = Modifier
                .size(45.dp)
                .clip(CircleShape))

        }
    }
}*/
@Preview
@Composable
fun CustomLoader(
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .background(White, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            CircularProgressIndicator(
                trackColor = androidx.compose.ui.graphics.Color.Transparent,
                color = HoneyFlower50,
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "loading",
                modifier = Modifier
                    .size(50.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(White)
            )
        }
    }
}

