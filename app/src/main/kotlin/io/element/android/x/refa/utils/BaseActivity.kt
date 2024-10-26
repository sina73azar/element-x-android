package com.drp.shared_ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.drp.refah.ui.data.enums.DialogName
import io.element.android.x.refa.utils.ChangeLanguage
import java.util.Locale

typealias ActivityInflater<T> = (LayoutInflater) -> T

open class BaseActivity<VB : ViewBinding>(val inflater: ActivityInflater<VB>) : AppCompatActivity() {
    lateinit var activityResultLauncher:
            ActivityResultLauncher<Intent>
    lateinit var dialog: FailNetworkDialog
    var freezSplash = false
    private var _binding: VB? = null
    val binding get() = _binding!!
    private lateinit var countDownTimer: CountDownTimer
    var DISCONNECT_TIMEOUT: Long = 3 * 60 * 1000 // 3 min = 3 * 60 * 1000 ms
    var requestCode = -1

    companion object {
        var finishPicker: Boolean = false
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ChangeLanguage.wrap(newBase, Locale("fa")))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflater.invoke(layoutInflater)
        setContentView(binding.root)
        setStatusBarGradiant(this)
        dataObserver()
        activityResultLauncher =
            (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (requestCode != -1)
                    activityResult(requestCode, result)
            }
        ChangeLanguage.wrap(applicationContext, Locale("fa"))
//        viewModel.getFontScale()?.let {
//            adjustFontScale(it as Float - 0.2f)
//        }
    }

    private fun adjustFontScale(scale: Float) {
        resources.configuration.fontScale = scale
        val metrics: DisplayMetrics = resources.displayMetrics
        val wm: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val defaultDisplay =
                DisplayManagerCompat.getInstance(applicationContext)
                    .getDisplay(Display.DEFAULT_DISPLAY)
            defaultDisplay?.let { createDisplayContext(it) }
            metrics.scaledDensity = resources.configuration.fontScale * metrics.density
            createConfigurationContext(baseContext.resources.configuration)
        } else {
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = resources.configuration.fontScale * metrics.density
            @Suppress("DEPRECATION")
            baseContext.resources.updateConfiguration(resources.configuration, metrics)
        }

    }

    fun showNextDialog(name: DialogName) {
        when (name) {
            DialogName.NETWORK_ERROR -> {
                FailNetworkDialog().show(supportFragmentManager, "ntwork_dialog")
            }
        }
    }


//    override fun onResume() {
//        super.onResume()
//        showVpnFragment(getConnectionType(this@BaseActivity) == 3)
//        if (!BuildConfig.DEBUG) {
//            var emulator = false
//            EmulatorDetector.with(this)
//                ?.setCheckTelephony(true)
//                ?.setDebug(true)
//                ?.detect { isEmulator -> emulator = isEmulator }
//            val isRoot = RootBeer(this).isRooted
//            val detectDebugger = Debug.isDebuggerConnected()
//            val hookingDetecting = useHooking()
//            when {
//                emulator -> {
//                    val dialog = FailureDialog {
//                        finishAffinity()
//                    }
//                    dialog.show(
//                        supportFragmentManager,
//                        getString(R.string.failed_title),
//                        getString(R.string.failure_simulator_issue)
//                    )
//                    freezSplash = true
//                }
//
//                isRoot -> {
//                    val dialog = FailureDialog {
//                        finishAffinity()
//
//                    }
//                    dialog.show(
//                        supportFragmentManager,
//                        getString(R.string.failed_title),
//                        getString(R.string.failure_root_issue)
//                    )
//                    freezSplash = true
//                }
//
//                hookingDetecting -> {
//                    val dialog = FailureDialog {
//                        finishAffinity()
//
//                    }
//                    dialog.show(
//                        supportFragmentManager,
//                        getString(R.string.failed_title),
//                        getString(R.string.failure_hook_issue)
//                    )
//                    freezSplash = true
//
//                }
//
//                detectDebugger -> {
//                    val dialog = FailureDialog {
//                        finishAffinity()
//                    }
//                    dialog.show(
//                        supportFragmentManager,
//                        getString(R.string.failed_title),
//                        getString(R.string.failure_debug_issue)
//                    )
//                    freezSplash = true
//                }
//
//                else -> {
//                    //Nohing
//                }
//            }
//            if (BuildConfig.BUILD_TYPE == "release")
//                window.setFlags(
//                    WindowManager.LayoutParams.FLAG_SECURE,
//                    WindowManager.LayoutParams.FLAG_SECURE
//                )
//        }
//    }

    open fun dataObserver() {
        // function for observing data inside activities
    }

    protected open fun activityResult(requestCode: Int, result: ActivityResult) {
        //I used as abstract method because i don't want to implement in
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

//    private fun showVpnFragment(isShow: Boolean) {
//        lifecycleScope.launchWhenResumed {
//            if (isShow) {
//                this@BaseActivity.instanceBottomSheet(
//                    VpnFragment(),
//                    VpnFragment.TAG,
//                    isCancelable = false
//                )
//            } else {
//                val fragment = supportFragmentManager.findFragmentByTag(VpnFragment.TAG)
//                if (fragment != null)
//                    (fragment as BottomSheetDialogFragment).dismiss()
//            }
//        }
//    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }



}
@SuppressLint("ObsoleteSdkInt")
fun setStatusBarGradiant(activity: Activity, color: String? = "#4562ab") {
    val window = activity.window
    val view = window.decorView
    if (color.equals("#ffffff"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val insetController = WindowInsetsControllerCompat(window, view)
            insetController.isAppearanceLightStatusBars = true
        }
    window.statusBarColor = Color.parseColor(color)
}

@SuppressLint("ObsoleteSdkInt")
@Suppress("DEPRECATION")
@IntRange(from = 0, to = 3)
fun getConnectionType(context: Context): Int {
    var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: vpn
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_VPN) {
                    result = 3
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = 1
                } else if (type == ConnectivityManager.TYPE_WIFI) {
                    result = 2
                }
            }
        }
    }
    return result
}

fun useHooking(): Boolean {
    try {
        throw Exception()
    } catch (e: Exception) {
        var zygoteInitCallCount = 0
        var isHookDetecting = false
        for (stackTraceElement in e.stackTrace) {
            if (stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
                zygoteInitCallCount++
                if (zygoteInitCallCount == 2) {
                    isHookDetecting = true
                }
            }
            if (stackTraceElement.className == "com.saurik.substrate.MS$2" && stackTraceElement.methodName == "invoked") {
                isHookDetecting = true
            }
            if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "main") {
                isHookDetecting = true
            }
            if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "handleHookedMethod") {
                isHookDetecting = true
            }
        }
        return isHookDetecting
    }
}





