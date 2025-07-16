@file:Suppress("DEPRECATION")

package com.kinship.mobile.app.ux.main.events.myEventDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.EventDeleteDialog
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black20
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.GrayF1
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor

import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.ux.main.events.EventsUiState
import es.dmoral.toasty.Toasty


@Composable
fun MyEvent(uiState: EventsUiState) {
    val isRefreshing by uiState.isAllEventsRefreshing.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    // Primary color for the loader
                    contentColor = AppThemeColor, // Change this to your desired color
                    // Optional: Background of the indicator can also be customized if needed
                    backgroundColor = White // Optional
                )
            },
            onRefresh = { uiState.myEventAndEventApiCall() }) {
            MyEventContent(uiState)

        }

    }

}


@Composable
fun MyEventContent(uiState: EventsUiState) {
    val showText by uiState.noDataFoundMyEventText.collectAsStateWithLifecycle()
    val myEventList by uiState.apiMyEventList.collectAsStateWithLifecycle()
    val totalCountInList = myEventList.size
    if (showText) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_event_found),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Black,
                    fontFamily = OpenSans,
                )
            }
        }
    }
    LazyColumn {
        itemsIndexed(myEventList) { index, list1 ->
            MyEventsItems(list1, index, totalCountInList, uiState)
        }
    }
}

@Composable
fun MyEventsItems(
    data: MyEventsData,
    index: Int,
    totalCountInList: Int,
    uiState: EventsUiState
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Row {
            MyEventImageItem(data.photo ?: "")
            MyEventInfoSection(data = data, uiState)
        }
        MyEventsDescriptionSection(data = data, uiState)
        if (index != totalCountInList.minus(1)) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Black20,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    val data = MyEventsData()
    val uiState = EventsUiState()
    MyEventsItems(data, index = 1, totalCountInList = 1, uiState)
}

@Composable
private fun MyEventImageItem(image: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        border = if (image.isNotBlank()) BorderStroke(width = 1.dp, color = AppThemeColor) else BorderStroke(
            width = 0.dp,
            color = White
        ),
        modifier = Modifier.size(95.dp)
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            error = painterResource(R.drawable.ic_events_placeholder),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_events_placeholder)
        )
    }
}

