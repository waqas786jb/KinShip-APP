package com.kinship.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.YellowFF

@Composable
fun SingleMultipleSelectUnselect(
    selectedOption: String,
    title:String,
    selectText:String,
    unSelectText:String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
            Text(text = title,
                modifier = Modifier.padding(start = 18.dp),
                fontFamily = OpenSans,
                fontSize = 14.sp,
                color = Black23,
                fontWeight = FontWeight.W400
                )
        Row(
            modifier = Modifier
                .border(width = 1.dp, color = YellowFF, shape = RoundedCornerShape(56.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            OptionButton(
                    text = selectText,
                    icon = R.drawable.ic_single_person,
                    isSelected = selectedOption == "Single",
                    onClick = { onOptionSelected("Single") }
                )
                OptionButton(
                    text = unSelectText,
                    icon = R.drawable.ic_multiple_person,
                    isSelected = selectedOption == "Multiple",
                    onClick = { onOptionSelected("Multiple") }
                )

            }
    }
}
@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,

    @DrawableRes icon: Int? = R.drawable.ic_single_person,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (isSelected) YellowFF else Color.Transparent
    Surface(
        color = backgroundColor,
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier

            .clickable(onClick = onClick)
            .size(height = 48.dp, width = 115.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.padding(3.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                color = Black23,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    var selectedOption by remember { mutableStateOf("Single") }
    val onOptionSelected: (String) -> Unit = { option ->
        selectedOption = option
    }
    SingleMultipleSelectUnselect(
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        selectText = "Single",
        unSelectText = "Unselect",
        title = stringResource(id = R.string.did_you_have_a_single_or_multiple_pregnancy)
    )

}
