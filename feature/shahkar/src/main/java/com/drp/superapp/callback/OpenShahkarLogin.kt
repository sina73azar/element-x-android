package com.drp.superapp.callback

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.drp.data.model.ShahkarUserData
import com.drp.data.repository.ShahkarLoginRepository
import com.drp.superapp.shahkar.ShahkarLoginActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Job

open class BaseClass {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LoginRepositoryProvider {
        var shahkarLoginRepository: ShahkarLoginRepository
    }
}

object OpenShahkarLogin : BaseClass() {
    private lateinit var userData: ShahkarUserData
    private var job: Job? = null

    interface ExposeShahkarLoginState {
        fun onLoading()
        fun onSuccess(shahkarUserData: ShahkarUserData)
        fun onError(message: String)
        fun onFail()
    }

    private var exposeShahkarLoginState: ExposeShahkarLoginState? = null

    private lateinit var shahkarLoginRepository: ShahkarLoginRepository

    fun init(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            LoginRepositoryProvider::class.java
        )
        shahkarLoginRepository = entryPoint.shahkarLoginRepository
        userData = shahkarLoginRepository.loadShahkarUserData()
    }

    @SuppressLint("SuspiciousIndentation")
    fun openShahkarLogin(
        context: Context,
        exposeShahkarLoginState: ExposeShahkarLoginState,
    ) {
        init(context.applicationContext)
        this.exposeShahkarLoginState = exposeShahkarLoginState
        exposeShahkarLoginState.onLoading()
        if (::userData.isInitialized && userData.walletId != null)
            this.exposeShahkarLoginState?.onSuccess(userData)

        /*if (::userData.isInitialized && !userData.accessToken.isNullOrEmpty()) {
            userData.expireDateInMillis?.let { expireDateInMillis ->
                // TODO change side
                if (Calendar.getInstance().timeInMillis < expireDateInMillis) {
                    this.exposeShahkarLoginState?.onSuccess(userData)
                } else {
                    job = CoroutineScope(context = Dispatchers.IO).launch {
                        val request =
                            ShahkarRefreshTokenRequest(refreshToken = userData.refreshToken!!)
                        val headers = mapOf("AccessParameter" to userData.phoneNumber!!)
                        shahkarLoginRepository.refreshToken(request, headers)
                            .collectLatest { response ->
                                when (response.status) {
                                    CustomResponse.Status.SUCCESS -> {
                                        response.data?.let { result ->
                                            val newUserData = userData.copy(
                                                accessToken = result.accessToken,
                                                refreshToken = result.refreshToken,
                                                expireDateInMillis = (result.iat + result.expiresIn) * 1000
                                            )
                                            withContext(Dispatchers.Main) {
                                                this@OpenShahkarLogin.exposeShahkarLoginState?.onSuccess(
                                                    shahkarUserData = newUserData
                                                )
                                            }
                                            setShahkarUserData(newUserData)
                                            clearJob()
                                        }
                                    }

                                    CustomResponse.Status.ERROR -> {
                                        response.message?.let {
                                            withContext(Dispatchers.Main) {
                                                this@OpenShahkarLogin.exposeShahkarLoginState?.onError(
                                                    it
                                                )
                                            }
                                        }
                                        clearJob()
                                        logout()
                                        openShahkarActivity(context)
                                    }

                                    CustomResponse.Status.Fail -> {
                                        withContext(Dispatchers.Main) {
                                            this@OpenShahkarLogin.exposeShahkarLoginState?.onFail()
                                        }
                                        clearJob()
                                    }

                                    else -> {
                                        // do nothing
                                    }
                                }
                            }
                    }

                }
            }
                ?: this@OpenShahkarLogin.exposeShahkarLoginState?.onError(context.getString(R.string.unknown_error_st))
        }*/ else {
            openShahkarActivity(context)
        }

    }

    internal fun getExposeShahkarLoginState() = this.exposeShahkarLoginState

    private fun clearJob() {
        if (job != null)
            job = null
    }

    private fun openShahkarActivity(context: Context) {
        val intent = Intent(context, ShahkarLoginActivity::class.java)
        context.startActivity(intent)
    }

    internal fun setShahkarUserData(shahkarUserData: ShahkarUserData) {
        shahkarLoginRepository.saveShahkarUserData(shahkarUserData)
    }

    fun logout() {
        shahkarLoginRepository.clearShahkarUserData()
    }
}