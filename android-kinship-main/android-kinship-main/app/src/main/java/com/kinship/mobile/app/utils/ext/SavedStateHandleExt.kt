package com.kinship.mobile.app.utils.ext

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle

@VisibleForTesting
fun createSaveStateErrorMessage(key: String) = "Missing SavedState value for Key: $key"
fun createSaveStateErrorMessageBoolean(key: Boolean) = "Missing SavedState value for Key: $key"

fun SavedStateHandle.requireString(key: String): String = requireNotNull(get<String>(key)) { createSaveStateErrorMessage(key) }
fun SavedStateHandle.requireInt(key: String): Int = requireNotNull(get<Int>(key)) { createSaveStateErrorMessage(key) }
fun SavedStateHandle.requireBoolean(key: Boolean): Int = requireNotNull(get<Int>(key.toString())) { createSaveStateErrorMessage(
    key.toString()
) }

/*
fun SavedStateHandle.requireDateProfileType(key: String): ProfileType = requireNotNull(get<ProfileType>(key)) { createSaveStateErrorMessage(key) }*/
