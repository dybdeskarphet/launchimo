package com.ahmetardakavakci.launchimo.model

import android.content.Intent
import android.graphics.drawable.Drawable

data class App(
    val label: String,
    val pkgName: String,
    val icon: Drawable,
    val intent: Intent
)