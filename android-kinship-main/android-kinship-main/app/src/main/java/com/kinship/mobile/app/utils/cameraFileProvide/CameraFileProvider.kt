package com.kinship.mobile.app.utils.cameraFileProvide

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.kinship.mobile.app.R
import java.io.File

class ComposeFileProvider : FileProvider(
    R.xml.path
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}