package com.iqonic.store.network

import com.iqonic.store.utils.Constants.SharedPref.CONSUMERKEY
import com.iqonic.store.utils.Constants.SharedPref.CONSUMERSECRET
import com.iqonic.store.utils.extensions.getSharedPrefInstance
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClientFactory(var url: String) {

    fun getRestApis(): RestApis {
        return getRetroFitClient().create(RestApis::class.java)
    }
    private fun getRetroFitClient(): Retrofit {

        val builder = OkHttpClient().newBuilder().connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)

        val client: OkHttpClient = builder
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original: Request = chain.request()
                        val httpUrl = original.url
                        val newHttpUrl = httpUrl
                                .newBuilder()
                                .addQueryParameter(
                                        "consumer_key",
                                        getSharedPrefInstance().getStringValue(CONSUMERKEY)
                                ).addQueryParameter("consumer_secret", getSharedPrefInstance().getStringValue(CONSUMERSECRET))
                                .build()

                        val requestBuilder = original
                                .newBuilder()
                                .url(newHttpUrl)
                        val request = requestBuilder
                                .build()
                        return chain.proceed(request)
                    }
                }).build()


        return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }
}


