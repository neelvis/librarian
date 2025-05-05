package ru.neelvis.librarian.common.utils

import android.view.Surface

fun Int.toSurfaceRotation(): Int = when (this) {
    in 45 until 135 -> Surface.ROTATION_270
    in 135 until 225 -> Surface.ROTATION_180
    in 225 until 315 -> Surface.ROTATION_90
    else -> Surface.ROTATION_0
}
