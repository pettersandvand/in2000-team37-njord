package com.example.in2000_prosjektoppgave.data.remote.drifty

import android.os.SystemClock
import okhttp3.Interceptor
import okhttp3.Response


/**
 *   Interceptor used by the Drifty Service to intercept and retry get request when the
 *   response code is 202. If the "retry-after" header is set in the response the delay
 *   between requests are set to this value, otherwise it is set to 10 seconds.
 *
 */
class RetryInterceptor : Interceptor {

    // Constants
    companion object {
        const val GET = "GET"
        const val RETRY_AFTER = "retry-after"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var delay: Long?
        var count = 0

        // Check if get request
        if (request.method == GET) {

            // If response code 202 retry up to 50 times
            while (response.code == 202 && count < 50) {
                try {

                    // Check for time out header
                    delay = response.headers[RETRY_AFTER]?.toLongOrNull()

                    // Delay
                    if (delay != null) {
                        SystemClock.sleep(1000 * delay)
                    } else {
                        SystemClock.sleep(10000)
                    }

                    // Continue with new request
                    response.close()
                    response = chain.proceed(request.newBuilder().build())
                } finally {
                    count++
                }
            }
        }
        return response
    }
}
