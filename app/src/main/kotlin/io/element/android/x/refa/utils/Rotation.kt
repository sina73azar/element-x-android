package com.drp.shared_ui

import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation

fun rotateArrow(arrowImageView: View, fromDegree: Float, toDegree: Float, time: Long = 300) {
    val animation: Animation = RotateAnimation(
        fromDegree,
        toDegree,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    animation.interpolator = DecelerateInterpolator()
    animation.repeatCount = 0
    animation.fillAfter = true
    animation.duration = time
    arrowImageView.startAnimation(animation)
}