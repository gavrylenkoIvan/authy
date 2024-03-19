package me.iru.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class HeaderInterceptor(private val key: String, private val value: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithUserAgent = originalRequest
            .newBuilder()
            .header(key, value)
            .build()

        return chain.proceed(requestWithUserAgent)
    }
}