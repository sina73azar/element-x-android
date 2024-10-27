/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.refa.di

import android.content.Context
import android.content.SharedPreferences
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.database.impl.DataBaseRequestImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.element.android.libraries.di.ApplicationContext
import io.element.android.x.di.PREF_KEY_TOKEN
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

class DependencyProvider(applicationContext: Context) {
    val baseUrl: String = "https://refaland-gateway.daneshrefah.ir/"

    val httpLoggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addNetworkInterceptor(httpLoggingInterceptor)
            // Add your OAuth interceptor or other interceptors if needed
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val sharedPreferences: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREF_KEY_TOKEN, Context.MODE_PRIVATE)
    }

    val dispatcher: CoroutineDispatcher = Dispatchers.IO

}
