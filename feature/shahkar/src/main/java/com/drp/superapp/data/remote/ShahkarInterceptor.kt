package com.drp.superapp.data.remote

import android.content.Context
import android.os.Build
import com.drp.superapp.util.Commons.checkInternet
import com.drp.superapp.util.Commons.correlation
import com.drp.superapp.util.Commons.getAppVersion
import com.drp.superapp.util.Commons.getApplicationSignature
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.net.HttpURLConnection
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ShahkarInterceptor @Inject constructor(
    @ApplicationContext val context: Context,
    private val baseUrl: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!checkInternet(context)) {
            // Return an error response indicating no internet connectivity
            return createErrorResponse("No internet connectivity")
        }
        val request = addHeader(chain)
        val response = chain.proceed(request)
        return response
    }

    private fun createErrorResponse(errorMessage: String): Response {
        // Create a response body with the error message
        val body = ResponseBody.create("text/plain".toMediaTypeOrNull(), errorMessage)

        return Response.Builder()
            .code(HttpURLConnection.HTTP_UNAVAILABLE) // Define your error code
            .message(errorMessage)
            .protocol(Protocol.HTTP_1_1)
            .body(body)
            .request(Request.Builder().url(baseUrl).build())
            .build()
    }

    private fun addHeader(chain: Interceptor.Chain): Request {
        var request = chain.request()
        request = request.newBuilder().addHeader("Channel", "MB").build()
        request = request.newBuilder().addHeader("Agent", "Android").build()
        request = request.newBuilder()
            .addHeader("X-Correlation-Id", correlation(System.currentTimeMillis().toString()))
            .build()
        request = request.newBuilder().addHeader("UUID", UUID.randomUUID().toString()).build()
        request = request.newBuilder().addHeader("AppVersion", getAppVersion(context)).build()
        request = request.newBuilder().addHeader("OSVersion", Build.VERSION.RELEASE).build()
        request =
            request.newBuilder().addHeader("DeviceModel", Build.MANUFACTURER + " " + Build.MODEL)
                .build()

        if (getApplicationSignature(context).isNotEmpty()) {
            request =
                request.newBuilder().addHeader("Signature", getApplicationSignature(context)[0])
                    .build()
        }
        return request
    }


}