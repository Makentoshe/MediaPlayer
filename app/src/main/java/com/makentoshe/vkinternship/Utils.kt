package com.makentoshe.vkinternship

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

fun Context.dip(dp: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()
