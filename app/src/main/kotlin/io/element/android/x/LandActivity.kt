/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Button

class LandActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ElementTheme {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Button(text = "go to element", onClick = {
                        val intent = Intent(this@LandActivity, io.element.android.x.MainActivity::class.java)
                        startActivity(intent)
                    })
                }

            }
        }
    }
}
