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

@Module
@InstallIn(SingletonComponent::class)
object DependencyModule {

    @Provides
    fun provideBaseUrl(): String = "https://refaland-gateway.daneshrefah.ir/"

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        @ApplicationContext context: Context,
        oAuthInterceptor: Interceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
        okHttpClient.readTimeout(15, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(15, TimeUnit.SECONDS)
        okHttpClient.retryOnConnectionFailure(true)
        okHttpClient.addNetworkInterceptor(logging)
        okHttpClient.addInterceptor(oAuthInterceptor)
        return okHttpClient.build()
    }

    /*    @Singleton
        @Provides
        fun provideOAuthInterceptor(
            @ApplicationContext context: Context,
            dynamicPreferences: DynamicPreferences
        ): Interceptor {
            return OAuthInterceptor(context, dynamicPreferences)
        }*/

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /*
        @Provides
        @Singleton
        fun provideApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
    */

    /*
        @Provides
        @Singleton
        fun dynamicApiCall(apiService: ApiService): DynamicApiCall = DynamicApiCallImpl(apiService)
    */

    /*

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase {
            return Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                Constant.DATABASE_NAME
            )
                .build()
        }
    */

    @Provides
    @Singleton
    fun provideDbImplementation(dataBaseRequestImpl: DataBaseRequestImpl): DataBaseRequest =
        dataBaseRequestImpl

    /*
        @Provides
        @Singleton
        fun provideCardFacilityRepository(
            cardFacilitiesRepositoryImpl: CardFacilitiesRepositoryImpl
        ): CardFacilitiesRepository = cardFacilitiesRepositoryImpl

        @Provides
        @Singleton
        fun provideCardFacilitiesTransactionRepository(
            cardFacilitiesTransactionRepositoryImpl: CardFacilitiesTransactionRepositoryImpl
        ): CardFacilitiesTransactionRepository = cardFacilitiesTransactionRepositoryImpl

        @Provides
        @Singleton
        fun provideUserRepository(
            cardFacilitiesUserRepositoryImpl: CardFacilitiesUserRepositoryImpl
        ): CardFacilitiesUserRepository =
            cardFacilitiesUserRepositoryImpl

        @Provides
        @Singleton
        fun provideCardFacilitiesBillRepository(
            cardFacilitiesBillRepositoryImpl: CardFacilitiesBillRepositoryImpl
        ): CardFacilitiesBillRepository = cardFacilitiesBillRepositoryImpl

        @Provides
        @Singleton
        fun provideCardFacilitiesLoanRepository(
            cardFacilitiesRepositoryImpl: CardFacilitiesLoanRepositoryImpl
        ): CardFacilitiesLoanRepository = cardFacilitiesRepositoryImpl

        @Provides
        @Singleton
        fun provideLoginRepository(
            shahkarLoginRepositoryImpl: ShahkarLoginRepositoryImpl
        ): ShahkarLoginRepository = shahkarLoginRepositoryImpl
    */

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_KEY_TOKEN, Context.MODE_PRIVATE) /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !BuildConfig.DEBUG) {
            val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREF_KEY_TOKEN,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {*/

//        }
    }

    /*
        @Provides
        @Singleton
        fun provideDynamicPreferences(sharedPreferences: SharedPreferences): DynamicPreferences =
            DynamicPreferencesImpl(sharedPreferences)
    */

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
