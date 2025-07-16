package com.kinship.mobile.app.utils.ext

import okhttp3.ResponseBody
import org.json.JSONObject

fun ResponseBody?.extractError(): String {
    return try {
        if (this != null) JSONObject(this.charStream().readText()).getString("message") else "something went wrong!"
    } catch (e: Exception) {
        this?.string() ?: "something went wrong!"
    }
}