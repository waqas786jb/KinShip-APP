package com.kinship.mobile.app.ui.compose.auth
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinship.mobile.app.R
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black40
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
@Composable
fun TermsAndConditionsText(
    onTermsAndConditionsClick: () -> Unit,
    onCheckChange : (Boolean) -> Unit,
    errorMessage: String? = null,

) {
    val context= LocalContext.current
    val termsAndConditions = "Terms and Conditions"

    val annotatedString = remember {
        buildAnnotatedString {
            append("By clicking register, I agree to ")
            val termsAndConditionsStart = length
            append(termsAndConditions)
            addStyle(
                style = SpanStyle(color = AppThemeColor, fontFamily = OpenSans, fontWeight = FontWeight.W500,
                    textDecoration =  TextDecoration.Underline),
                start = termsAndConditionsStart,
                end = termsAndConditionsStart + termsAndConditions.length,
                )
            addStringAnnotation(
                tag = "URL",
                annotation = "Terms and Conditions",
                start = termsAndConditionsStart,
                end = termsAndConditionsStart + termsAndConditions.length,
                )
            append(".")
        }
    }
    Column(horizontalAlignment = Alignment.Start) {
        Row(modifier = Modifier.padding(end = 10.dp)){
            var isChecked by rememberSaveable { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = if (isChecked) AppThemeColor else White,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onCheckChange(!isChecked); isChecked = !isChecked }
                    .border(width = 1.5.dp, color = AppThemeColor, shape = RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isChecked,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_box),
                        contentDescription = null,
                        tint = White
                    )
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
            ClickableText(
                text = annotatedString,
                style = TextStyle(
                    color = Black40,
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,

                    ),
                onClick = { offset ->
                    annotatedString.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { annotation ->
                        when (annotation.item) {
                            context.getString(R.string.Terms_and_Conditions) -> onTermsAndConditionsClick()

                        }
                    }
                },
            )
        }
        errorMessage?.let { error ->
            androidx.compose.material3.Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)

            )
        }
    }
}

@Preview
@Composable
fun PreviewTermsAndCond(){
    //TermsAndConditionsText(onTermsAndConditionsClick = {}, onPrivacyPolicyClick = {}, onCheckChange = {})
}