@SuppressLint("RememberReturnType")
@Composable
private fun MyEventInfoSection(data: MyEventsData, uiState: EventsUiState) {
    val showEventDialog = remember {
        mutableStateOf(false)
    }
    val showEventDeleteDialog = remember {
        mutableStateOf(false)
    }
    val createOn = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W600,
            ),
        ) {
            append(stringResource(id = R.string.created_on))
        }
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W400,
            )
        ) {
            append(" ${AppUtils.getDateStringMonthName(data.createdAt)}")
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(
                text = data.eventName ?: "",
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .padding(start = 10.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_event_edit),
                contentDescription = "eventEdit",
                Modifier.clickable {
                    showEventDialog.value = true
                }
            )
        }
        Row(modifier = Modifier.padding(top = 7.dp)) {
            Text(
                text = createOn,
                maxLines = 2,
                fontSize = 13.sp,
                color = Black50,
                fontFamily = OpenSans,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        Text(
            text = AppUtils.getDateStringMonthName(data.eventDate),
            fontSize = 12.sp,
            color = Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            modifier = Modifier.padding(start = 10.dp, top = 7.dp)
        )

        Text(
            text = AppUtils.getTimeFromMillis(data.startTime).plus(" to ")
                .plus(AppUtils.getTimeFromMillis(data.endTime)),
            fontSize = 12.sp,
            color = Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            modifier = Modifier.padding(start = 10.dp, top = 7.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
    if (showEventDialog.value) {
        // Render your dialog here
        CameraGalleryDialog(
            onDismissRequest = { showEventDialog.value = false },
            topText = stringResource(id = R.string.edit_event),
            bottomText = stringResource(id = R.string.delete_event),
            onTopClick = {
                uiState.navigateToMyEventEvent(data)
                showEventDialog.value = false
            },
            topIcon = R.drawable.ic_edit,
            bottomIcon = R.drawable.ic_delete,
            onBottomClick = {
                showEventDeleteDialog.value = true
                showEventDialog.value = false
            }
        )
    }
    if (showEventDeleteDialog.value) {
        EventDeleteDialog(
            onDismissRequest = { showEventDeleteDialog.value = false },
            onPositiveClick = {
                uiState.eventDeleteAPICall(data.id)
                showEventDeleteDialog.value = false
            },
            title = stringResource(id = R.string.delete_event),
            positiveText = stringResource(id = R.string.delete),
            description = stringResource(id = R.string.are_you_sure_you_want_to_delete_the_event),
            negative = stringResource(id = R.string.cancel)

        )
    }
}

@Composable
private fun MyEventsDescriptionSection(data: MyEventsData, uiState: EventsUiState) {
    val urlPattern = ".*(http|https|www\\.).*".toRegex()
    val context = LocalContext.current
    // val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(data.link)) }
    val intent = remember {
        val formattedLink =
            if (!data.link.startsWith("http://") && !data.link.startsWith("https://")) {
                "http://${data.link}"
            } else {
                data.link
            }
        Intent(Intent.ACTION_VIEW, Uri.parse(formattedLink))
    }
    Column(modifier = Modifier.padding(top = 10.dp)) {
        if (data.link.isNotBlank()) {
            val link = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Black,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        fontFamily = OpenSans
                    )
                ) {
                    append(stringResource(id = R.string.zoom).plus(" "))
                }
                withStyle(
                    style = SpanStyle(
                        color = AppThemeColor,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        fontFamily = OpenSans,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(stringResource(id = R.string.click_to_join))
                }
            }
            Text(text = link, modifier = Modifier
                .padding(top = 10.dp)
                .clickable {
                    if (link.contains(urlPattern)) {
                        Toasty
                            .warning(
                                context,
                                context.getString(R.string.not_a_valid_link),
                                Toast.LENGTH_SHORT,
                                false
                            )
                            .show()
                    } else {
                        context.startActivity(intent)
                    }
                })
        }
        if (data.location?.isNotBlank() == true) {
            Spacer(modifier = Modifier.padding(5.dp))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = stringResource(id = R.string.location)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = data.location,
                    color = Black,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    fontFamily = OpenSans
                )
            }
        }
        val description = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = AppThemeColor,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    fontFamily = OpenSans
                )
            ) {
                append(stringResource(id = R.string.description))
            }
            withStyle(
                style = SpanStyle(
                    color = Black,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    fontFamily = OpenSans
                )
            ) {
                append("  ${data.eventDescription}")
            }
        }
        Text(text = description, modifier = Modifier.padding(top = 10.dp))
        Spacer(modifier = Modifier.padding(5.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = GrayF1,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.rsvp_my_event),
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                )
                Text(
                    text = stringResource(id = R.string.yes).plus(" - "),
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Text(
                    text = data.yes.toString(),
                    fontSize = 12.sp,
                    color = AppThemeColor,
                    fontFamily = OpenSans,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 10.dp)


                )
                Text(
                    text = stringResource(id = R.string.may_be).plus(" - "),
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Text(
                    text = data.maybe.toString(),
                    fontSize = 12.sp,
                    color = AppThemeColor,
                    fontFamily = OpenSans,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier
                        .padding(start = 10.dp)

                )
                Spacer(modifier = Modifier.padding(5.dp))
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        uiState.navigateToRsvpScreen(data.id)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppThemeColor,
                        contentColor = Color.Red
                    ),
                    modifier = Modifier
                        .widthIn(min = 100.dp) // Ensure the width is sufficient to fit the text
                        .heightIn(max = 32.dp, min = 35.dp)
                ) {
                    Text(
                        text = stringResource(R.string.view_details),
                        fontWeight = FontWeight.W400,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.padding()
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

