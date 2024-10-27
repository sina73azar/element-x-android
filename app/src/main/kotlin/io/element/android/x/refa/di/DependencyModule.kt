/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.refa.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.drp.data.database.AppDataBase
import com.drp.data.database.Constant
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.database.impl.DataBaseRequestImpl
import com.drp.data.network.ApiService
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.data.network.api_call.DynamicApiCallImpl
import com.drp.data.repository.CardFacilitiesLoanRepositoryImpl
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesRepositoryImpl
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesTransactionRepositoryImpl
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.data.repository.CardFacilitiesUserRepositoryImpl
import com.drp.data.sharepref.DynamicPreferences
import com.drp.data.sharepref.DynamicPreferencesImpl
import io.element.android.x.di.PREF_KEY_TOKEN
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

    val dynamicPreferences: DynamicPreferences by lazy {
        DynamicPreferencesImpl(sharedPreferences)
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    val dynamicApiCall: DynamicApiCall by lazy {
        DynamicApiCallImpl(apiService)
    }


    val appDatabase: AppDataBase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            Constant.DATABASE_NAME
        ).build()
    }
    val dbImplementation: DataBaseRequest by lazy {
        DataBaseRequestImpl(appDatabase)
    }
    val cardFacilityRepository: CardFacilitiesRepository by lazy {
        CardFacilitiesRepositoryImpl(dynamicApiCall,dbImplementation,dynamicPreferences)
    }

    val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository by lazy {
        CardFacilitiesTransactionRepositoryImpl(dynamicApiCall,dbImplementation)
    }

    val userRepository: CardFacilitiesUserRepository by lazy {
        CardFacilitiesUserRepositoryImpl(dynamicApiCall,dynamicPreferences,dbImplementation)
    }

/*    val cardFacilitiesBillRepository: CardFacilitiesBillRepository by lazy {
        CardFacilitiesBillRepositoryImpl()
    }*/

/*    val cardFacilitiesLoanRepository: CardFacilitiesLoanRepository by lazy {
        CardFacilitiesLoanRepositoryImpl()
    }*/

    val dispatcher: CoroutineDispatcher = Dispatchers.IO
}
