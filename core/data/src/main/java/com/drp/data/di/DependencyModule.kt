package com.drp.data.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.drp.data.BuildConfig
import com.drp.data.database.AppDataBase
import com.drp.data.database.Constant
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.database.impl.DataBaseRequestImpl
import com.drp.data.network.ApiService
import com.drp.data.network.OAuthInterceptor
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.data.network.api_call.DynamicApiCallImpl
import com.drp.data.repository.CardFacilitiesBillRepository
import com.drp.data.repository.CardFacilitiesBillRepositoryImpl
import com.drp.data.repository.CardFacilitiesLoanRepository
import com.drp.data.repository.CardFacilitiesLoanRepositoryImpl
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesRepositoryImpl
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesTransactionRepositoryImpl
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.data.repository.CardFacilitiesUserRepositoryImpl
import com.drp.data.repository.ShahkarLoginRepository
import com.drp.data.repository.ShahkarLoginRepositoryImpl
import com.drp.data.sharepref.DynamicPreferences
import com.drp.data.sharepref.DynamicPreferencesImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * this class is used for provide all repositories that used by use-cases in our Application
 * in this class we provide Repository
 * @see Repository
 * @see RepositoryImpl for implementation
 */
private const val PREF_KEY_TOKEN: String = "PREF_KEY_ACCESS_TOKEN"

@Module
@InstallIn(SingletonComponent::class)
object DependencyModule {

    @Provides
    fun provideBaseUrl(): String = "https://refalanddev-gateway.daneshrefah.ir/"


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

    @Singleton
    @Provides
    fun provideOAuthInterceptor(
        @ApplicationContext context: Context,
        dynamicPreferences: DynamicPreferences
    ): Interceptor {
        return OAuthInterceptor(context, dynamicPreferences)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun dynamicApiCall(apiService: ApiService): DynamicApiCall = DynamicApiCallImpl(apiService)


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


    @Provides
    @Singleton
    fun provideDbImplementation(dataBaseRequestImpl: DataBaseRequestImpl): DataBaseRequest =
        dataBaseRequestImpl

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

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !BuildConfig.DEBUG) {
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
        } else {
            context.getSharedPreferences(PREF_KEY_TOKEN, Context.MODE_PRIVATE)
        }
    }

    @Provides
    @Singleton
    fun provideDynamicPreferences(sharedPreferences: SharedPreferences): DynamicPreferences =
        DynamicPreferencesImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}