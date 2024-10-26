package com.drp.utils
import io.element.android.x.BuildConfig
import timber.log.Timber


inline fun <reified T : Any> T.logger(tag: String = "REFAHLAND_LOGGER", msg: String? = "") {
    if (BuildConfig.DEBUG) {
        Timber.tag(tag).d(
            """
                ${this::class.simpleName}: -------------------->>>
                $msg
            """.trimIndent()
        )
    }
}
