package com.kinship.mobile.app.ui.compose.common

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kinship.mobile.app.ui.theme.Alto
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.White
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialogFuture(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit,
    onSelectedDate: Long? = null,
    onDismiss: () -> Unit,
    onDateSelectedLong: (Long) -> Unit,
) {

    val today = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = onSelectedDate ?: today.timeInMillis,
        initialDisplayedMonthMillis = today.timeInMillis,
        selectableDates = PastOrPresentSelectableDatesFuture
    )

    Log.d("DatePickerWithDialogFuture", "DatePickerWithDialogFuture: $onSelectedDate")

    val selectedDate = remember { mutableStateOf(today.timeInMillis) }

    if (datePickerState.selectedDateMillis != selectedDate.value) {
        selectedDate.value = datePickerState.selectedDateMillis ?: today.timeInMillis
    }

    val formattedDate = remember(selectedDate.value) {
        convertMillisToDate(selectedDate.value)
    }

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(formattedDate)
                    onDateSelectedLong(selectedDate.value)

                    Log.d(
                        "DatePickerWithDialogFuture",
                        "DatePickerWithDialogFuture: ${selectedDate.value}"
                    )
                    onDismiss()
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) { Text("Cancel") }
        }
    ) {
        DatePicker(
            colors = DatePickerDefaults.colors(
                todayDateBorderColor = Black,
                currentYearContentColor = AppThemeColor,
                selectedYearContentColor = AppThemeColor,
                selectedDayContainerColor = Black,
                todayContentColor = Black,
                selectedYearContainerColor = Black,
                selectedDayContentColor = White,
                dayContentColor = Black,
            ),
            state = datePickerState,
            showModeToggle = false
        )
    }
}

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDatesFuture : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}




    
