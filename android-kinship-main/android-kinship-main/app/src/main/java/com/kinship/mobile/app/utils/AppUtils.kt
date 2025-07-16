package com.kinship.mobile.app.utils
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
//noinspection ExifInterface
import android.media.ExifInterface
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.format.DateFormat
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.MimeTypeMap
import com.kinship.mobile.app.data.source.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.sqrt

object AppUtils {


    //Get device id
    @SuppressLint("HardwareIds")
    fun getDeviceId(c: Context): String {
        return Settings.Secure.getString(c.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @SuppressLint("DefaultLocale")
    fun formatTime(seconds: Int): String {
        return String.format("%02dsec", seconds)
    }



    /**
     * Helps to set clickable part in text.
     *
     * Don't forget to set android:textColorLink="@color/link" (click selector) and
     * android:textColorHighlight="@color/window_background" (background color while clicks)
     * in the TextView where you will use this.
     */
    fun SpannableString.withClickableSpan(
        clickablePart: String,
        onClickListener: () -> Unit
    ): SpannableString {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = onClickListener.invoke()
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#000000")
//                ds.typeface = ResourcesCompat.getFont(context, R.font.public_sans_bold)
                ds.isUnderlineText = false
            }
        }
        val clickablePartStart = indexOf(clickablePart)
        setSpan(
            clickableSpan,
            clickablePartStart,
            clickablePartStart + clickablePart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return this
    }
    fun getCurrentTimeInSeconds(): Long {
        return System.currentTimeMillis().div(1000)
    }


    fun formatTimestampToDateTime(timestamp: Long, pattern: String = "dd MMM hh:mm a"): String {
        val date = Date(timestamp) // Convert timestamp to Date
        val formatter = SimpleDateFormat(pattern, Locale.getDefault()) // Create formatter with desired pattern
        val formattedDate = formatter.format(date) // Format the date
        return formattedDate.replace("am", "AM").replace("pm", "PM") // Convert am/pm to uppercase
    }

    fun createMultipartBody(file: File?, keyName: String?): MultipartBody.Part {
        return if (file != null) {
            MultipartBody.Part.createFormData(keyName!!, file.name, file.asRequestBody(getMimeType(file.toString())?.toMediaTypeOrNull()))
        } else {
            MultipartBody.Part.createFormData(keyName!!, "", "".toRequestBody("text/plain".toMediaTypeOrNull()))
        }
    }
    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
    fun getFileFromContentUri(context: Context, contentUri: Uri, filename: String): File? {
        val inputStream = context.contentResolver.openInputStream(contentUri)
        inputStream?.let {
            // Making directory
            val cacheDir = context.cacheDir
            val gapMediaDir = File(cacheDir, Constants.AppInfo.DIR_NAME)
            if (!gapMediaDir.exists()) {
                gapMediaDir.mkdir()
            }

            val mimeType = getFileExtensionFromUri(context = context, uri = contentUri)

            // Create a new file with a unique name
            val tempFile = File.createTempFile(filename, ".$mimeType", gapMediaDir)

            try {
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Rotate the bitmap if required
                val rotatedBitmap = rotateImageIfRequired(context, bitmap, contentUri)

                // Compress the bitmap if its size is greater than 2MB
                val maxSize = 2 * 1024 * 1024 // 2MB
                val compressQuality = 90 // Adjust as needed
                val stream = ByteArrayOutputStream()

                val scaledBitmap: Bitmap = if (rotatedBitmap.byteCount > maxSize) {
                    val scaleFactor = sqrt((rotatedBitmap.byteCount / maxSize).toDouble())
                    val scaledWidth = (rotatedBitmap.width / scaleFactor).toInt()
                    val scaledHeight = (rotatedBitmap.height / scaleFactor).toInt()
                    Bitmap.createScaledBitmap(rotatedBitmap, scaledWidth, scaledHeight, true)
                } else {
                    rotatedBitmap
                }

                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream)
                val outputStream: OutputStream = FileOutputStream(tempFile)
                outputStream.write(stream.toByteArray())

                // Close the streams
                inputStream.close()
                outputStream.close()

                return tempFile
            } catch (e: IOException) {
                throw e
            } finally {
                // Close the input stream
                inputStream.close()
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei = ExifInterface(input!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }
    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }




    fun convertHourMinToTime(
        hour: Int, min: Int,
        pattern: String = "hh:mm a" // default pattern
    ): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, min)
        // Subtract 5 hours and 30 minutes
        cal.add(Calendar.HOUR_OF_DAY, 5)
        cal.add(Calendar.MINUTE, 30)
        // Set the time zone to GMT+05:30
        val timeZone = TimeZone.getDefault()
        val formatter = SimpleDateFormat(pattern, Locale.getDefault()).apply {
            this.timeZone = timeZone
        }
        return formatter.format(cal.time)
    }
    private fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        // Get the mime type of the content URI
        val mimeType: String? = contentResolver.getType(uri)
        // Extract the file extension from the mime type
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    fun convertHourMinToTimestamp(hour: Int, min: Int, eventDateInMillis: Long): Long {
        // Create a calendar instance with UTC time zone, and set the event date
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = eventDateInMillis // Use the event date instead of the current date
        calendar.set(Calendar.HOUR_OF_DAY, hour) // Set the event time (hour)
        calendar.set(Calendar.MINUTE, min)       // Set the event time (minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Return time in UTC milliseconds
        return calendar.timeInMillis
    }

    @SuppressLint("DefaultLocale")
    fun getTimeFromMillis(milliseconds: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = milliseconds


        // Subtract 5 hours and 30 minutes
        calendar.add(Calendar.HOUR_OF_DAY,0)
        calendar.add(Calendar.MINUTE, 0)

        // Set the time zone to GMT+05:30
        val timeZone = TimeZone.getDefault()
        calendar.timeZone = timeZone

        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

        // Convert hour '0' to '12' for 12-hour format (12 AM or 12 PM)
        val hourFormatted = if (hours == 0) 12 else hours

        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
    }


    fun getDateStringMonthName(date: Long): String {
        val then = Calendar.getInstance()
        then.timeInMillis = date
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        return sdf.format(date)
    }
    fun getDateLabel(date: Long,context: Context): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = date
        return when {
            now.isSameDay(then) -> getFormatter("h:mm a",context).format(date)
            now.isYesterday(then) -> "Yesterday"
            else -> getFormatter("dd/M/yyyy",context).format(date)
        }
    }

    private fun Calendar.isSameDay(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
    }

    fun convertTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }

    fun convertMemberDateFormat(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
    private fun Calendar.isYesterday(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) - 1 == other.get(Calendar.DAY_OF_YEAR)
    }



    private fun getFormatter(pattern: String,context: Context): SimpleDateFormat {
        var formattedPattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern)

        if (DateFormat.is24HourFormat(context)) {
            formattedPattern = formattedPattern
                .replace("h", "HH")
                .replace("K", "HH")
//                .replace(" a".toRegex(), "")
        }

        return SimpleDateFormat(formattedPattern, Locale.getDefault())
    }

    fun Context.isScreenLocked(): Boolean {
        val keyguardManager: KeyguardManager? = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        val powerManager: PowerManager? = getSystemService(Context.POWER_SERVICE) as PowerManager?
        val locked = keyguardManager != null && keyguardManager.isKeyguardLocked
        val interactive = powerManager != null && powerManager.isInteractive
        return locked || !interactive
    }

}