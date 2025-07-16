@file:Suppress("DEPRECATION")

package com.kinship.mobile.app.ux.main.events.myEventDetails
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.model.domain.tabSelector.Tab
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomTab3Item
import com.kinship.mobile.app.ui.compose.common.GroupNotCreateDialog
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black30
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.ux.main.events.EventsUiState
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch


@Composable
fun AllEvent(uiState: EventsUiState, list: ArrayList<MyEventsData>, mainId: String) {
    val isRefreshing by uiState.isAllEventsRefreshing.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                uiState.myEventAndEventApiCall() // Ensure this method refreshes the event list
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    // Primary color for the loader
                    contentColor = AppThemeColor, // Change this to your desired color
                    // Optional: Background of the indicator can also be customized if needed
                    backgroundColor = White // Optional
                )
            }
        ) {
            AllEventContent(uiState, list, mainId = mainId)
        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AllEventContent(uiState: EventsUiState, list: ArrayList<MyEventsData>, mainId: String) {
    val showDialog by uiState.showDialog.collectAsStateWithLifecycle()
    val eventList by uiState.apiEventList.collectAsStateWithLifecycle()
    val showEventNoText by uiState.showEventNoFoundText.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    if (showEventNoText) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.no_event_found),
                fontSize = 16.sp,
                maxLines = 1,
                color = Black,
                fontFamily = OpenSans,
            )
        }
    }
    LazyColumn(state = listState) {
        itemsIndexed(eventList.reversed()) { index, list1 ->
            EventsItem(list1, uiState, index, list.size)
            if (mainId.isNotEmpty()) {
                val targetIndex = eventList.indexOfFirst { it.id == mainId }
                if (targetIndex != -1) {
                    coroutine.launch {
                        listState.animateScrollToItem(targetIndex, scrollOffset = -50)
                    }
                }
            }
        }
    }
    if (showDialog) {
        GroupNotCreateDialog(
            onDismissRequest = { uiState.onShowDialog(false) },
            title = stringResource(R.string.you_cannot_add_event_as_you_do_not_belong_to_any_kinship_group),
            positiveText = stringResource(R.string.okay)

        )
    }
}

@Composable
private fun EventsItem(
    data: MyEventsData,
    uiState: EventsUiState,
    index: Int,
    totalCountInList: Int
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)

            .fillMaxWidth()
    ) {
        Row {
            EventImageItem(data.photo ?: "")
            EventInfoSection(data = data)
        }
        EventsDescriptionSection(data = data, uiState)
        if (index != totalCountInList.minus(1)) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Black30,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun EventImageItem(image: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        border = if (image.isNotBlank()) BorderStroke(
            width = 1.dp,
            color = AppThemeColor
        ) else BorderStroke(
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

@Composable
private fun EventInfoSection(data: MyEventsData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = data.eventName ?: "",
            fontSize = 15.sp,
            color = Color.Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(modifier = Modifier.padding(top = 7.dp)) {
            Text(
                text = stringResource(id = R.string.created_by),
                fontSize = 13.sp,
                color = Black50,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(start = 10.dp)
            )
            Text(
                text = data.firstName.plus(" ").plus(data.lastName),
                fontSize = 13.sp,
                color = AppThemeColor,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(start = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = AppUtils.getDateStringMonthName(data.eventDate),
            fontSize = 13.sp,
            color = Color.Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(start = 10.dp, top = 7.dp)
        )
        Text(
            text = AppUtils.getTimeFromMillis(data.startTime).plus(" to ")
                .plus(AppUtils.getTimeFromMillis(data.endTime)),
            fontSize = 13.sp,
            color = Color.Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(start = 10.dp, top = 7.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EventsDescriptionSection(data: MyEventsData, uiState: EventsUiState) {
    var optionSelected by remember { mutableIntStateOf(-1) }
    val context = LocalContext.current
    val urlPattern = ".*(http|https|www\\.).*".toRegex()
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
                        fontSize = 13.sp,
                        fontFamily = OpenSans
                    )
                ) {
                    append(stringResource(id = R.string.zoom).plus(" "))
                }
                withStyle(
                    style = SpanStyle(
                        color = AppThemeColor,
                        fontWeight = FontWeight.W600,
                        fontSize = 13.sp,
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = stringResource(id = R.string.location)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = data.location,
                    color = Black,
                    fontWeight = FontWeight.W400,
                    fontSize = 13.sp,
                    fontFamily = OpenSans
                )
            }
        }
        val description = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = AppThemeColor,
                    fontWeight = FontWeight.W600,
                    fontSize = 13.sp,
                    fontFamily = OpenSans
                )
            ) {
                append(stringResource(id = R.string.description))
            }
            withStyle(
                style = SpanStyle(
                    color = Black,
                    fontWeight = FontWeight.W400,
                    fontSize = 13.sp,
                    fontFamily = OpenSans
                )
            ) {
                append("  ${data.eventDescription}")
            }
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Text(text = description)
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = stringResource(id = R.string.rsvp),
            fontSize = 13.sp,
            color = Color.Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
        )
        Spacer(modifier = Modifier.padding(5.dp))
        val tabItems = listOf(
            Tab(stringResource(id = R.string.yes), null),
            Tab(stringResource(id = R.string.no), null),
            Tab(stringResource(id = R.string.may_be), null)
        )
        CustomTab3Item(
            tab = tabItems,
            modifier = Modifier.fillMaxWidth(),
        ) { index ->
            optionSelected = index
            when (index) {
                0 -> {
                    uiState.onStatusClicked(1, data.id)
                }

                1 -> {
                    uiState.onStatusClicked(2, data.id)
                }

                2 -> {
                    uiState.onStatusClicked(3, data.id)
                }
            }
        }
    }
}