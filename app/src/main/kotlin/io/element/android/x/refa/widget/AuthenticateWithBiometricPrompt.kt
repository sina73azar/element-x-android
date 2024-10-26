package com.drp.shared_ui.widget

/*
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import io.element.android.x.R

@SuppressLint("WrongConstant")
@Composable
fun AuthenticateWithBiometricPrompt(
    context: Context,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val fragmentActivity = context as FragmentActivity
    val executor = context.mainExecutor
    val biometricPrompt = BiometricPrompt(
        fragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError()
            }

            override fun onAuthenticationFailed() {
                onError()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(stringResource(id = R.string.biometric_auth_title))
        .setDescription(stringResource(id = R.string.biometric_auth_desc))
        .setNegativeButtonText(stringResource(id = R.string.cancel))
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@SuppressLint("WrongConstant")
fun canAuthenticateWithBiometric(context: Context): Boolean {
    val fragmentActivity = context as FragmentActivity
    val biometricManager = BiometricManager.from(fragmentActivity)
    return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> {
            false
        }
    }
}
*/
