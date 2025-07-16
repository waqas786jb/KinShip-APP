package com.kinship.mobile.app.data.source.remote.interceptor

import co.touchlab.kermit.Logger
import com.kinship.mobile.app.App
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.utils.AppUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestInterceptor
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        try {
            runBlocking{
                val token = appPreferenceDataStore.getUserAuthData()?.accessToken ?: ""
                val refreshToken = appPreferenceDataStore.getUserAuthData()?.refreshToken ?: ""
                val tokenStoreTime = appPreferenceDataStore.getAccessTokenStoreTime() ?: 0L
                if (token.isNotEmpty()) {
                    if (tokenStoreTime != 0L && AppUtils.getCurrentTimeInSeconds() >= tokenStoreTime) {
                        //todo put refresh token code here
                        builder.addHeader("Authorization", "Bearer $refreshToken")
                    } else {
                        builder.addHeader("Authorization", "Bearer $token")
                    }
                }

            }
        } catch (e: Exception) {
            Logger.e("exception")
        }

        return chain.proceed(builder.build())
    }
}