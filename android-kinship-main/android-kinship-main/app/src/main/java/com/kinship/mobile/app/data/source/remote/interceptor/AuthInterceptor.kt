package com.kinship.mobile.app.data.source.remote.interceptor


import com.kinship.mobile.app.App
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val mainResponse = chain.proceed(chain.request())

        if (mainResponse.code == 401 || mainResponse.code == 403) {
            mainResponse.close()  // Close response before restarting
            // Trigger app restart
            App.instance?.restartApp()
            // Return a new empty response to avoid returning a closed response
            return chain.proceed(chain.request().newBuilder().build())
        }

        return mainResponse
    }
}
